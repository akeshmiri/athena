package org.catools.athena.core.kube;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.TypedResponse;
import org.catools.athena.kube.model.PodDto;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Set;

@FeignClient(value = "podFeignClient")
public interface PodFeignClient {

  @RequestLine("GET /kube/pods?projectId={projectId}&namespace={namespace}")
  TypedResponse<Set<PodDto>> getAll(@Param Long projectId, @Param String namespace);

  @RequestLine("GET /kube/pod?name={name}&namespace={namespace}")
  TypedResponse<PodDto> getByNameAndNamespace(@Param String name, @Param String namespace);

  @RequestLine("GET /kube/pod/{id}")
  TypedResponse<PodDto> getById(@Param Long id);

  @RequestLine("POST /kube/pod")
  @Headers("Content-Type: application/json")
  TypedResponse<Void> saveOrUpdate(PodDto pod);

}
