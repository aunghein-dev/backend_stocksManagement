package com.aunghein.SpringTemplate.model.dto;

import com.aunghein.SpringTemplate.model.Business;

public interface Account {
     Long getAccountId();
     String getUsername();
     String getRole();
     String getFullName();
     String getUserImgUrl();
     Business getBusiness();

     default String toStringSafe() {
          return "Account{" +
                  "accountId=" + getAccountId() +
                  ", username='" + getUsername() + '\'' +
                  ", role='" + getRole() + '\'' +
                  ", fullName='" + getFullName() + '\'' +
                  ", userImgUrl='" + getUserImgUrl() + '\'' +
                  ", business=" + (getBusiness() != null ? getBusiness().getBusinessName() : "null") +
                  '}';
     }

}
