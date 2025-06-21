package com.aunghein.SpringTemplate.repository;


import com.aunghein.SpringTemplate.model.StkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StkItemRepo extends JpaRepository<StkItem, Long> {

    @Query(value = "SELECT * FROM stk_item WHERE item_id = :itemId", nativeQuery = true)
    StkItem findItemByCustomId(@Param("itemId") Long itemId);

    @Query(value = "SELECT * FROM stk_item WHERE group_id = :groupId", nativeQuery = true)
    List<StkItem> findItemsByGroupId(@Param("groupId") Long groupId);

}
