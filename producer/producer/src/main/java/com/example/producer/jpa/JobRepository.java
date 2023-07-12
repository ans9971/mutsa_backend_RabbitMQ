package com.example.producer.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<JobEntity, String> {
    Optional<JobEntity> findByJobId(String id);
}
