package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Kiểu ID của Category là Integer
}