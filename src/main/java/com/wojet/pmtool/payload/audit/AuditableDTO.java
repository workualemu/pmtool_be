package com.wojet.pmtool.payload.audit;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public abstract class AuditableDTO {

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long createdBy;
  private Long updatedBy;
  private String createdByName;
  private String updatedByName;

}