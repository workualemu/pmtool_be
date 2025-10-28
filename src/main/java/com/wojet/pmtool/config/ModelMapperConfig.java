package com.wojet.pmtool.config;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.Role;
import com.wojet.pmtool.model.Tag;
import com.wojet.pmtool.model.Task;
import com.wojet.pmtool.model.TaskLevel;
import com.wojet.pmtool.model.TaskPriority;
import com.wojet.pmtool.model.TaskStatus;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.model.audit.Auditable;
import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.payload.RoleDTO;
import com.wojet.pmtool.payload.TagDTO;
import com.wojet.pmtool.payload.TaskDTO;
import com.wojet.pmtool.payload.TaskLevelDTO;
import com.wojet.pmtool.payload.TaskPriorityDTO;
import com.wojet.pmtool.payload.TaskStatusDTO;
import com.wojet.pmtool.payload.audit.AuditableDTO;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {

  private final TaskDtoToEntityConverter taskDtoToEntityConverter;

  private final Converter<User, String> userToFullNameConverter = new Converter<>() {
    @Override
    public String convert(MappingContext<User, String> context) {
      User user = context.getSource();
      return user != null ? user.getFullName() : null;
    }
  };

  private final Converter<User, Long> userToIdConverter = context -> {
    User user = context.getSource();
    return user != null ? user.getId() : null;
  };

  private final Converter<TaskLevel, Long> taskLevelToIdConverter = context -> {
    TaskLevel src = context.getSource();
    return src != null ? src.getId() : null;
  };

  private final Converter<TaskStatus, Long> taskStatusToIdConverter = context -> {
    TaskStatus src = context.getSource();
    return src != null ? src.getId() : null;
  };

  private final Converter<TaskPriority, Long> taskPriorityToIdConverter = context -> {
    TaskPriority src = context.getSource();
    return src != null ? src.getId() : null;
  };

  private final Converter<TaskLevel, String> taskLevelToNameConverter = context -> {
    TaskLevel src = context.getSource();
    return src != null ? src.getName() : null;
  };

  private final Converter<TaskStatus, String> taskStatusToValueConverter = context -> {
    TaskStatus src = context.getSource();
    return src != null ? src.getValue() : null;
  };

  private final Converter<TaskPriority, String> taskPriorityToValueConverter = context -> {
    TaskPriority src = context.getSource();
    return src != null ? src.getValue() : null;
  };

  private final Converter<Set<Tag>, Set<Long>> tagSetToIds = context -> {
    Set<Tag> tags = context.getSource();
    if (tags == null) return Set.of();
    return tags.stream().map(Tag::getId).collect(Collectors.toCollection(LinkedHashSet::new));
  };

  private final Converter<Set<Tag>, Set<String>> tagSetToLabels = context -> {
    Set<Tag> tags = context.getSource();
    if (tags == null) return Set.of();
    return tags.stream().map(Tag::getLabel).collect(Collectors.toCollection(LinkedHashSet::new));
  };

  private <S extends Auditable, D extends AuditableDTO> void registerAuditMappings(ModelMapper mapper, Class<S> source,
      Class<D> dest) {
    mapper.typeMap(source, dest).addMappings(mapping -> {
      mapping.using(userToIdConverter).map(Auditable::getCreatedBy, AuditableDTO::setCreatedBy);
      mapping.using(userToIdConverter).map(Auditable::getUpdatedBy, AuditableDTO::setUpdatedBy);

      mapping.using(userToFullNameConverter).map(Auditable::getCreatedBy, AuditableDTO::setCreatedByName);
      mapping.using(userToFullNameConverter).map(Auditable::getUpdatedBy, AuditableDTO::setUpdatedByName);
    });
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();

    mapper.getConfiguration()
        .setAmbiguityIgnored(true)
        .setSkipNullEnabled(true);

    mapper.typeMap(Auditable.class, AuditableDTO.class).addMappings(mapping -> {
      mapping.using(userToFullNameConverter).map(Auditable::getCreatedBy, AuditableDTO::setCreatedByName);
      mapping.using(userToFullNameConverter).map(Auditable::getUpdatedBy, AuditableDTO::setUpdatedByName);

      mapping.using(userToIdConverter)
          .map(Auditable::getCreatedBy, AuditableDTO::setCreatedBy);
      mapping.using(userToIdConverter)
          .map(Auditable::getUpdatedBy, AuditableDTO::setUpdatedBy);
    });

    registerAuditMappings(mapper, Client.class, ClientDTO.class);
    registerAuditMappings(mapper, Project.class, ProjectDTO.class);
    registerAuditMappings(mapper, Tag.class, TagDTO.class);
    registerAuditMappings(mapper, TaskPriority.class, TaskPriorityDTO.class);
    registerAuditMappings(mapper, TaskStatus.class, TaskStatusDTO.class);
    registerAuditMappings(mapper, TaskLevel.class, TaskLevelDTO.class);
    registerAuditMappings(mapper, Task.class, TaskDTO.class);
    registerAuditMappings(mapper, Role.class, RoleDTO.class);

    TypeMap<Task, TaskDTO> taskMap = mapper.getTypeMap(Task.class, TaskDTO.class);
    if (taskMap == null)
      taskMap = mapper.createTypeMap(Task.class, TaskDTO.class);
    taskMap.addMappings(m -> {
      m.using(taskLevelToIdConverter).map(Task::getTaskLevel, TaskDTO::setTaskLevelId);
      m.using(taskStatusToIdConverter).map(Task::getTaskStatus, TaskDTO::setTaskStatusId);
      m.using(taskPriorityToIdConverter).map(Task::getTaskPriority, TaskDTO::setTaskPriorityId);
      m.using(tagSetToIds).map(Task::getTags, TaskDTO::setTagIds);

      // names/values (only if these getters exist on your entities & setters on DTO)
      m.using(taskLevelToNameConverter).map(Task::getTaskLevel, TaskDTO::setTaskLevelName);
      m.using(taskStatusToValueConverter).map(Task::getTaskStatus, TaskDTO::setTaskStatusValue);
      m.using(taskPriorityToValueConverter).map(Task::getTaskPriority, TaskDTO::setTaskPriorityValue);
      m.using(tagSetToLabels).map(Task::getTags, TaskDTO::setTagLabels);
    });

    mapper.addConverter(taskDtoToEntityConverter);

    mapper.addConverter(userToIdConverter, User.class, Long.class);
    return mapper;
  }
}
