package com.hexagonal.entity;

import com.hexagonal.vo.ID;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Category {
    private final ID id;
    private String name;
    private String description;
    private ID parentCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private Category(ID id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        this.id = id;
        this.name = name.trim();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Category create(String name) {
        return new Category(ID.generate(), name);
    }

    public static Category of(ID id, String name) {
        return new Category(id, name);
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.name = name.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void setParentCategory(ID parentCategoryId) {
        if (parentCategoryId != null && parentCategoryId.equals(this.id)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        this.parentCategoryId = parentCategoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeParentCategory() {
        this.parentCategoryId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isRootCategory() {
        return parentCategoryId == null;
    }
}
