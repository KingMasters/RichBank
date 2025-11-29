package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.domain.vo.ID;

public interface ModifyCategoryHierarchyUseCase {
    void setParentCategory(ID categoryId, ID parentCategoryId);
    void removeParentCategory(ID categoryId);
}