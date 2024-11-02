package org.catools.athena.tms.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.Instant;

import static org.catools.athena.tms.common.config.TmsConstant.ATHENA_TMS_SCHEMA;


@Entity
@Table(name = "execution", indexes = @Index(columnList = "created, cycle_id, item_id"), schema = ATHENA_TMS_SCHEMA)
@Getter
@Setter
@Accessors(chain = true)
public class TestExecution implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  private Long id;

  @Column(name = "created", columnDefinition = "TIMESTAMPTZ", nullable = false)
  private Instant createdOn;

  @Column(name = "executed", columnDefinition = "TIMESTAMPTZ")
  private Instant executedOn;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cycle_id", nullable = false, referencedColumnName = "id")
  private TestCycle cycle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false, referencedColumnName = "id")
  private Item item;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false, referencedColumnName = "id")
  private Status status;

  @Column(name = "executor_id")
  private Long executorId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    TestExecution execution = (TestExecution) o;

    EqualsBuilder equalsBuilder = new EqualsBuilder();
    return equalsBuilder
        .append(item == null ? null : item.getCode(), execution.item == null ? null : execution.item.getCode())
        .append(cycle == null ? null : cycle.getCode(), execution.cycle == null ? null : execution.cycle.getCode())
        .append(createdOn, execution.createdOn)
        .append(executedOn, execution.executedOn)
        .append(status, execution.status)
        .append(executorId, execution.executorId)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(item == null ? null : item.getCode())
        .append(cycle == null ? null : cycle.getCode())
        .append(createdOn)
        .append(executedOn)
        .append(status)
        .append(executorId)
        .toHashCode();
  }
}
