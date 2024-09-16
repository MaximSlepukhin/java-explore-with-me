package ru.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getCategories(Pageable pageable, Integer offset, Integer size);

    CategoryDto getCategory(Long catId);
}
