package com.wojet.pmtool.model;

import com.wojet.pmtool.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_levels")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskLevel extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String name;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String pluralName;

  @Positive
  @Column(nullable = false)
  private Integer level;

}
