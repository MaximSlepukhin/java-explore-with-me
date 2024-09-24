package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.PublicCategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение категорий.");
        int page = from / size;
        int offset = from % size;
        Pageable pageable = PageRequest.of(page, size);
        return publicCategoryService.getCategories(pageable, offset, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable Integer catId) {
        log.info("GET запрос на получение категории c id:" + catId);
        return publicCategoryService.getCategory(catId);
    }
}
