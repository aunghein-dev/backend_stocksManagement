package com.aunghein.SpringTemplate.repository.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MediaUrlRepo {

    @Autowired
    private JdbcTemplate jdbc;

    private static final String SQL =
            """
            SELECT DISTINCT (image_url) AS image_url
            FROM (
              SELECT u.user_img_url    AS image_url FROM users    u WHERE u.user_img_url    IS NOT NULL
              UNION ALL
              SELECT b.business_logo   AS image_url FROM business b WHERE b.business_logo   IS NOT NULL
              UNION ALL
              SELECT c.img_url         AS image_url FROM customer c WHERE c.img_url         IS NOT NULL
              UNION ALL
              SELECT s.group_image     AS image_url FROM stk_group s WHERE s.group_image     IS NOT NULL
              UNION ALL
              SELECT i.item_image      AS image_url FROM stk_item  i WHERE i.item_image      IS NOT NULL
            ) AS all_images
            WHERE TRIM(image_url) <> ''
            """;

    public List<String> getExistingUrlBasedOnDb() {
        return jdbc.queryForList(SQL, String.class);
    }
}
