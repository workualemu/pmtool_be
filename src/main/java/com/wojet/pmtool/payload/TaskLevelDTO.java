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
public class TaskLevelDTO extends AuditableDTO {
  private Long id;
  private Integer level;
  private String name;
  private String pluralName;
  private Long projectId;
  private String projectTitle;
}
