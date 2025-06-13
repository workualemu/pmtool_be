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
public class TaskStatusDTO extends AuditableDTO {

  private Long id;
  private String value;
  private String description;
  private String color;
  private Integer kanbanListRank;
  private Boolean isCompleting;
  private Long projectId;
  private String projectTitle;
  private Long clientId;
  private String clientName;
}
