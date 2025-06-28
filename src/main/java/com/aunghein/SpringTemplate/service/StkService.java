package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.Business;
import com.aunghein.SpringTemplate.model.StkGroup;
import com.aunghein.SpringTemplate.model.StkItem;
import com.aunghein.SpringTemplate.repository.BusinessRepo;
import com.aunghein.SpringTemplate.repository.StkItemRepo;
import com.aunghein.SpringTemplate.repository.StkRepo;
import jakarta.persistence.EntityNotFoundException;
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
public class StkService{

    private static final Logger log = LoggerFactory.getLogger(StkService.class);

    @Autowired
    private StkRepo stkRepo;

    @Autowired
    private BusinessRepo businessRepo;

    @Autowired
    private SupabaseService supabaseService;

    @Autowired
    private StkItemRepo stkItemRepo;

    // ✅ GET with items (default if using JPA fetch type LAZY unless overridden in repo)
    public List<StkGroup> getStkGroupByBusinessId(Long bizId) {
        return stkRepo.findStkGroupByBusinessId(bizId);
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

        String url = null;
        try {
            url = supabaseService.uploadGroupImage(groupImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stkGroup.setGroupImage(url);

        // ✅ Link each child item to the parent group
        IntStream.range(0, stkGroup.getItems().size()).parallel().forEach(i -> {
            StkItem item = stkGroup.getItems().get(i);
            item.setStkGroup(stkGroup);

            String itemImageUrl = null;
            if (itemImages != null && i < itemImages.size()) {
                MultipartFile file = itemImages.get(i);
                itemImageUrl = uploadItemImagesSafe(file);
            }

            item.setItemImage(itemImageUrl);
        });
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

            for (StkItem item : group.getItems()) {
                if (item.getItemImage() != null && item.getItemImage().startsWith("https://svmeynesalueoxzhtdqp.supabase.co")) {
                    supabaseService.deleteFile(item.getItemImage());
                }
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
        List<MultipartFile> itemImages
) {
    // 1) Fetch existing group
    StkGroup existingGroup = stkRepo.findById(groupId)
            .orElseThrow(() -> new EntityNotFoundException("Group not found"));

    // 2) Update scalar fields
    existingGroup.setGroupName(updatedGroupData.getGroupName());
    existingGroup.setGroupUnitPrice(updatedGroupData.getGroupUnitPrice());
    existingGroup.setReleasedDate(updatedGroupData.getReleasedDate());

    // 3) Replace group image (delete old one first)
    if (groupImage != null && !groupImage.isEmpty()) {
        String oldGroupImage = existingGroup.getGroupImage();
        if (oldGroupImage != null) {
            supabaseService.deleteFile(oldGroupImage);
        }
        try {
            String newGroupImageUrl = supabaseService.uploadGroupImage(groupImage);
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

    Map<Long, StkItem> updatedById = updatedItems.stream()
            .filter(item -> item.getItemId() != null)
            .collect(Collectors.toMap(StkItem::getItemId, item -> item));

    // 4a) Delete old items not present in updated list
    Iterator<StkItem> iter = existingItems.iterator();
    while (iter.hasNext()) {
        StkItem oldItem = iter.next();
        if (oldItem.getItemId() == null || !updatedById.containsKey(oldItem.getItemId())) {
            if (oldItem.getItemImage() != null) {
                supabaseService.deleteFile(oldItem.getItemImage());
            }
            iter.remove();
            existingGroup.removeItem(oldItem);
        }
    }

    // 4b) Add/update items
    for (int i = 0; i < updatedItems.size(); i++) {
        StkItem updatedItem = updatedItems.get(i);
        Long id = updatedItem.getItemId();

        if (id != null) {
            // Update existing item
            StkItem target = existingItems.stream()
                    .filter(e -> id.equals(e.getItemId()))
                    .findFirst()
                    .orElse(null);
            if (target != null) {
                target.setItemQuantity(updatedItem.getItemQuantity());
                target.setItemColorHex(updatedItem.getItemColorHex());

                if (itemImages != null && i < itemImages.size() && !itemImages.get(i).isEmpty()) {
                    if (target.getItemImage() != null) {
                        supabaseService.deleteFile(target.getItemImage());
                    }
                    try {
                        String newItemImageUrl = supabaseService.uploadItemImage(itemImages.get(i));
                        target.setItemImage(newItemImageUrl);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to upload item image", e);
                    }
                }
            }
        } else {
            // Add new item
            StkItem newItem = new StkItem();
            newItem.setItemQuantity(updatedItem.getItemQuantity());
            newItem.setItemColorHex(updatedItem.getItemColorHex());
            newItem.setStkGroup(existingGroup);

            if (itemImages != null && i < itemImages.size() && !itemImages.get(i).isEmpty()) {
                try {
                    String newItemImageUrl = supabaseService.uploadItemImage(itemImages.get(i));
                    newItem.setItemImage(newItemImageUrl);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload new item image", e);
                }
            }

            existingGroup.addItem(newItem);
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

        // NEW LOGIC: Check if this is the last item in the group
        // This check must happen *before* deleting toDeleteItem from the DB.
        if (currentItemListForGroup.size() == 1) { // If there's only one item currently in the list
            log.info("Item {} is the last item in group {}. Deleting the group.", itemId, groupId);
            // Find and delete the group
            StkGroup groupToDelete = stkRepo.findById(groupId)
                    .orElseThrow(() -> new NoResourceFoundException("Group not found with ID: " + groupId));
            String toDeleteImgUrl = groupToDelete.getGroupImage();
            if(toDeleteImgUrl.startsWith("https://svmeynesalueoxzhtdqp.supabase.co")){
                supabaseService.deleteFile(toDeleteImgUrl);
            }
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
