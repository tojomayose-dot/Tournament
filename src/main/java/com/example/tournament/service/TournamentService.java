package com.example.tournament.service;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TournamentService {

    Tournament createTournament(Tournament tournament);

    Tournament getTournamentById(Long id);

    Page<Tournament> getAllTournaments(Pageable pageable);

    Page<Tournament> searchTournaments(String sport, TournamentStatus status, Pageable pageable);

    Tournament updateTournament(Long id, Tournament updatedData);

    void deleteTournament(Long id);
}