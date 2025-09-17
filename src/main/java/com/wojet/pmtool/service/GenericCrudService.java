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
import com.wojet.pmtool.model.Task;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.model.audit.Auditable;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.repository.TaskRepository;

public abstract class GenericCrudService<E extends Auditable, // Entity
    D, // DTO
    R extends JpaRepository<E, Long> // Repository
> {

  @Autowired
  protected R repository;

  @Autowired
  private AuditorAware<Long> auditorAware;

  // for testing purposes
  public void setAuditorAware(AuditorAware<Long> auditorAware) {
    this.auditorAware = auditorAware;
  }

  // for testing purposes
  public void setRepository(R repository) {
    this.repository = repository;
  }

  /**
   * Get all entities with pagination and sorting
   *
   * @param pageNumber the page number to retrieve
   * @param pageSize   the number of records per page
   * @param sortBy     the field to sort by
   * @param sortDir    the direction of sorting (asc or desc)
   * @return a paginated response containing DTOs
   */
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

  /**
   * Get an entity by its ID
   *
   * @param id the ID of the entity to retrieve
   * @return the DTO of the entity
   */
  public D getById(Long id) {
    E entity = repository.findById(id)
        .orElseThrow(() -> new APIException("Entity with ID " + id + " not found!"));
    return mapToDTO(entity);
  }

  /**
   * Create a new entity
   *
   * @param dto the DTO to create
   * @return the created DTO
   */
  public D create(D dto) {
    E entity = mapToEntity(dto);
    if (validateOnCreate(entity)) {
      throw new APIException("Duplicate record!");
    }
    applyAuditOnCreate(entity, auditorAware);
    return mapToDTO(repository.save(entity));
  }

  /**
   * Create a new entity with associated relationships
   *
   * @param dto                the DTO to create
   * @param relationshipSetter a consumer to set relationships on the entity
   * @return the created DTO
   */
  public D createWithAssociations(D dto, Consumer<E> relationshipSetter) {
    E entity = mapToEntity(dto);
    applyAuditOnCreate(entity, auditorAware);
    relationshipSetter.accept(entity);
    if (validateOnCreate(entity)) {
      throw new APIException("Duplicate record!");
    }
    // If the entity is object of class Task set path attribute to null
    if (entity instanceof Task task) {
      task.setPath(null);
      Task savedTask = (Task) repository.save((E) task);

      String newPath = (savedTask.getParent() != null && savedTask.getParent().getPath() != null)
          ? task.getParent().getPath() + "." + savedTask.getId()
          : String.valueOf(savedTask.getId());

      if (repository instanceof TaskRepository taskRepository) {
        taskRepository.updatePath(savedTask.getId(), newPath);
      }
      savedTask.setPath(newPath);
      // task.setPath(newPath);
      return mapToDTO(repository.save((E) task));
    }

    return mapToDTO(repository.save(entity));
  }

  // The updatePath method should be defined in the repository interface, not here.


  /**
   * Update an existing entity
   *
   * @param id  the ID of the entity to update
   * @param dto the DTO containing updated information
   * @return the updated DTO
   */
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

  /**
   * Update an existing entity with associated relationships
   *
   * @param id                 the ID of the entity to update
   * @param dto                the DTO containing updated information
   * @param relationshipSetter a consumer to set relationships on the entity
   * @return the updated DTO
   */
  public D updateWithAssociations(Long id, D dto, Consumer<E> relationshipSetter) {
    E existing = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));

    if (validateOnUpdate(dto, id)) {
      throw new APIException("Duplicate record!");
    }

    E entity = mapToEntity(dto);
    entity.setId(id);
    applyAuditOnUpdate(entity, existing, auditorAware);
    relationshipSetter.accept(entity);

    return mapToDTO(repository.save(entity));
  }

  /**
   * Delete an entity by its ID
   *
   * @param id the ID of the entity to delete
   * @return the deleted DTO
   */
  public D deleteById(Long id) {
    E entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));
    repository.deleteById(id);
    return mapToDTO(entity);
  }

  /**
   * Map a DTO to its corresponding entity
   *
   * @param dto the DTO to map
   * @return the mapped entity
   */
  protected abstract E mapToEntity(D dto);

  /**
   * Map an entity to its corresponding DTO
   *
   * @param entity the entity to map
   * @return the mapped DTO
   */
  protected abstract D mapToDTO(E entity);

  /**
   * Validate the entity on creation
   *
   * @param entity the entity to validate
   * @return true if validation fails (duplicate), false otherwise
   */
  protected abstract boolean validateOnCreate(E entity);

  /**
   * Validate the DTO on update
   *
   * @param dto the DTO to validate
   * @param id  the ID of the entity being updated
   * @return true if validation fails (duplicate), false otherwise
   */
  protected abstract boolean validateOnUpdate(D dto, Long id);

}
