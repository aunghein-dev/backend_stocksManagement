package com.aunghein.SpringTemplate.repository;

import com.aunghein.SpringTemplate.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News, Long> {

    @Query(value = """
           SELECT * FROM news WHERE target_biz_id = :bizId;
           """, nativeQuery = true)
    List<News> getNewsByTargetBizId(@Param("bizId") Long bizId);
}
