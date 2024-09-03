package org.catools.athena.pipeline.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.common.utils.RetryUtils;
import org.catools.athena.pipeline.common.entity.PipelineExecution;
import org.catools.athena.pipeline.common.entity.PipelineExecutionMetadata;
import org.catools.athena.pipeline.common.mapper.PipelineMapper;
import org.catools.athena.pipeline.common.repository.PipelineExecutionMetaDataRepository;
import org.catools.athena.pipeline.common.repository.PipelineExecutionRepository;
import org.catools.athena.pipeline.model.PipelineExecutionDto;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineExecutionServiceImpl implements PipelineExecutionService {
  private final PipelineExecutionMetaDataRepository pipelineExecutionMetaDataRepository;
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineMapper pipelineMapper;

  /**
   * Save entity
   */
  @Override
  public PipelineExecutionDto save(PipelineExecutionDto entity) {
    log.debug("Saving entity: {}", entity);
    final PipelineExecution pipelineExecution = pipelineMapper.executionDtoToExecution(entity);
    pipelineExecution.setMetadata(normalizeMetadata(pipelineExecution.getMetadata()));
    final PipelineExecution savedPipelineExecution = RetryUtils.retry(3, 1000, integer -> pipelineExecutionRepository.saveAndFlush(pipelineExecution));
    return pipelineMapper.executionToExecutionDto(savedPipelineExecution);
  }

  /**
   * Retrieve execution by id
   */
  @Override
  public Optional<PipelineExecutionDto> getById(Long id) {
    final Optional<PipelineExecution> savedPipelineExecution = pipelineExecutionRepository.findById(id);
    return savedPipelineExecution.map(pipelineMapper::executionToExecutionDto);
  }

  private synchronized Set<PipelineExecutionMetadata> normalizeMetadata(Set<PipelineExecutionMetadata> metadataSet) {
    final Set<PipelineExecutionMetadata> metadata = new HashSet<>();

    for (PipelineExecutionMetadata md : metadataSet) {
      // Read md from DB and if MD does not exist we create one and assign it to the pipeline
      PipelineExecutionMetadata pipelineMD =
          pipelineExecutionMetaDataRepository.findByNameAndValue(md.getName(), md.getValue())
              .orElseGet(() -> pipelineExecutionMetaDataRepository.saveAndFlush(md));

      metadata.add(pipelineMD);
    }

    return metadata;
  }
}
