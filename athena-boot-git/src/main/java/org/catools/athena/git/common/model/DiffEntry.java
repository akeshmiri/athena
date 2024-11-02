package org.catools.athena.git.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.catools.athena.git.common.config.GitConstant;

import java.io.Serializable;


@Entity
@Table(name = "diff_entry", schema = GitConstant.ATHENA_GIT_SCHEMA)
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "commit"})
@Accessors(chain = true)
public class DiffEntry implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "commit_id", referencedColumnName = "id", nullable = false)
  private Commit commit;

  @Column(name = "old", length = 1000, nullable = false)
  private String oldPath;

  @Column(name = "new", length = 1000, nullable = false)
  private String newPath;

  @Column(name = "change_type", length = 30, nullable = false)
  private String changeType;

  @Column(name = "inserted", nullable = false)
  private Integer inserted;

  @Column(name = "deleted", nullable = false)
  private Integer deleted;

}
