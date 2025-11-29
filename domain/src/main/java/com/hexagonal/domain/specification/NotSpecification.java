package com.hexagonal.domain.specification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotSpecification<T> implements Specification<T> {
    private final Specification<T> specification;

    @Override
    public boolean isSatisfiedBy(T entity) {
        return !specification.isSatisfiedBy(entity);
    }
}

