package com.wojet.pmtool.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.Tag;
import com.wojet.pmtool.model.TaskPriority;
import com.wojet.pmtool.model.TaskStatus;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.model.audit.Auditable;
import com.wojet.pmtool.payload.ClientDTO;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.payload.TagDTO;
import com.wojet.pmtool.payload.TaskPriorityDTO;
import com.wojet.pmtool.payload.TaskStatusDTO;
import com.wojet.pmtool.payload.audit.AuditableDTO;

@Configuration
public class ModelMapperConfig {

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

  private <S, D> void registerAuditMappings(ModelMapper mapper, Class<S> source, Class<D> dest) {
    mapper.typeMap(source, dest).addMappings(mapping -> {
      mapping.using(userToIdConverter).map(src -> ((Auditable) src).getCreatedBy(),
          (destObj, value) -> ((AuditableDTO) destObj).setCreatedBy((Long) value));
      mapping.using(userToIdConverter).map(src -> ((Auditable) src).getUpdatedBy(),
          (destObj, value) -> ((AuditableDTO) destObj).setUpdatedBy((Long) value));
      mapping.using(userToFullNameConverter).map(src -> ((Auditable) src).getCreatedBy(),
          (destObj, value) -> ((AuditableDTO) destObj).setCreatedByName((String) value));
      mapping.using(userToFullNameConverter).map(src -> ((Auditable) src).getUpdatedBy(),
          (destObj, value) -> ((AuditableDTO) destObj).setUpdatedByName((String) value));
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

    mapper.addConverter(userToIdConverter, User.class, Long.class);
    return mapper;
  }
}
