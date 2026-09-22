package com.example.tournament.repository;

import com.example.tournament.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCaseAndArchivedFalse(String name);

    Page<Team> findByArchivedFalse(Pageable pageable);
}