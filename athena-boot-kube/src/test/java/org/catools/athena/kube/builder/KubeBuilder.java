package org.catools.athena.kube.builder;

import lombok.experimental.UtilityClass;
import org.catools.athena.core.model.MetadataDto;
import org.catools.athena.core.model.NameValuePair;
import org.catools.athena.core.model.ProjectDto;
import org.catools.athena.kube.common.model.Container;
import org.catools.athena.kube.common.model.ContainerMetadata;
import org.catools.athena.kube.common.model.Pod;
import org.catools.athena.kube.common.model.PodAnnotation;
import org.catools.athena.kube.common.model.PodLabel;
import org.catools.athena.kube.common.model.PodMetadata;
import org.catools.athena.kube.common.model.PodSelector;
import org.catools.athena.kube.common.model.PodStatus;
import org.catools.athena.kube.model.ContainerDto;
import org.catools.athena.kube.model.ContainerStateDto;
import org.catools.athena.kube.model.PodDto;
import org.catools.athena.kube.model.PodStatusDto;
import org.instancio.Instancio;

import java.util.Set;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@UtilityClass
public class KubeBuilder {
  public static Pod buildPod(final ProjectDto project) {
    return Instancio.of(Pod.class)
        .ignore(field(Pod::getId))
        .ignore(field(PodAnnotation::getId))
        .ignore(field(PodSelector::getId))
        .ignore(field(PodLabel::getId))
        .ignore(field(PodMetadata::getId))
        .ignore(field(Container::getId))
        .ignore(field(ContainerMetadata::getId))
        .generate(field(Pod::getUid), gen -> gen.text().uuid())
        .generate(field(Pod::getName), gen -> gen.string().length(1, 500))
        .generate(field(Pod::getNamespace), gen -> gen.string().length(1, 100))
        .generate(field(Pod::getHostname), gen -> gen.string().length(1, 200))
        .generate(field(Pod::getNodeName), gen -> gen.string().length(1, 200))
        .set(field(Pod::getStatus), buildPodStatus())
        .set(field(Pod::getProjectId), project.getId())
        .set(field(Pod::getMetadata), buildPodMetadata())
        .set(field(Pod::getAnnotations), buildPodAnnotation())
        .set(field(Pod::getLabels), buildPodLabels())
        .set(field(Pod::getSelectors), buildPodSelectors())
        .create();
  }

  public static PodDto buildPodDto(final Pod pod, final ProjectDto project) {
    return new PodDto()
        .setId(pod.getId())
        .setUid(pod.getUid())
        .setName(pod.getName())
        .setNamespace(pod.getNamespace())
        .setHostname(pod.getHostname())
        .setNodeName(pod.getNodeName())
        .setDeletedAt(pod.getDeletedAt())
        .setCreatedAt(pod.getCreatedAt())
        .setStatus(buildPodStatusDto(pod.getStatus()))
        .setProject(project.getCode())
        .setLastSync(pod.getLastSync())
        .setMetadata(pod.getMetadata().stream().map(KubeBuilder::buildMetadataDto).collect(Collectors.toSet()))
        .setAnnotations(pod.getAnnotations().stream().map(KubeBuilder::buildMetadataDto).collect(Collectors.toSet()))
        .setLabels(pod.getLabels().stream().map(KubeBuilder::buildMetadataDto).collect(Collectors.toSet()))
        .setSelectors(pod.getSelectors().stream().map(KubeBuilder::buildMetadataDto).collect(Collectors.toSet()))
        .setContainers(pod.getContainers().stream().map(KubeBuilder::buildContainerDto).collect(Collectors.toSet()));
  }

  public static Container buildContainer(final Pod pod) {
    return Instancio.of(Container.class)
        .ignore(field(Container::getId))
        .ignore(field(ContainerMetadata::getId))
        .generate(field(Container::getType), gen -> gen.string().length(1, 100))
        .generate(field(Container::getName), gen -> gen.string().length(1, 300))
        .generate(field(Container::getImage), gen -> gen.string().length(1, 1000))
        .generate(field(Container::getImageId), gen -> gen.string().length(1, 300))
        .set(field(Container::getPod), pod)
        .set(field(Container::getMetadata), buildContainerMetadata())
        .create();
  }

