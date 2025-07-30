package com.aunghein.SpringTemplate.service;

public enum StorageFolder {
    BUSINESS_LOGO("business/logo"),
    GROUP_IMAGES("group"),
    ITEM_IMAGES("group/items"),
    PROFILE_PICTURES("profilePictures");

    private final String path;

    StorageFolder(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}