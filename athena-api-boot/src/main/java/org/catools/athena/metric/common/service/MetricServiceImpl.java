package org.catools.athena.metric.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.catools.athena.metric.common.entity.Action;
import org.catools.athena.metric.common.entity.Metric;
import org.catools.athena.metric.common.mapper.MetricMapper;
import org.catools.athena.metric.common.repository.ActionRepository;
import org.catools.athena.metric.common.repository.MetricRepository;
import org.catools.athena.metrics.model.MetricDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

  private final ActionRepository actionRepository;
  private final MetricRepository metricRepository;
  private final MetricMapper metricMapper;

  @Override
  public MetricDto save(MetricDto entity) {
    Metric metric = metricMapper.metricDtoToMetric(entity);

    Action action = metric.getAction();
    Action savedAction = actionRepository.findByNameAndTypeAndTargetAndCommand(action.getName(), action.getType(), action.getTarget(), action.getCommand())
        .orElseGet(() -> actionRepository.save(action));

    metric.setAction(savedAction);

    Metric savedMetric = metricRepository.save(metric);
    return metricMapper.metricToMetricDto(savedMetric);
  }

  @Override
  public Optional<MetricDto> getById(Long id) {
    return metricRepository.findById(id).map(metricMapper::metricToMetricDto);
  }
}
