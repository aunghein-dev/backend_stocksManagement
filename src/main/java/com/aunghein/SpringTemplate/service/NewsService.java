package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.News;
import com.aunghein.SpringTemplate.repository.NewsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepo newsRepo;

    public List<News> getNewsByTargetBizId(Long bizId) {
        return newsRepo.getNewsByTargetBizId(bizId);
    }
}
