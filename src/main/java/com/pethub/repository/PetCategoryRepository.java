package com.pethub.repository;

import com.pethub.entity.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetCategoryRepository extends JpaRepository<PetCategory, Long> {
    Optional<PetCategory> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
