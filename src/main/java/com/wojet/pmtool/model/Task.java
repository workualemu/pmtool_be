package com.wojet.pmtool.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ColumnTransformer;
// import org.hibernate.annotations.JdbcTypeCode;
// import org.hibernate.type.SqlTypes;

import com.wojet.pmtool.model.audit.Auditable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// import com.vladmihalcea.hibernate.type.ltree.LTreeType;
// import com.vladmihalcea.hibernate.type.ltree.LTree;
// import org.hibernate.annotations.Type;

@Entity
@Table(name = "tasks")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Task extends Auditable {

  // Relationships
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_by")
  private User reportedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_status_id")
  private TaskStatus taskStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_priority_id")
  private TaskPriority taskPriority;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent")
  private Task parent;

  // ManyToMany

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  // OneToMany

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Attachment> attachments = new HashSet<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Comment> comments = new HashSet<>();

  // Simple Fields

  @NotBlank
  @Size(max = 255)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private LocalDateTime actualStartDate;
  private LocalDateTime actualEndDate;

  @Column(nullable = false)
  private Double budget = 0.0;

  @Column(nullable = false)
  private Double expense = 0.0;

  @Column(nullable = false)
  private Integer duration = 1;

  @Column(nullable = false)
  private Double progress = 0.0;

  @Column(nullable = false, name = "kanban_list_rank")
  private Integer kanbanListRank = 1;

  @Size(max = 255)
  private String text;

  @Size(max = 255)
  private String type;

  @Column(nullable = false)
  private Integer level = 0;

  @Column(nullable = false)
  private Boolean isStarred = false;

  @Column(name = "path", columnDefinition = "ltree")
  @ColumnTransformer(write = "?::ltree", read = "path")
  private String path;

}
