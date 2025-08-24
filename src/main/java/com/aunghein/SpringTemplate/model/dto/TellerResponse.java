package com.aunghein.SpringTemplate.model.dto;

import java.time.Instant;

public interface TellerResponse {
     Long getId();
     String getUsername();
     String getRole();
     String getFullName();
     String getUserImgUrl();
     String getDeviceName();
     String getDeviceType();
     String getGeoCity();
     String getGeoRegion();
     String getIpAddress();
     Instant getLoginAt();
     Instant getLogoutAt();
     String getStatus();
     Instant getUpdatedAt();
}
