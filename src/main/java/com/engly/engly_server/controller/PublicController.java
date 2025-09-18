package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.response.CategoriesDto;
import com.engly.engly_server.service.common.CategoriesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Tag(name = "05. Public Resources", description = "Endpoints that do not require authentication.")
public class PublicController {
    private final CategoriesService categoriesService;

    public PublicController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @Operation(
            summary = "Get all available categories (paginated)",
            description = """
                    Retrieves a paginated list of all publicly available room categories.
                    
                    The response is structured according to the Spring HATEOAS `PagedModel` format, which includes:
                    - `_embedded.categories`: An array of category objects.
                    - `_links`: Navigation links for `first`, `prev`, `self`, `next`, and `last` pages.
                    - `page`: Metadata about the current page, including size, total elements, total pages, and number.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the list of categories.",
            content = @Content
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error. An unexpected error occurred on the server.",
            content = @Content
    )
    @GetMapping("/get-all-categories")
    public Page<CategoriesDto> getAll(@ParameterObject
                                      @PageableDefault(size = 8, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                                      Pageable pageable) {
        return categoriesService.getAllCategories(pageable);
    }
}
