package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.config.AbstractTestcontainersConfiguration;
import com.engly.engly_server.config.TestJpaConfiguration;
import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.request.CategoryRequest;
import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.entity.Categories;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.service.common.CategoriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {TestJpaConfiguration.class, CategoryServiceImpl.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class CategoryServiceImplTest extends AbstractTestcontainersConfiguration {

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private CategoriesRepository categoriesRepository;

    private Categories testCategory;
    private CategoryRequest validCategoryRequest;

    @BeforeEach
    void setUp() {
        categoriesRepository.deleteAll();

        testCategory = Categories.builder()
                .name(CategoryType.TECH)
                .description("Technology and programming discussions")
                .createdAt(Instant.now())
                .build();

        validCategoryRequest = new CategoryRequest(CategoryType.HOBBIES, "Hobbies and interests");
    }

    @Test
    @DisplayName("Should add category successfully")
    void addCategory_ValidRequest_ReturnsCreatedCategory() {
        // Act
        CategoriesDto result = categoriesService.addCategory(validCategoryRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name().toLowerCase()).isEqualTo(CategoryType.HOBBIES.name().toLowerCase());
        assertThat(result.description()).isEqualTo("Hobbies and interests");
        assertThat(result.icon()).isEqualTo(CategoryType.HOBBIES.getIcon());

        // Verify in database
        Optional<Categories> savedCategory = categoriesRepository.findById(result.id());
        assertThat(savedCategory).isPresent();
        assertThat(savedCategory.get().getName()).isEqualTo(CategoryType.HOBBIES);
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate category")
    void addCategory_DuplicateName_ThrowsEntityAlreadyExistsException() {
        // Arrange
        categoriesRepository.save(testCategory);
        CategoryRequest duplicateRequest = new CategoryRequest(CategoryType.TECH, "Another tech category");

        // Act & Assert
        assertThatThrownBy(() -> categoriesService.addCategory(duplicateRequest))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("Category with name " + CategoryType.TECH + " already exists");
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_ValidRequest_ReturnsUpdatedCategory() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);
        CategoryRequest updateRequest = new CategoryRequest(CategoryType.SPORTS, "Updated description");

        // Act
        CategoriesDto result = categoriesService.updateCategory(savedCategory.getId(), updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedCategory.getId());
        assertThat(result.name().toLowerCase()).isEqualTo(CategoryType.SPORTS.name().toLowerCase());
        assertThat(result.description()).isEqualTo("Updated description");

        // Verify in database
        Categories updatedCategory = categoriesRepository.findById(savedCategory.getId()).orElseThrow();
        assertThat(updatedCategory.getName()).isEqualTo(CategoryType.SPORTS);
        assertThat(updatedCategory.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should update only description when name is null")
    void updateCategory_OnlyDescription_UpdatesOnlyDescription() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);
        CategoryRequest updateRequest = new CategoryRequest(null, "New description only");

        // Act
        CategoriesDto result = categoriesService.updateCategory(savedCategory.getId(), updateRequest);

        // Assert
        assertThat(result.name().toLowerCase()).isEqualTo(CategoryType.TECH.name().toLowerCase()); // unchanged
        assertThat(result.description()).isEqualTo("New description only");
    }

    @Test
    @DisplayName("Should update only name when description is null")
    void updateCategory_OnlyName_UpdatesOnlyName() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);
        CategoryRequest updateRequest = new CategoryRequest(CategoryType.MOVIES, null);

        // Act
        CategoriesDto result = categoriesService.updateCategory(savedCategory.getId(), updateRequest);

        // Assert
        assertThat(result.name().toLowerCase()).isEqualTo(CategoryType.MOVIES.name().toLowerCase());
        assertThat(result.description()).isEqualTo("Technology and programming discussions"); // unchanged
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void updateCategory_NonExistentCategory_ThrowsNotFoundException() {
        // Arrange
        String nonExistentId = "non-existent-id";
        CategoryRequest updateRequest = new CategoryRequest(CategoryType.NEWS, "News updates");

        // Act & Assert
        assertThatThrownBy(() -> categoriesService.updateCategory(nonExistentId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentId);
    }

    @Test
    @DisplayName("Should get all categories with pagination")
    void getAllCategories_ValidPageable_ReturnsPagedCategories() {
        // Arrange
        Categories category1 = Categories.builder()
                .name(CategoryType.TECH)
                .description("Tech category")
                .createdAt(Instant.now())
                .build();
        Categories category2 = Categories.builder()
                .name(CategoryType.SPORTS)
                .description("Sports category")
                .createdAt(Instant.now())
                .build();
        categoriesRepository.saveAll(List.of(category1, category2));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CategoriesDto> result = categoriesService.getAllCategories(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(cont -> cont.name().toLowerCase())
                .containsExactlyInAnyOrder(CategoryType.TECH.name().toLowerCase(), CategoryType.SPORTS.name().toLowerCase());
    }

    @Test
    @DisplayName("Should return empty page when no categories exist")
    void getAllCategories_NoCategories_ReturnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CategoriesDto> result = categoriesService.getAllCategories(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void getCategoryById_ValidId_ReturnsCategory() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);

        // Act
        CategoriesDto result = categoriesService.getCategoryById(savedCategory.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(savedCategory.getId());
        assertThat(result.name().toLowerCase()).isEqualTo(CategoryType.TECH.name().toLowerCase());
        assertThat(result.description()).isEqualTo("Technology and programming discussions");
    }

    @Test
    @DisplayName("Should throw exception when getting category by non-existent ID")
    void getCategoryById_NonExistentId_ThrowsNotFoundException() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert
        assertThatThrownBy(() -> categoriesService.getCategoryById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentId);
    }

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_ValidId_DeletesCategory() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);
        String categoryId = savedCategory.getId();

        // Act
        categoriesService.deleteCategory(categoryId);

        // Assert
        Optional<Categories> deletedCategory = categoriesRepository.findById(categoryId);
        assertThat(deletedCategory).isEmpty();
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent category")
    void deleteCategory_NonExistentId_DoesNotThrowException() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert
        assertDoesNotThrow(() -> categoriesService.deleteCategory(nonExistentId));
    }

    @Test
    @DisplayName("Should find category by name successfully")
    void findByName_ValidName_ReturnsCategory() {
        // Arrange
        categoriesRepository.save(testCategory);

        // Act
        Categories result = categoriesService.findByName(CategoryType.TECH);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(CategoryType.TECH);
        assertThat(result.getDescription()).isEqualTo("Technology and programming discussions");
    }

    @Test
    @DisplayName("Should throw exception when finding category by non-existent name")
    void findByName_NonExistentName_ThrowsNotFoundException() {
        // Act & Assert
        assertThatThrownBy(() -> categoriesService.findByName(CategoryType.MOVIES))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(CategoryType.MOVIES.name());
    }

    @Test
    @DisplayName("Should find category entity by ID successfully")
    void findCategoryEntityById_ValidId_ReturnsCategory() {
        // Arrange
        Categories savedCategory = categoriesRepository.save(testCategory);

        // Act
        Categories result = categoriesService.findCategoryEntityById(savedCategory.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedCategory.getId());
        assertThat(result.getName()).isEqualTo(CategoryType.TECH);
        assertThat(result.getDescription()).isEqualTo("Technology and programming discussions");
    }

    @Test
    @DisplayName("Should throw exception when finding category entity by non-existent ID")
    void findCategoryEntityById_NonExistentId_ThrowsNotFoundException() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert
        assertThatThrownBy(() -> categoriesService.findCategoryEntityById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(nonExistentId);
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void getAllCategories_WithPagination_ReturnsCorrectPage() {
        // Arrange
        List<Categories> categories = List.of(
                Categories.builder().name(CategoryType.TECH).description("Tech").createdAt(Instant.now()).build(),
                Categories.builder().name(CategoryType.SPORTS).description("Sports").createdAt(Instant.now()).build(),
                Categories.builder().name(CategoryType.NEWS).description("News").createdAt(Instant.now()).build(),
                Categories.builder().name(CategoryType.MOVIES).description("Movies").createdAt(Instant.now()).build()
        );
        categoriesRepository.saveAll(categories);

        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        // Act
        Page<CategoriesDto> firstPageResult = categoriesService.getAllCategories(firstPage);
        Page<CategoriesDto> secondPageResult = categoriesService.getAllCategories(secondPage);

        // Assert
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(4);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(firstPageResult.isLast()).isFalse();

        assertThat(secondPageResult.getContent()).hasSize(2);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(4);
        assertThat(secondPageResult.getTotalPages()).isEqualTo(2);
        assertThat(secondPageResult.isFirst()).isFalse();
        assertThat(secondPageResult.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should validate category creation with all enum values")
    void addCategory_AllCategoryTypes_CreatesSuccessfully() {
        // Test creating categories for all enum values
        for (CategoryType categoryType : CategoryType.values()) {
            CategoryRequest request = new CategoryRequest(categoryType, "Description for " + categoryType.getVal());

            CategoriesDto result = categoriesService.addCategory(request);

            assertThat(result.name().toLowerCase()).isEqualTo(categoryType.getVal().toLowerCase());
            assertThat(result.icon()).isEqualTo(categoryType.getIcon());
            assertThat(result.description()).isEqualTo("Description for " + categoryType.getVal());
        }

        // Verify all categories were created
        Page<CategoriesDto> allCategories = categoriesService.getAllCategories(PageRequest.of(0, 20));
        assertThat(allCategories.getTotalElements()).isEqualTo(CategoryType.values().length);
    }

    @Test
    @DisplayName("Should handle concurrent category creation")
    @Transactional
    void addCategory_ConcurrentCreation_HandlesCorrectly() {
        // This test simulates concurrent creation attempts
        CategoryRequest request1 = new CategoryRequest(CategoryType.GENERAL_CHAT, "First attempt");
        CategoryRequest request2 = new CategoryRequest(CategoryType.GENERAL_CHAT, "Second attempt");

        // First creation should succeed
        CategoriesDto result1 = categoriesService.addCategory(request1);
        assertThat(result1).isNotNull();

        assertThatThrownBy(() -> categoriesService.addCategory(request2))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }
}