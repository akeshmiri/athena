package org.catools.athena.core.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.TypedResponse;
import org.catools.athena.core.model.EnvironmentDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "environmentFeignClient", url = "${feign.clients.athena.core.url}")
public interface EnvironmentFeignClient {

  @RequestLine("GET /environment?keyword={keyword}")
  @Cacheable(value = "environment-by-keyword", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<EnvironmentDto> search(@Param String keyword);

  @RequestLine("GET /environment/{id}")
  @Cacheable(value = "environment-by-id", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<EnvironmentDto> getById(@Param Long id);

  @RequestLine("POST /environment")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> save(EnvironmentDto environment);

  @RequestLine("PUT /environment")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> update(EnvironmentDto environment);

}
