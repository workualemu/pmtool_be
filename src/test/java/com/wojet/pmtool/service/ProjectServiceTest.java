package com.wojet.pmtool.service;

import com.wojet.pmtool.model.Client;
import com.wojet.pmtool.model.Project;
import com.wojet.pmtool.model.User;
import com.wojet.pmtool.payload.ProjectDTO;
import com.wojet.pmtool.repository.ClientRepository;
import com.wojet.pmtool.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @InjectMocks
  private ProjectServiceImpl projectService;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private AuditorAware<User> auditorAware;

  private Client client;
  private Project project;
  private ProjectDTO projectDTO;

  @BeforeEach
  void setUp() {
    // Prepare Client
    client = new Client();
    client.setId(1L);
    client.setName("Test Client");

    // Prepare Project entity
    project = new Project();
    project.setId(1L);
    project.setTitle("Test Project");
    project.setClient(client);

    // Prepare ProjectDTO
    projectDTO = new ProjectDTO();
    projectDTO.setTitle("Test Project");

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setFirstName("Test");
    mockUser.setLastName("User");
    // ... set other fields if needed

    when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(mockUser));

    projectService.setAuditorAware(auditorAware);
    projectService.setRepository(projectRepository);

  }

  @Test
  void createProject_shouldReturnSavedProjectDTO() {
    // Arrange
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(projectRepository.findByTitle("Test Project")).thenReturn(null);
    when(modelMapper.map(projectDTO, Project.class)).thenReturn(project);
    when(modelMapper.map(project, ProjectDTO.class)).thenReturn(projectDTO);
    when(projectRepository.save(any(Project.class))).thenReturn(project);

    // Act
    ProjectDTO result = projectService.createProject(1L, projectDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Test Project");

    // Verify that proper methods were called
    verify(clientRepository, times(1)).findById(1L);
    verify(projectRepository, times(1)).save(any(Project.class));
    verify(auditorAware, times(1)).getCurrentAuditor();
  }
}
