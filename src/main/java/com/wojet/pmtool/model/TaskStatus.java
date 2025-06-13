package com.wojet.pmtool.model;

import com.wojet.pmtool.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(columnDefinition = "text", nullable = false)
  private String description;

  @NotBlank
  @Size(max = 255)
  private String value;

  @Column(nullable = false, name = "kanban_list_rank")
  private Integer kanbanListRank = 1;

  // Color stored as HEX string (recommended, see previous answer)
  @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid HEX value like #RRGGBB")
  @Size(max = 7)
  private String color;

  @Column(nullable = false)
  private Boolean isCompleting = false;
}
