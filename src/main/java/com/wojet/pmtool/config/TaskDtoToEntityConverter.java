package com.wojet.pmtool.config;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import com.wojet.pmtool.model.Task;
import com.wojet.pmtool.payload.TaskDTO;
import com.wojet.pmtool.repository.TaskLevelRepository;
import com.wojet.pmtool.repository.TaskPriorityRepository;
import com.wojet.pmtool.repository.TaskStatusRepository;
import com.wojet.pmtool.repository.TagRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskDtoToEntityConverter implements Converter<TaskDTO, Task> {

  private final TaskLevelRepository taskLevelRepo;
  private final TaskStatusRepository taskStatusRepo;
  private final TaskPriorityRepository taskPriorityRepo;
  private final TagRepository tagRepository;

  @Override
  public Task convert(MappingContext<TaskDTO, Task> ctx) {
    TaskDTO src = ctx.getSource();
    Task dest = ctx.getDestination() != null ? ctx.getDestination() : new Task();

    dest.setId(src.getId());
    dest.setTitle(src.getTitle());
    dest.setDescription(src.getDescription());

    if (src.getTaskLevelId() != null) {
      dest.setTaskLevel(taskLevelRepo.getReferenceById(src.getTaskLevelId()));
    } else {
      dest.setTaskLevel(null);
    }

    if (src.getTaskStatusId() != null) {
      dest.setTaskStatus(taskStatusRepo.getReferenceById(src.getTaskStatusId()));
    } else {
      dest.setTaskStatus(null);
    }

    if (src.getTaskPriorityId() != null) {
      dest.setTaskPriority(taskPriorityRepo.getReferenceById(src.getTaskPriorityId()));
    } else {
      dest.setTaskPriority(null);
    }

    // map tags (if provided)
    if (src.getTagIds() != null) {
      if (src.getTagIds().isEmpty()) {
        dest.setTags(new HashSet<>());
      } else {
        List<com.wojet.pmtool.model.Tag> tags = tagRepository.findAllById(src.getTagIds());
        // if strict existence check is desired, compare sizes and throw if mismatch
        dest.setTags(new HashSet<>(tags));
      }
    }

    return dest;
  }
}