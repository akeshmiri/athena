package org.catools.athena.spec.common.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import static org.catools.athena.spec.common.config.ApiSpecConstant.ATHENA_SPEC_SCHEMA;

@Entity
@Table(name = "api_spec", schema = ATHENA_SPEC_SCHEMA)
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Accessors(chain = true)
public class ApiSpec implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  private Long id;

  @Column(name = "project_id", nullable = false)
  private Long projectId;

  @Column(name = "version", length = 10, nullable = false)
  private String version;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "title", length = 100, nullable = false)
  private String title;

  @Column(name = "first_time_seen", columnDefinition = "TIMESTAMPTZ")
  private Instant firstTimeSeen;

  @Column(name = "last_sync_time", columnDefinition = "TIMESTAMPTZ")
  private Instant lastSyncTime;

  @OneToMany(mappedBy = "spec", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<ApiPath> paths = new HashSet<>();

  @ManyToMany
  @JoinTable(
      schema = ATHENA_SPEC_SCHEMA,
      name = "api_spec_metadata_mid",
      joinColumns = {@JoinColumn(name = "spec_id")},
      inverseJoinColumns = {@JoinColumn(name = "metadata_id")}
  )
  private Set<ApiSpecMetadata> metadata = new HashSet<>();

  public void setPaths(Set<ApiPath> paths) {
    if (paths == null) {
      this.paths.forEach(this::removePath);
      return;
    }
    this.paths.clear();
    paths.forEach(this::addPath);
  }

  public void removePath(ApiPath path) {
    if (path == null) return;
    this.paths.remove(path.setSpec(null));
  }

  public void addPath(ApiPath path) {
    if (path == null) return;
    this.paths.add(path.setSpec(this));
  }
}
