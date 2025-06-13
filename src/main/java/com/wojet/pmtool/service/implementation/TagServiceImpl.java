package com.wojet.pmtool.service.implementation;

import java.util.List;
import java.util.function.Consumer;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.Tag;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.TagDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import com.wojet.pmtool.repository.TagRepository;
import com.wojet.pmtool.service.GenericCrudService;
import com.wojet.pmtool.service.TagService;

@Service
public class TagServiceImpl extends GenericCrudService<Tag, TagDTO, TagRepository>
    implements TagService {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ModelMapper modelMapper;

  /**
   * Get all tags by project with pagination and sorting
   */
  public PagedResponse<TagDTO> getTagsByProject(Long projectId, Integer pageNumber, Integer pageSize, String sortBy,
      String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    Page<Tag> page = tagRepository.findByProjectId(projectId, pageable);

    List<Tag> entities = page.getContent();
    if (entities.isEmpty()) {
      throw new APIException("No records found!");
    }

    List<TagDTO> dtos = entities.stream().map(this::mapToDTO).toList();

    PagedResponse<TagDTO> response = new PagedResponse<>();
    response.setContent(dtos);
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLastPage(page.isLast());

    return response;
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TagDTO createTag(Long projectId, TagDTO tagDTO) {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", project.getClient().getId()));

    Consumer<Tag> associations = tag -> {
      tag.setProject(project);
      tag.setClient(client);
    };

    return createWithAssociations(tagDTO, associations);
  }

  /**
   * Create a new project with associated client
   */
  @Override
  public TagDTO updateTag(Long tagId, TagDTO tagDTO) {
    Tag existingTag = tagRepository.findById(tagId)
        .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

    Long projectId = tagDTO.getProjectId() == null ? existingTag.getProject().getId() : tagDTO.getProjectId();
    Project project = projectRepository.findById(
        projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Client client = clientRepository.findById(
        project.getClient().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

    Consumer<Tag> associations = tag -> {
      tag.setProject(project);
      tag.setClient(client);
    };

    return updateWithAssociations(tagId, tagDTO, associations);
  }

  @Override
  public String deleteByProjectId(Long projectId) {
    tagRepository.deleteByProjectId(projectId);
    return "All records deleted successfully!";
  }

  /**
   * Map DTO to entity
   */
  @Override
  protected Tag mapToEntity(TagDTO dto) {
    return modelMapper.map(dto, Tag.class);
  }

  /**
   * Map entity to DTO
   */
  @Override
  protected TagDTO mapToDTO(Tag entity) {
    return modelMapper.map(entity, TagDTO.class);
  }

  /**
   * Duplicate check logic for create
   */
  @Override
  protected boolean validateOnCreate(Tag entity) {
    return tagRepository.findByProjectIdAndLabel(entity.getProject().getId(), entity.getLabel()) != null;
  }

  /**
   * Duplicate check logic for update
   */
  @Override
  protected boolean validateOnUpdate(TagDTO dto, Long id) {
    Tag existing = tagRepository.findByProjectIdAndLabel(dto.getProjectId(), dto.getLabel());
    return existing != null && !existing.getId().equals(id);
  }

}
