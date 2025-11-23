package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.vo.ID;
import java.util.Set;

public interface AssignProductsToCategoryUseCase {
    void execute(ID categoryId, Set<ID> productIds);
}