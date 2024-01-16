package org.catools.athena.pipeline.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.catools.athena.core.model.MetadataDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PipelineScenarioExecutionDto implements Serializable {

    private Long id;

    private String feature;

    private String scenario;

    private String parameters;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime beforeScenarioStartTime;

    private LocalDateTime beforeScenarioEndTime;

    private String status;

    private String executor;

    private Long pipelineId;

    private Set<MetadataDto> metadata = new HashSet<>();
}
