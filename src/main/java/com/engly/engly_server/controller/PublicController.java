package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.CategoriesDto;
import com.engly.engly_server.service.CategoriesService;
import com.engly.engly_server.utils.pagging.PageConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final CategoriesService categoriesService;

    public PublicController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0", required = false) Integer page,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(new PageConfig<CategoriesDto>()
                .response(categoriesService.getAllCategories(PageRequest.of(page, size)), CategoriesDto.class), HttpStatus.OK);
    }
}
