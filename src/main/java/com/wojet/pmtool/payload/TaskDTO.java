package com.wojet.pmtool.payload;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonAlias;

import com.wojet.pmtool.payload.audit.AuditableDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO extends AuditableDTO {

  private Long id;
  private String title;
  private String description;
  private Long projectId;
  private String projectTitle;
  private Long clientId;
  private String clientName;

  private Long assignedToId;
  private String assignedToName;
  private Long reportedById;
  private String reportedByName;
  private Long parentId;
  private String parentTitle;
  @JsonAlias({ "taskTag", "tagIds" })
  private Set<Long> tagIds = new HashSet<>();
  private Set<String> tagLabels = new HashSet<>();

  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate actualStartDate;
  private LocalDate actualEndDate;
  private Double budget = 0.0;
  private Double expense = 0.0;
  private Integer duration = 1;
  private Double progress = 0.0;
  private Integer kanbanListRank = 1;
  private String text;
  private String type;
  private Boolean isStarred = false;
  private String path;

  private Long taskStatusId;
  private String taskStatusValue;
  private Long taskPriorityId;
  private String taskPriorityValue;
  private Long taskLevelId;
  private String taskLevelName;

  private boolean isMatch = false;
  private boolean isAncestor = false;

  // @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval =
  // true, fetch = FetchType.LAZY)
  // private Set<Attachment> attachments = new HashSet<>();

  // @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval =
  // true, fetch = FetchType.LAZY)
  // private Set<Comment> comments = new HashSet<>();

}
