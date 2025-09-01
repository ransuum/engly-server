package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.config.AbstractTestcontainersConfiguration;
import com.engly.engly_server.config.DataJpaTestWithContainer;
import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.request.RoomRequest;
import com.engly.engly_server.models.dto.request.RoomSearchCriteriaRequest;
import com.engly.engly_server.models.dto.request.RoomUpdateRequest;
import com.engly.engly_server.models.dto.response.RoomsDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.enums.Provider;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.repository.ChatParticipantRepository;
import com.engly.engly_server.repository.RoomRepository;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.AuthenticatedUserProvider;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DataJpaTestWithContainer
@Import(RoomServiceImpl.class)
class RoomServiceImplTest extends AbstractTestcontainersConfiguration {

    @Autowired
    private RoomServiceImpl roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private ChatParticipantRepository chatParticipantsRepository;

    @MockitoBean
    private AuthenticatedUserProvider authenticatedUserProvider;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CategoriesService categoriesService;

    @MockitoBean
    private ChatParticipantsService chatParticipantsService;

    private Users testUser;
    private Categories testCategory;
    private Rooms testRoom;

    @BeforeEach
    void setUp() {
        // Clear repositories
        chatParticipantsRepository.deleteAll();
        roomRepository.deleteAll();
        usersRepository.deleteAll();
        categoriesRepository.deleteAll();

        // Create test data
        testUser = createTestUser();
        testCategory = createTestCategory();
        testRoom = createTestRoom();
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(getPostgreSQLContainer().isRunning());
        assertTrue(getPostgreSQLContainer().isCreated());
    }

