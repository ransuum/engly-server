package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.models.request.createrequests.CategoryRequest;
import com.engly.engly_server.service.CategoriesService;
import com.engly.engly_server.utils.pagging.PageConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoriesService categoriesService;

    public CategoryController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @PreAuthorize("hasAuthority('SCOPE_CREATE_CATEGORY')")
    @PostMapping("/create")
    public ResponseEntity<CategoriesDto> addCategory(@RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoriesService.addCategory(categoryRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_UPDATE_CATEGORY')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriesDto> updateCategory(@PathVariable String id, @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoriesService.updateCategory(id, categoryRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE_CATEGORY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoriesService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(new PageConfig<CategoriesDto>()
                .response(categoriesService.getAllCategories(PageRequest.of(page, size)), CategoriesDto.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriesDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoriesService.getCategoryById(id));
    }
}
