package com.aunghein.SpringTemplate.service;

import com.aunghein.SpringTemplate.model.dto.TellerResponse;
import com.aunghein.SpringTemplate.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TellerService {

    private final UserRepo userRepo;

    public List<TellerResponse> getAllTellersForBiz(Long bizId) {
        return userRepo.getAllTellersForBiz(bizId);
    }
}
