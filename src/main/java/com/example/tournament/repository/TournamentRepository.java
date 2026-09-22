package com.example.tournament.repository;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>,
        JpaSpecificationExecutor<Tournament> {

    List<Tournament> findByStatus(TournamentStatus status);

    List<Tournament> findBySportIgnoreCase(String sport);

    boolean existsByNameIgnoreCase(String name);
}