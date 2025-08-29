package com.aunghein.SpringTemplate.controller.news;

import com.aunghein.SpringTemplate.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<?> getNewsByTargetBizId(@PathVariable Long bizId){
        return ResponseEntity.ok(newsService.getNewsByTargetBizId(bizId));
    }
}
