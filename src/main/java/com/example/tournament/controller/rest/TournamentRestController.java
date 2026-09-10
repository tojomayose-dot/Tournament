package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.CreateTournamentRequest;
import com.example.tournament.dto.request.UpdateTournamentRequest;
import com.example.tournament.dto.response.TournamentResponse;
import com.example.tournament.entity.Tournament;
import com.example.tournament.mapper.TournamentMapper;
import com.example.tournament.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentRestController {

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @Valid @RequestBody CreateTournamentRequest request) {

        Tournament tournament = tournamentMapper.toEntity(request);
        Tournament created = tournamentService.createTournament(tournament);
        TournamentResponse response = tournamentMapper.toResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournamentById(@PathVariable Long id) {

        Tournament tournament = tournamentService.getTournamentById(id);
        TournamentResponse response = tournamentMapper.toResponse(tournament);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TournamentResponse>> getAllTournaments(Pageable pageable) {

        Page<TournamentResponse> page = tournamentService.getAllTournaments(pageable)
                .map(tournamentMapper::toResponse);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTournamentRequest request) {

        Tournament updatedData = tournamentMapper.toEntity(request);
        Tournament updated = tournamentService.updateTournament(id, updatedData);
        TournamentResponse response = tournamentMapper.toResponse(updated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}