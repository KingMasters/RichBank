package com.hexagonal.specification;

/**
 * Specification pattern interface for domain business rules
 * @param <T> The type of entity to be specified
 */
public interface Specification<T> {
    /**
     * Checks if the given entity satisfies the specification
     * @param entity The entity to check
     * @return true if the entity satisfies the specification, false otherwise
     */
    boolean isSatisfiedBy(T entity);

    /**
     * Combines this specification with another using AND logic
     * @param other The other specification
     * @return A new specification that represents the AND of both specifications
     */
    default Specification<T> and(Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    /**
     * Combines this specification with another using OR logic
     * @param other The other specification
     * @return A new specification that represents the OR of both specifications
     */
    default Specification<T> or(Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    /**
     * Negates this specification
     * @return A new specification that represents the negation of this specification
     */
    default Specification<T> not() {
        return new NotSpecification<>(this);
    }
}

