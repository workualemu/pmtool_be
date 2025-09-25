package com.wojet.pmtool.model;

import com.wojet.pmtool.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_statuses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatus extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  @Column(columnDefinition = "text", nullable = false)
  private String description;

  @NotBlank
  @Size(max = 255)
  private String value;

  @NotNull
  @Column(nullable = false, name = "display_order")
  private Integer displayOrder = 1;

  // Color stored as HEX string (recommended, see previous answer)
  @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Color must be a valid HEX value like #RRGGBB")
  @Size(max = 7)
  private String color;

  @NotNull
  @Column(nullable = false)
  private Boolean completing = false;
}
