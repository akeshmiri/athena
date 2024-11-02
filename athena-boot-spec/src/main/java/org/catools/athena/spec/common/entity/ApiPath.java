package org.catools.athena.spec.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.catools.athena.spec.common.config.ApiSpecConstant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "api_path", schema = ApiSpecConstant.ATHENA_SPEC_SCHEMA)
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "spec"})
@Accessors(chain = true)
public class ApiPath implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  private Long id;

  @Column(name = "method", length = 10, nullable = false)
  private String method;

  @Column(name = "title", length = 1000)
  private String title;

  @Column(name = "description", length = 5000)
  private String description;

  @Column(name = "url", length = 500, nullable = false)
  private String url;

  @Column(name = "first_time_seen", columnDefinition = "TIMESTAMPTZ")
  private Instant firstTimeSeen;

  @Column(name = "last_sync_time", columnDefinition = "TIMESTAMPTZ")
  private Instant lastSyncTime;

  @Column(name = "parameters", length = 2000)
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> parameters = new HashMap<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spec_id", nullable = false, referencedColumnName = "id")
  private ApiSpec spec;

  @ManyToMany
  @JoinTable(
      schema = ApiSpecConstant.ATHENA_SPEC_SCHEMA,
      name = "path_metadata_mid",
      joinColumns = {@JoinColumn(name = "path_id")},
      inverseJoinColumns = {@JoinColumn(name = "metadata_id")}
  )
  private Set<ApiPathMetadata> metadata = new HashSet<>();

}
