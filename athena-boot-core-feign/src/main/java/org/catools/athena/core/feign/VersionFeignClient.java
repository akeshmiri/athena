package org.catools.athena.core.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.TypedResponse;
import org.catools.athena.core.model.VersionDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "versionFeignClient")
public interface VersionFeignClient {

  @RequestLine("GET /version?keyword={keyword}")
  @Cacheable(value = "version-by-keyword", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<VersionDto> search(@Param String keyword);

  @RequestLine("GET /version/{id}")
  @Cacheable(value = "version-by-id", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<VersionDto> getById(@Param Long id);

  @RequestLine("POST /version")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> save(VersionDto version);

  @RequestLine("PUT /version")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> update(VersionDto version);

}
