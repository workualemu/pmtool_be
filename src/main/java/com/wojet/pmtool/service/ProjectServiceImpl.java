package com.wojet.pmtool.service;

import com.wojet.pmtool.exception.APIException;
import com.wojet.pmtool.exception.ResourceNotFoundException;
import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.payload.PagedResponse;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;

import java.util.List;
import java.util.function.Consumer;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends GenericCrudService<Project, ProjectDTO, ProjectRepository>
        implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Get all projects by client with pagination and sorting
     */
    public PagedResponse<ProjectDTO> getProjectsByClient(Long clientId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Project> page = projectRepository.findByClientId(clientId, pageable);

        List<Project> entities = page.getContent();
        if (entities.isEmpty()) {
        throw new APIException("No records found!");
        }

        List<ProjectDTO> dtos = entities.stream().map(this::mapToDTO).toList();

        PagedResponse<ProjectDTO> response = new PagedResponse<>();
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
    public ProjectDTO createProject(Long clientId, ProjectDTO projectDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        List<Consumer<Project>> associations = List.of(
                project -> project.setClient(client));
        return createWithAssociations(projectDTO, associations);
    }

    /**
     * Create a new project with associated client
     */
    @Override
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        Client client = clientRepository.findById(
                existingProject.getClient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", existingProject.getClient().getId()));

        List<Consumer<Project>> associations = List.of(
                project -> project.setClient(client)
        );

        return updateWithAssociations(projectId, projectDTO, associations);
    }

    public String deleteByClientId(Long clientId) {
        projectRepository.deleteByClientId(clientId);
        return "All records deleted successfully!";
    }

    /**
     * Map DTO to entity
     */
    @Override
    protected Project mapToEntity(ProjectDTO dto) {
        return modelMapper.map(dto, Project.class);
    }

    /**
     * Map entity to DTO
     */
    @Override
    protected ProjectDTO mapToDTO(Project entity) {
        return modelMapper.map(entity, ProjectDTO.class);
    }

    /**
     * Duplicate check logic for create
     */
    @Override
    protected boolean validateOnCreate(Project entity) {
        return projectRepository.findByTitle(entity.getTitle()) != null;
    }

    /**
     * Duplicate check logic for update
     */
    @Override
    protected boolean validateOnUpdate(ProjectDTO dto, Long id) {
        Project existing = projectRepository.findByTitle(dto.getTitle());
        return existing != null && !existing.getId().equals(id);
    }

}
