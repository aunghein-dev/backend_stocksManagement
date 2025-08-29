package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.StkGroup;
import com.aunghein.SpringTemplate.model.StkItem;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.repository.StkItemRepo;
import com.aunghein.SpringTemplate.repository.StkRepo;
import com.aunghein.SpringTemplate.service.minio.MinioService;
import com.aunghein.SpringTemplate.utils.FileStorageManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StkService{

    private static final Logger log = LoggerFactory.getLogger(StkService.class);
    private final StkRepo stkRepo;
    private final BusinessRepo businessRepo;
    private final SupabaseService supabaseService;
    private final StkItemRepo stkItemRepo;
    private final MinioService minioService;

    // ✅ GET with items (default if using JPA fetch type LAZY unless overridden in repo)
    public List<StkGroup> getStkGroupByBusinessId(Long bizId) {
        return stkRepo.findStkGroupByBusinessId(bizId);
    }

    public List<StkGroup> getStkGroupByBizNonZeroItems(Long bizId) {
        List<StkGroup> groups = stkRepo.findStkGroupByBusinessId(bizId);

        // Split into two lists: one with non-empty items, one with empty items (after filtering)
        List<StkGroup> nonEmptyGroups = new ArrayList<>();
        List<StkGroup> emptyGroups = new ArrayList<>();

        for (StkGroup group : groups) {
            List<StkItem> filteredItems = group.getItems().stream()
                    .filter(item -> item.getItemQuantity() > 0)
                    .sorted(Comparator.comparingInt(StkItem::getItemQuantity).reversed())
                    .collect(Collectors.toList());

            group.setItems(filteredItems);

            if (filteredItems.isEmpty()) {
                emptyGroups.add(group); // group now has no items
            } else {
                nonEmptyGroups.add(group); // group still has items
            }
        }

        // Combine non-empty groups first, then empty groups at the end
        nonEmptyGroups.addAll(emptyGroups);
        return nonEmptyGroups;
    }



    // Pagination
    public Page<StkGroup> getStkGroupsByBusinessId(Long bizId, Pageable pageable) {
        return stkRepo.findByBusinessId(bizId, pageable);
    }

    // ✅ CREATE group and child items
    @Transactional
    public StkGroup createStkGroupByBusinessId(Long bizId,
                                               StkGroup stkGroup,
                                               MultipartFile groupImage,
                                               List<MultipartFile> itemImages) {
        Business business = businessRepo.findById(bizId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        stkGroup.setBusiness(business);

        String url = "https://svmeynesalueoxzhtdqp.supabase.co/storage/";
        try {
            url = supabaseService.uploadGroupImage(groupImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        //UPLOAD
        String url2 = FileStorageManager.FILE_STORAGE;
        try {
            url2 = minioService.uploadFile(groupImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        stkGroup.setGroupImage(url);

        // ✅ Link each child item to the parent group
        if (!stkGroup.isColorless()) {
            IntStream.range(0, stkGroup.getItems().size()).forEach(i -> {
                StkItem item = stkGroup.getItems().get(i);
                item.setStkGroup(stkGroup);

                if (itemImages != null && i < itemImages.size()) {
                    MultipartFile file = itemImages.get(i);
                    String itemImageUrl = uploadItemImagesSafe(file);
                    item.setItemImage(itemImageUrl);
                } else {
                    item.setItemImage(null); // or default placeholder
                }
            });
        } else {
            // Colorless: skip uploading item images
            stkGroup.getItems().forEach(item -> {
                item.setStkGroup(stkGroup);
                item.setItemImage(null);
            });
        }

        return stkRepo.save(stkGroup);
    }

    // ✅ DELETE group and all its items (orphanRemoval = true handles child deletion)
    @Transactional
    public void deleteStkGroupByBusinessIdWithGroupId(Long bizId, Long groupId) {
        StkGroup group = stkRepo.findById(groupId)
                .filter(g -> g.getBusiness().getBusinessId().equals(bizId))
                .orElseThrow(() -> new NoResourceFoundException("Group not found or does not belong to the business"));

        try {
            if (group.getGroupImage() != null && group.getGroupImage().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                supabaseService.deleteFile(group.getGroupImage());
            }

            /*
            //DELETE
            if (group.getGroupImage() != null && group.getGroupImage().contains("file.openwaremyanmar")) {
                minioService.deleteFile(group.getGroupImage());
            }
            */

            for (StkItem item : group.getItems()) {
                if (item.getItemImage() != null && item.getItemImage().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                    supabaseService.deleteFile(item.getItemImage());
                }

                /*
                //DELETE
                if (item.getItemImage() != null && item.getItemImage().contains("file.openwaremyanmar")) {
                    minioService.deleteFile(item.getItemImage());
                }
                */
            }

            stkRepo.delete(group);

        } catch (Exception e) {
            throw new NoTransactionException("Failed to delete associated files: " + e.getMessage());
        }
    }

    @Transactional
    public StkGroup editByBusiness_BusinessIdWithGroupId(
            Long groupId,
            StkGroup updatedGroupData,
            MultipartFile groupImage,
            // CHANGE 1: Receive all item images as a map or individual files,
            // assuming your frontend sends them with distinct names like "itemImage_ID.jpg" or "itemImage_new_INDEX.jpg"
            // For simplicity, let's process the raw MultipartHttpServletRequest or iterate over parameter names.
            // A more Spring-friendly way is to adjust the DTO or use @RequestParam Map<String, MultipartFile>
            // But given your frontend sends "itemImages", we'll iterate the request.
            // For this example, let's assume `itemImages` passed here will contain ALL files,
            // and we'll map them by their original filenames if possible or rely on the frontend's naming convention.
            // **Best practice is to change the controller to accept Map<String, MultipartFile> if possible.**
            // For now, let's adapt to your existing `List<MultipartFile>`.
            List<MultipartFile> itemImages // Keep this for now, but internally map by name
    ) {
        // 1) Fetch existing group
        StkGroup existingGroup = stkRepo.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // 2) Update scalar fields
        existingGroup.setGroupName(updatedGroupData.getGroupName());
        existingGroup.setGroupUnitPrice(updatedGroupData.getGroupUnitPrice());
        existingGroup.setReleasedDate(updatedGroupData.getReleasedDate());
        existingGroup.setGroupOriginalPrice(updatedGroupData.getGroupOriginalPrice());

        // 3) Replace group image (delete old one first)
        if (groupImage != null && !groupImage.isEmpty()) {
            String oldGroupImage = existingGroup.getGroupImage();
            if (oldGroupImage != null) {
                supabaseService.deleteFile(oldGroupImage);
            }

            /*
            //DELETE
            if (oldGroupImage != null) {
                try {
                    minioService.deleteFile(oldGroupImage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            */

            try {
                String newGroupImageUrl = supabaseService.uploadGroupImage(groupImage);
                /*
                //UPLOAD
                String newGroupImageUrl = minioService.uploadFile(groupImage);
                */
                existingGroup.setGroupImage(newGroupImageUrl);

            } catch (Exception e) {
                throw new RuntimeException("Failed to upload group image", e);
            }
        }

        // 4) Handle items
        List<StkItem> updatedItems = updatedGroupData.getItems() != null
                ? updatedGroupData.getItems()
                : Collections.emptyList();
        List<StkItem> existingItems = new ArrayList<>(existingGroup.getItems());

        // NEW LOGIC: Map incoming item images by the unique part of their filename
        // Assuming frontend sends filenames like "itemImage_NEW_0.jpg", "itemImage_123.jpg" etc.
        // The name you append as "itemImage_${item.itemId || 'new_' + index}.jpg" is the crucial part.
        Map<String, MultipartFile> incomingItemImageMap = new HashMap<>();
        if (itemImages != null) {
            for (MultipartFile file : itemImages) {
                // Use getOriginalFilename() if the frontend sets it appropriately,
                // otherwise, you need to find a way to map it.
                // Given your frontend, `getOriginalFilename()` likely contains the full name
                // you set, e.g., "itemImage_123.jpg" or "itemImage_new_0.jpg".
                if (file.getOriginalFilename() != null && !file.isEmpty()) {
                    incomingItemImageMap.put(file.getOriginalFilename(), file);
                }
            }
        }


        Map<Long, StkItem> updatedById = updatedItems.stream()
                .filter(item -> item.getItemId() != null)
                .collect(Collectors.toMap(StkItem::getItemId, item -> item));

        // 4a) Delete old items not present in updated list
        // Use a temporary list to avoid ConcurrentModificationException
        List<StkItem> itemsToRemove = new ArrayList<>();
        for (StkItem oldItem : existingItems) {
            if (oldItem.getItemId() == null || !updatedById.containsKey(oldItem.getItemId())) {
                // This item is no longer in the updated list
                if (oldItem.getItemImage() != null) {
                    supabaseService.deleteFile(oldItem.getItemImage());
                }

                /*
                //DELETE
                if (oldItem.getItemImage() !=null) {
                    try {
                        minioService.deleteFile(oldItem.getItemImage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                */

                itemsToRemove.add(oldItem);
            }
        }
        existingItems.removeAll(itemsToRemove);
        existingGroup.getItems().removeAll(itemsToRemove); // Ensure collection is updated

        // 4b) Add/update items
        for (int i = 0; i < updatedItems.size(); i++) {
            StkItem updatedItem = updatedItems.get(i);
            Long id = updatedItem.getItemId();

            // Determine the expected filename for the image for this specific item
            // This MUST match how your frontend names the files when appending to FormData.
            String expectedImageFileName = updatedItem.getItemId() != null
                    ? "itemImage_" + updatedItem.getItemId() + ".jpg" // For existing items
                    : "itemImage_new_" + i + ".jpg"; // For new items (using index from frontend)

            MultipartFile itemImageFile = incomingItemImageMap.get(expectedImageFileName);
            boolean hasNewImage = itemImageFile != null && !itemImageFile.isEmpty();


            if (id != null) {
                // Update existing item
                StkItem target = existingItems.stream()
                        .filter(e -> id.equals(e.getItemId()))
                        .findFirst()
                        .orElse(null);

                if (target != null) {
                    target.setItemQuantity(updatedItem.getItemQuantity());
                    target.setItemColorHex(updatedItem.getItemColorHex());
                    target.setBarcodeNo(updatedItem.getBarcodeNo());

                    // Only update image if a NEW image file is provided for this specific item
                    if (hasNewImage) {
                        if (target.getItemImage() != null) {
                            supabaseService.deleteFile(target.getItemImage());
                        }

                        /*
                        //DELETE
                        if (target.getItemImage() != null) {
                            try {
                                minioService.deleteFile(target.getItemImage());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        */

                        try {
                            String newItemImageUrl = supabaseService.uploadItemImage(itemImageFile);

                            /*
                            //UPLOAD
                            String newItemImageUrl = minioService.uploadFile(itemImageFile);
                            */
                            target.setItemImage(newItemImageUrl);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to upload item image for existing item " + id, e);
                        }
                    }
                    // If itemImageFile is null/empty AND item.itemImage is null, it means the image was removed by frontend.
                    // In your frontend, if `_tempFile` is null and `itemImage` becomes null, it signifies removal.
                    // Your current frontend doesn't explicitly send a "delete image" signal for an existing image if
                    // `_tempFile` is null and `itemImage` is also null for an existing item.
                    // If `item.itemImage` from `updatedGroupData` is null, and `hasNewImage` is false, it implies removal.
                    // Your frontend logic for `itemsPayload` only sends `item.itemImage` if `_isExistingImage` is true and `item.itemImage` exists.
                    // This means if `_tempFile` is null and `item.itemImage` is null, `itemImage` will be undefined in the payload.
                    // So, if `item.itemImage` is missing from the payload for an existing item, it means it was removed.
                    if (!hasNewImage && updatedItem.getItemImage() == null && target.getItemImage() != null) {
                        supabaseService.deleteFile(target.getItemImage());
                        target.setItemImage(null);
                    }

                    /*
                    //DELETE
                    if (!hasNewImage && updatedItem.getItemImage() == null && target.getItemImage() != null) {
                        try {
                            minioService.deleteFile(target.getItemImage());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        target.setItemImage(null);
                    }
                   */
                }
            } else {
                // Add new item
                StkItem newItem = new StkItem();
                newItem.setItemQuantity(updatedItem.getItemQuantity());
                newItem.setItemColorHex(updatedItem.getItemColorHex());
                newItem.setBarcodeNo(updatedItem.getBarcodeNo());
                newItem.setStkGroup(existingGroup); // Link to the group

                // Only upload image if a new image file is provided for this specific new item
                if (hasNewImage) {
                    try {
                        String newItemImageUrl = supabaseService.uploadItemImage(itemImageFile);
                        newItem.setItemImage(newItemImageUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload new item image for new item " + i, e);
                    }
                }

                /*
                //UPLOAD
                if (hasNewImage) {
                    try {
                        String newItemImageUrl = minioService.uploadFile(itemImageFile);
                        newItem.setItemImage(newItemImageUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload new item image for new item " + i, e);
                    }
                }
                */

                existingGroup.addItem(newItem); // Add to the group's collection
            }
        }

        // 5) Save and return
        return stkRepo.save(existingGroup);
    }




    @Transactional
    private String uploadItemImagesSafe(MultipartFile file) {
        try {
            return supabaseService.uploadItemImage(file);
        } catch (Exception e) {
            e.printStackTrace(); // Log appropriately
            return null;
        }

        /*
        //UPLOAD
        try {
            return minioService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace(); // Log appropriately
            return null;
        }
        */

    }


    public Optional<StkGroup> getStkGroupByGroupId(Long groupId, Long bizId) {
        return stkRepo.findStkGroupByBusinessId(bizId)
                 .stream()
                 .filter(group-> group.getGroupId().equals(groupId)).findFirst();
    }

    @Transactional
    public StkItem deleteItemOnlyByItemId(Long itemId, Long groupId) {
        // 1. Find the item to delete
        StkItem toDeleteItem = stkItemRepo.findById(itemId)
                .orElseThrow(() -> new NoResourceFoundException("Item not found with ID: " + itemId));

        // 2. Get current items for the group (BEFORE deleting the item)
        // This is crucial to check if the current item is the *last* one.
        List<StkItem> currentItemListForGroup = stkItemRepo.findItemsByGroupId(groupId);

        // 3. Handle image deletion (Supabase) - Keep it as is for now,
        //    but remember asynchronous is better for performance if feasible.
        if (toDeleteItem.getItemImage() != null && toDeleteItem.getItemImage().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
            try {
                supabaseService.deleteFile(toDeleteItem.getItemImage());
                log.info("Successfully triggered deletion for image: {}", toDeleteItem.getItemImage());
            } catch (Exception e) {
                log.error("Failed to delete image from Supabase: {}", toDeleteItem.getItemImage(), e);
            }
        } else {
            log.info("Item {} does not have a Supabase uploaded image or image URL is null.", itemId);
        }

        /*
        //DELETE
        if (toDeleteItem.getItemImage() != null && toDeleteItem.getItemImage().contains("file.openwaremyanmar")) {
            try {
                minioService.deleteFile(toDeleteItem.getItemImage());
                log.info("Successfully triggered deletion for image: {}", toDeleteItem.getItemImage());
            } catch (Exception e) {
                log.error("Failed to delete image from Supabase: {}", toDeleteItem.getItemImage(), e);
            }
        } else {
            log.info("Item {} does not have a Supabase uploaded image or image URL is null.", itemId);
        }
        */

        // NEW LOGIC: Check if this is the last item in the group
        // This check must happen *before* deleting toDeleteItem from the DB.
        if (currentItemListForGroup.size() == 1) { // If there's only one item currently in the list
            log.info("Item {} is the last item in group {}. Deleting the group.", itemId, groupId);
            // Find and delete the group
            StkGroup groupToDelete = stkRepo.findById(groupId)
                    .orElseThrow(() -> new NoResourceFoundException("Group not found with ID: " + groupId));
            String toDeleteImgUrl = groupToDelete.getGroupImage();
            // **FIX:** Add null check for toDeleteImgUrl
            if (toDeleteImgUrl != null && toDeleteImgUrl.startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                try {
                    supabaseService.deleteFile(toDeleteItem.getItemImage());
                    log.info("Successfully triggered deletion for image: {}", toDeleteItem.getItemImage());
                } catch (Exception e) {
                    log.error("Failed to delete image from Supabase: {}", toDeleteItem.getItemImage(), e);
                }

            } else {
                log.info("Group {} does not have a Supabase uploaded image or image URL is null.", groupId);
            }

            /*
            //DELETE
            if (toDeleteImgUrl != null && toDeleteImgUrl.contains("file.openwaremyanmar")) {
                try {
                    minioService.deleteFile(toDeleteItem.getItemImage());
                    log.info("Successfully triggered deletion for image: {}", toDeleteItem.getItemImage());
                } catch (Exception e) {
                    log.error("Failed to delete image from Supabase: {}", toDeleteItem.getItemImage(), e);
                }

            } else {
                log.info("Group {} does not have a Supabase uploaded image or image URL is null.", groupId);
            }
            */
            stkRepo.delete(groupToDelete);
        }

        // 4. Delete the item itself
        // If the group was deleted in the step above, this might be implicitly handled by cascade on the group.
        // However, for clarity and to ensure the item is definitely removed,
        // it's fine to keep it here if the group's cascade doesn't implicitly delete items.
        stkItemRepo.delete(toDeleteItem);

        return toDeleteItem;
    }

}
