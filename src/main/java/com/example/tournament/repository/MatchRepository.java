package com.example.tournament.repository;

import com.example.tournament.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    boolean existsByTournamentId(Long tournamentId);

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByTournamentIdAndRoundNumber(Long tournamentId, Integer roundNumber);
}