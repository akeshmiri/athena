package org.catools.athena.tms.common.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.catools.athena.tms.common.config.TmsConstant.ATHENA_TMS_SCHEMA;


@Entity
@Table(name = "item", indexes = @Index(columnList = "code"), schema = ATHENA_TMS_SCHEMA)
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Accessors(chain = true)
public class Item implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  private Long id;

  @Column(name = "code", length = 15, unique = true, nullable = false)
  private String code;

  @Column(name = "name", length = 300, nullable = false)
  private String name;

  @Column(name = "created_on", columnDefinition = "TIMESTAMPTZ", nullable = false)
  private Instant createdOn;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "updated_on", columnDefinition = "TIMESTAMPTZ")
  private Instant updatedOn;

  @Column(name = "updated_by")
  private Long updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_id", nullable = false, referencedColumnName = "id")
  private ItemType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false, referencedColumnName = "id")
  private Status status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "priority_id", nullable = false, referencedColumnName = "id")
  private Priority priority;

  @Column(name = "project_id", nullable = false)
  private Long projectId;

  @OneToMany(mappedBy = "item", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<StatusTransition> statusTransitions = new HashSet<>();

  @ElementCollection
  @CollectionTable(
      schema = ATHENA_TMS_SCHEMA,
      name = "item_version_mid",
      joinColumns = {@JoinColumn(name = "item_id")}
  )
  @Column(name = "version_id")
  private Set<Long> versionIds = new HashSet<>();

  @ManyToMany
  @JoinTable(
      schema = ATHENA_TMS_SCHEMA,
      name = "item_metadata_mid",
      joinColumns = {@JoinColumn(name = "item_id")},
      inverseJoinColumns = {@JoinColumn(name = "metadata_id")}
  )
  private Set<ItemMetadata> metadata = new HashSet<>();

  public void setStatusTransitions(Set<StatusTransition> statusTransitions) {
    if (statusTransitions == null) {
      this.statusTransitions.forEach(this::removeStatusTransition);
      return;
    }
    this.statusTransitions.clear();
    statusTransitions.forEach(this::addStatusTransition);
  }

  public void removeStatusTransition(StatusTransition statusTransition) {
    if (statusTransition == null) return;
    this.statusTransitions.remove(statusTransition.setItem(null));
  }

  public void addStatusTransition(StatusTransition statusTransition) {
    if (statusTransition == null) return;
    this.statusTransitions.add(statusTransition.setItem(this));
  }
}
