package com.wojet.pmtool.payload;

import com.wojet.pmtool.payload.audit.AuditableDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO extends AuditableDTO {

  private Long id;
  private String label;
  private String description;
  private String color;
  private Long projectId;
  private String projectTitle;
  private Long clientId;
  private String clientName;
}