    @Test
    void createRoom_Success() {
        // Given
        RoomRequest roomRequest = new RoomRequest("New Test Room", "Test Description");
        String userId = testUser.getId();

        when(userService.findEntityById(userId)).thenReturn(testUser);
        when(categoriesService.findByName(CategoryType.GENERAL_CHAT)).thenReturn(testCategory);
        doNothing().when(chatParticipantsService).addParticipant(any(Rooms.class), any(Users.class), eq(RoomRoles.ADMIN));

        // When
        RoomsDto result = roomService.createRoom(userId, CategoryType.GENERAL_CHAT, roomRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New Test Room");
        assertThat(result.description()).isEqualTo("Test Description");

        // Verify room was saved to database
        Optional<Rooms> savedRoom = roomRepository.findById(result.id());
        assertThat(savedRoom).isPresent();
        assertThat(savedRoom.get().getCreator().getId()).isEqualTo(testUser.getId());

        verify(chatParticipantsService).addParticipant(any(Rooms.class), eq(testUser), eq(RoomRoles.ADMIN));
    }

    @Test
    void createRoom_ThrowsEntityAlreadyExistsException_WhenRoomNameExists() {
        // Given
        RoomRequest roomRequest = new RoomRequest(testRoom.getName(), "Different Description");
        String userId = testUser.getId();

        when(userService.findEntityById(userId)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> roomService.createRoom(userId, CategoryType.GENERAL_CHAT, roomRequest))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void findAllWithCriteria_Success() {
        // Given
        RoomSearchCriteriaRequest searchRequest = RoomSearchCriteriaRequest.builder()
                .keyword("Test")
                .categoryType(CategoryType.GENERAL_CHAT)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        var result = roomService.findAllWithCriteria(searchRequest, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().getFirst().name()).contains("Test");
    }

    @Test
    void findAllWithCriteria_WithDateFilters() {
        // Given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        RoomSearchCriteriaRequest searchRequest = RoomSearchCriteriaRequest.builder()
                .createdAfter(yesterday)
                .createdBefore(tomorrow)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        var result = roomService.findAllWithCriteria(searchRequest, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void deleteRoomById_Success() {
        // Given
        String roomId = testRoom.getId();

        // When
        roomService.deleteRoomById(roomId);

        // Then
        Optional<Rooms> deletedRoom = roomRepository.findById(roomId);
        assertThat(deletedRoom).isEmpty();
    }

    @Test
    void deleteRoomById_ThrowsNotFoundException_WhenRoomNotExists() {
        // Given
        String nonExistentRoomId = "non-existent-id";

        // When & Then
        assertThatThrownBy(() -> roomService.deleteRoomById(nonExistentRoomId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateRoom_Success() {
        // Given
        String roomId = testRoom.getId();
        RoomUpdateRequest updateRequest = new RoomUpdateRequest(
                "Updated Room Name",
                "Updated Description",
                CategoryType.TECH,
                "newcreator@example.com"
        );

        Users newCreator = createTestUser("newcreator@example.com", "newcreator");
        Categories techCategory = createTestCategory(CategoryType.TECH);

        when(categoriesService.findByName(CategoryType.TECH)).thenReturn(techCategory);
        when(userService.findUserEntityByEmail("newcreator@example.com")).thenReturn(newCreator);

        // When
        RoomsDto result = roomService.updateRoom(roomId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Updated Room Name");
        assertThat(result.description()).isEqualTo("Updated Description");
    }

    @Test
    void updateRoom_ThrowsNotFoundException_WhenRoomNotExists() {
        // Given
        String nonExistentRoomId = "non-existent-id";
        RoomUpdateRequest updateRequest = new RoomUpdateRequest(
                "Updated Name", "Updated Description", null, null
        );

        // When & Then
        assertThatThrownBy(() -> roomService.updateRoom(nonExistentRoomId, updateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllRoomsByCategoryType_Success() {
        // Given
        CategoryType categoryType = CategoryType.GENERAL_CHAT;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        var result = roomService.findAllRoomsByCategoryType(categoryType, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void findRoomEntityById_Success() {
        // Given
        String roomId = testRoom.getId();

        // When
        Rooms result = roomService.findRoomEntityById(roomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roomId);
        assertThat(result.getName()).isEqualTo(testRoom.getName());
    }

    @Test
    void findRoomEntityById_ThrowsNotFoundException_WhenRoomNotExists() {
        // Given
        String nonExistentRoomId = "non-existent-id";

        // When & Then
        assertThatThrownBy(() -> roomService.findRoomEntityById(nonExistentRoomId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAllWithCriteria_EmptyResults() {
        // Given
        RoomSearchCriteriaRequest searchRequest = RoomSearchCriteriaRequest.builder()
                .keyword("123123123123")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        var result = roomService.findAllWithCriteria(searchRequest, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void createRoom_WithNullDescription() {
        // Given
        RoomRequest roomRequest = new RoomRequest("Room Without Description", null);
        String userId = testUser.getId();

        when(userService.findEntityById(userId)).thenReturn(testUser);
        when(categoriesService.findByName(CategoryType.GENERAL_CHAT)).thenReturn(testCategory);
        doNothing().when(chatParticipantsService).addParticipant(any(Rooms.class), any(Users.class), eq(RoomRoles.ADMIN));

        // When
        RoomsDto result = roomService.createRoom(userId, CategoryType.GENERAL_CHAT, roomRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Room Without Description");
        assertThat(result.description()).isNull();
    }

    @Test
    void updateRoom_PartialUpdate() {
        // Given
        String roomId = testRoom.getId();
        RoomUpdateRequest updateRequest = new RoomUpdateRequest(
                "Only Name Updated",
                null,
                null,
                null
        );

        // When
        RoomsDto result = roomService.updateRoom(roomId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Only Name Updated");
        assertThat(result.description()).isEqualTo(testRoom.getDescription());
    }

    // Helper methods
    private Users createTestUser() {
        return createTestUser("test@example.com", "testuser");
    }

    private Users createTestUser(String email, String username) {
        Users user = Users.builder()
                .username(username)
                .email(email)
                .password("password123")
                .roles("ROLE_USER")
                .provider(Provider.GOOGLE)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return usersRepository.save(user);
    }

    private Categories createTestCategory() {
        return createTestCategory(CategoryType.GENERAL_CHAT);
    }

    private Categories createTestCategory(CategoryType categoryType) {
        Categories category = Categories.builder()
                .name(categoryType)
                .description("Test category for " + categoryType.name())
                .build();
        return categoriesRepository.save(category);
    }

    private Rooms createTestRoom() {
        Rooms room = Rooms.builder()
                .name("Test Room")
                .description("Test Room Description")
                .creator(testUser)
                .category(testCategory)
                .createdAt(Instant.now())
                .build();
        return roomRepository.save(room);
    }
}