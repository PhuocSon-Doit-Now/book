package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
}