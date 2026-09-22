package com.example.tournament.repository;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>,
        JpaSpecificationExecutor<Tournament> {

    List<Tournament> findByStatus(TournamentStatus status);

    Page<Tournament> findByArchivedFalse(Pageable pageable);

    List<Tournament> findBySportIgnoreCase(String sport);

    boolean existsByNameIgnoreCaseAndArchivedFalse(String name);
}