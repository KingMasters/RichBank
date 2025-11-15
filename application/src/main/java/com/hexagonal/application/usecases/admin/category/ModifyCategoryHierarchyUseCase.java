package com.hexagonal.application.usecases.admin.category;

import com.hexagonal.vo.ID;

public interface ModifyCategoryHierarchyUseCase {
    void setParentCategory(ID categoryId, ID parentCategoryId);
    void removeParentCategory(ID categoryId);
}