package com.example.tournament.repository;

import com.example.tournament.entity.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {

    boolean existsByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    List<TournamentRegistration> findByTournamentId(Long tournamentId);

    List<TournamentRegistration> findByTeamId(Long teamId);
}