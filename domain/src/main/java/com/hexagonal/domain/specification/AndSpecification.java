package com.hexagonal.domain.specification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AndSpecification<T> implements Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;

    @Override
    public boolean isSatisfiedBy(T entity) {
        return left.isSatisfiedBy(entity) && right.isSatisfiedBy(entity);
    }
}

