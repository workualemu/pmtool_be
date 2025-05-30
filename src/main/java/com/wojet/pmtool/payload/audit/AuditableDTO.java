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

  // public String getCreatedByName() {
  // return createdByName;
  // }

  // public void setCreatedByName(String createdByName) {
  // this.createdByName = createdByName;
  // }

  // public String getUpdatedByName() {
  // return updatedByName;
  // }

  // public void setUpdatedByName(String updatedByName) {
  // this.updatedByName = updatedByName;
  // }

}