  public static ContainerDto buildContainerDto(final Container container) {
    return new ContainerDto()
        .setId(container.getId())
        .setType(container.getType())
        .setName(container.getName())
        .setImage(container.getImage())
        .setImageId(container.getImageId())
        .setReady(container.getReady())
        .setStarted(container.getStarted())
        .setRestartCount(container.getRestartCount())
        .setStartedAt(container.getStartedAt())
        .setLastSync(container.getLastSync())
        .setMetadata(container.getMetadata().stream().map(KubeBuilder::buildMetadataDto).collect(Collectors.toSet()));
  }

  public static MetadataDto buildMetadataDto(final NameValuePair nvp) {
    return new MetadataDto(nvp.getId(), nvp.getName(), nvp.getValue());
  }

  public static PodStatus buildPodStatus() {
    return Instancio.of(PodStatus.class)
        .ignore(field(PodStatus::getId))
        .generate(field(PodStatus::getName), gen -> gen.string().length(1, 200).nullable())
        .generate(field(PodStatus::getPhase), gen -> gen.string().length(1, 200).nullable())
        .generate(field(PodStatus::getMessage), gen -> gen.string().length(1, 1000).nullable())
        .generate(field(PodStatus::getReason), gen -> gen.string().length(1, 1000).nullable())
        .create();
  }

  public static PodStatusDto buildPodStatusDto(final PodStatus podStatus) {
    return new PodStatusDto()
        .setId(podStatus.getId())
        .setName(podStatus.getName())
        .setMessage(podStatus.getMessage())
        .setPhase(podStatus.getPhase())
        .setReason(podStatus.getReason());
  }

  public static Set<PodSelector> buildPodSelectors() {
    return Instancio.of(PodSelector.class)
        .ignore(field(PodSelector::getId))
        .generate(field(PodSelector::getValue), gen -> gen.string().length(1, 1000))
        .generate(field(PodSelector::getName), gen -> gen.string().length(1, 300))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

  public static Set<PodLabel> buildPodLabels() {
    return Instancio.of(PodLabel.class)
        .ignore(field(PodLabel::getId))
        .generate(field(PodLabel::getValue), gen -> gen.string().length(1, 1000))
        .generate(field(PodLabel::getName), gen -> gen.string().length(1, 300))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

  public static Set<PodAnnotation> buildPodAnnotation() {
    return Instancio.of(PodAnnotation.class)
        .ignore(field(PodAnnotation::getId))
        .generate(field(PodAnnotation::getValue), gen -> gen.string().length(1, 1000))
        .generate(field(PodAnnotation::getName), gen -> gen.string().length(1, 300))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

  public static Set<PodMetadata> buildPodMetadata() {
    return Instancio.of(PodMetadata.class)
        .ignore(field(PodMetadata::getId))
        .generate(field(PodMetadata::getValue), gen -> gen.string().length(1, 1000))
        .generate(field(PodMetadata::getName), gen -> gen.string().length(1, 300))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

  public static Set<ContainerMetadata> buildContainerMetadata() {
    return Instancio.of(ContainerMetadata.class)
        .ignore(field(ContainerMetadata::getId))
        .generate(field(ContainerMetadata::getValue), gen -> gen.string().length(1, 1000))
        .generate(field(ContainerMetadata::getName), gen -> gen.string().length(1, 300))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

  public static Set<ContainerStateDto> buildContainerStateDto() {
    return Instancio.of(ContainerStateDto.class)
        .ignore(field(ContainerStateDto::getId))
        .generate(field(ContainerStateDto::getType), gen -> gen.string().length(1, 100))
        .generate(field(ContainerStateDto::getMessage), gen -> gen.string().length(1, 1000))
        .generate(field(ContainerStateDto::getValue), gen -> gen.string().length(1, 1000))
        .stream()
        .limit(4)
        .collect(Collectors.toSet());
  }

}
