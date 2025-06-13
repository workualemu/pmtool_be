package com.wojet.pmtool.model;

import com.wojet.pmtool.model.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @NotBlank
  @Size(max = 255)
  private String fileName;

  @NotBlank
  @Size(max = 1024)
  private String fileUrl; // can be S3 URL or file path

  private Long fileSize;

  @Size(max = 255)
  private String fileType;
}
