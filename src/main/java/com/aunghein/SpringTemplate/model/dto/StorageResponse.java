package com.aunghein.SpringTemplate.model.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageResponse {
    private Long limitStorageKb;
    private String limitStorageTxt;
    private String longName;
}