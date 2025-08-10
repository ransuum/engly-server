package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.models.dto.request.CategoryRequest;
import com.engly.engly_server.service.common.CategoriesService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@Tag(name = "06. Categories", description = "APIs for managing room categories.")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoriesService categoriesService;

    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @Operation(summary = "Create a new category")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriesDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid category data provided.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_CREATE_GLOBAL'.", content = @Content)
    })
    @PreAuthorize("hasAuthority('SCOPE_CREATE_GLOBAL')")
    @PostMapping
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<CategoriesDto> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.status(201).body(categoriesService.addCategory(categoryRequest));
    }

    @Operation(summary = "Get a category by its ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriesDto.class))
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content),
    })
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public CategoriesDto getCategoryById(
            @Parameter(description = "The unique identifier of the category.", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
            @PathVariable String id) {

        return categoriesService.getCategoryById(id);
    }

    @Operation(summary = "Update an existing category")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoriesDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid category data provided.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_UPDATE_GLOBAL'.", content = @Content),
    })
    @PreAuthorize("hasAuthority('SCOPE_UPDATE_GLOBAL')")
    @PutMapping("/{id}")
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<CategoriesDto> updateCategory(
            @Parameter(description = "The ID of the category to update.")
            @PathVariable String id,
            @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity.ok(categoriesService.updateCategory(id, categoryRequest));
    }

    @Operation(summary = "Delete a category by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_DELETE_GLOBAL'.", content = @Content),
    })
    @PreAuthorize("hasAuthority('SCOPE_DELETE_GLOBAL')")
    @DeleteMapping("/{id}")
    @RateLimiter(name = "CategoryController")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "The ID of the category to delete.")
            @PathVariable String id) {

        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
