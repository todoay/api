package com.todoay.api.domain.category.service;

import com.todoay.api.domain.category.dto.*;
import com.todoay.api.domain.category.entity.Category;
import com.todoay.api.domain.category.exception.CategoryNotFoundException;
import com.todoay.api.domain.category.exception.NotYourCategoryException;
import com.todoay.api.domain.category.repository.CategoryRepository;
import com.todoay.api.global.context.LoginAuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCRUDServiceImpl implements CategoryCRUDService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategorySaveResponseDto addCategory(CategorySaveRequestDto dto) {
        return new CategorySaveResponseDto(categoryRepository.save(new Category(dto.getName(), dto.getColor(), dto.getOrderIndex(), LoginAuthContext.getLoginAuth())).getId());
    }

    @Override
    public void modifyCategory(Long id, CategoryModifyRequestDto dto) {
        checkIsPresentAndIsMineAndGetCategory(id).modify(dto.getName(), dto.getColor());
    }

    @Override
    public CategoryListByTokenResponseDto findCategoryByToken() {
        return CategoryListByTokenResponseDto.of(categoryRepository.findCategoryByAuth(LoginAuthContext.getLoginAuth()));
    }

    @Override
    public void modifyOrderIndexes(CategoryOrderIndexModifyDto dto) {
        dto.getOrderIndexes().forEach(i -> checkIsPresentAndIsMineAndGetCategory(i.getId()).changeOrderIndex(i.getOrderIndex()));
    }

    @Override
    public void removeCategory(Long id) {
        categoryRepository.delete(checkIsPresentAndIsMineAndGetCategory(id));
    }

    @Override
    public void endCategory(Long id) {
        checkIsPresentAndIsMineAndGetCategory(id).end();
    }

    private Category checkIsPresentAndIsMineAndGetCategory(Long id) {
        Category category = checkIsPresentAndGetCategory(id);
        checkIsMine(category);
        return category;
    }

    private void checkIsMine(Category category) {
        if(!category.getAuth().equals(LoginAuthContext.getLoginAuth())) throw new NotYourCategoryException();
    }

    private Category checkIsPresentAndGetCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }
}
