package org.catools.athena.core.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.TypedResponse;
import org.catools.athena.core.model.ProjectDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "projectFeignClient", url = "${feign.clients.core.url}")
public interface ProjectFeignClient {

  @RequestLine("GET /project?keyword={keyword}")
  @Cacheable(value = "project-by-keyword", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<ProjectDto> search(@Param String keyword);

  @RequestLine("GET /project/{id}")
  @Cacheable(value = "project-by-id", key = "#p0", condition = "#p0!=null", unless = "#result==null")
  TypedResponse<ProjectDto> getById(@Param Long id);

  @RequestLine("POST /project")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> save(ProjectDto project);

  @RequestLine("PUT /project")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> update(ProjectDto project);

}
