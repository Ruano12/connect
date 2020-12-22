package com.connect.connect.security;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;


public interface UserInfoOperations {

  @SuppressWarnings("rawtypes")
  @Cacheable(cacheManager = "userInfoCacheManager", value = "auth-userinfo", key = "#accessToken")
  Map get(String accessToken);
}
