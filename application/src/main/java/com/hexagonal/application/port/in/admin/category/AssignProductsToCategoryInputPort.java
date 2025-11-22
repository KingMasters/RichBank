package com.hexagonal.application.port.in.admin.category;

import com.hexagonal.vo.ID;
import java.util.Set;

public interface AssignProductsToCategoryInputPort {
    void execute(ID categoryId, Set<ID> productIds);
}