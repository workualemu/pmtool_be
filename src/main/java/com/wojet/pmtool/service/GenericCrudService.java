package com.wojet.pmtool.service;

import static com.wojet.pmtool.util.AuditUtil.applyAuditOnCreate;
import static com.wojet.pmtool.util.AuditUtil.applyAuditOnUpdate;

import java.util.List;
import java.util.function.Consumer;

// import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.model.audit.Auditable;
import com.wojet.pmtool.payload.PagedResponse;

public abstract class GenericCrudService<E extends Auditable, // Entity
    D, // DTO
    R extends JpaRepository<E, Long> // Repository
> {

  @Autowired
  protected R repository;

  @Autowired
    private AuditorAware<User> auditorAware;

  // @Autowired
  // protected ModelMapper modelMapper;

  public PagedResponse<D> getAll(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<E> page = repository.findAll(pageable);

    List<E> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<D> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<D> response = new PagedResponse<>();
    response.setContent(dtos);
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLastPage(page.isLast());

    return response;
  }

  public D getById(Long id) {
    E entity = repository.findById(id)
        .orElseThrow(() -> new APIException("Entity with ID " + id + " not found!"));
    return mapToDTO(entity);
  }

  public D create(D dto) {
    E entity = mapToEntity(dto);
    if (validateOnCreate(entity)) {
      throw new APIException("Duplicate record!");
    }
    applyAuditOnCreate(entity, auditorAware);
    return mapToDTO(repository.save(entity));
  }

  /**
   * Create with extra relationships (like client/project)
   */
  public D createWithAssociations(D dto, List<Consumer<E>> relationshipSetters) {
    E entity = mapToEntity(dto);
    if (validateOnCreate(entity)) {
      throw new APIException("Duplicate record!");
    }
    applyAuditOnCreate(entity, auditorAware);
    for (Consumer<E> relationshipSetter : relationshipSetters) {
      relationshipSetter.accept(entity); 
    }
    // relationshipSetter.accept(entity); // set client/project/etc.
    return mapToDTO(repository.save(entity));
  }

  public D update(Long id, D dto) {
    E existing = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));

    if (validateOnUpdate(dto, id)) {
      throw new APIException("Duplicate record!");
    }

    E entity = mapToEntity(dto);
    entity.setId(id);
    applyAuditOnUpdate(entity, existing, auditorAware);
    return mapToDTO(repository.save(entity));
  }

  public D updateWithAssociations(Long id, D dto, List<Consumer<E>> relationshipSetters) {
    E existing = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));

    if (validateOnUpdate(dto, id)) {
      throw new APIException("Duplicate record!");
    }

    E entity = mapToEntity(dto);
    entity.setId(id);
    applyAuditOnUpdate(entity, existing, auditorAware);
    for (Consumer<E> relationshipSetter : relationshipSetters) {
      relationshipSetter.accept(entity);
    }
    return mapToDTO(repository.save(entity));
  }

  public D deleteById(Long id) {
    E entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));
    repository.deleteById(id);
    return mapToDTO(entity);
  }

 


  protected abstract E mapToEntity(D dto);

  protected abstract D mapToDTO(E entity);

  protected abstract boolean validateOnCreate(E entity);

  protected abstract boolean validateOnUpdate(D dto, Long id);

}
