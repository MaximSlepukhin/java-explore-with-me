package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Pageable pageable, Integer offset, Integer size) {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        if (categoryDtos.size() > offset) {
            return categoryDtos.subList(offset, Math.min(offset + size, categoryDtos.size()));
        } else {
            return List.of();
        }
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + catId + " не нейдена."));
        return CategoryMapper.toCategoryDto(category);
    }
}
