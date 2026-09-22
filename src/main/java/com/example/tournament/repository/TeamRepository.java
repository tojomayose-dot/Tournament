package com.example.tournament.repository;

import com.example.tournament.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCaseAndArchivedFalse(String name);

    Page<Team> findByArchivedFalse(Pageable pageable);

    @Query("SELECT t FROM Team t WHERE t.archived = false AND (:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.city) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Team> searchTeams(@Param("keyword") String keyword, Pageable pageable);
}