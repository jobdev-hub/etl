package com.jobdev.dataharvest.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobdev.dataharvest.entity.Work;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {
    Optional<Work> findByRefKey(String refKey);
}
