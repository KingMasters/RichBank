package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.vo.ID;

public interface ModifyCategoryHierarchyInputPort {
    void setParentCategory(ID categoryId, ID parentCategoryId);
    void removeParentCategory(ID categoryId);
}