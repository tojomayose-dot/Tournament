package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.MatchResultRequest;
import com.example.tournament.dto.response.MatchResponse;
import com.example.tournament.entity.Match;
import com.example.tournament.mapper.MatchMapper;
import com.example.tournament.service.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.tournament.dto.request.MatchResultRequest;
import jakarta.validation.Valid;
import com.example.tournament.dto.response.TeamRankingResponse;
import com.example.tournament.dto.response.TournamentStatisticsResponse;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}")
@RequiredArgsConstructor
public class MatchRestController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @PostMapping("/generate-matches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MatchResponse>> generateMatches(@PathVariable Long tournamentId) {

        List<MatchResponse> matches = matchService.generateMatches(tournamentId).stream()
                .map(matchMapper::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(matches);
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchResponse>> getMatches(@PathVariable Long tournamentId) {

        List<MatchResponse> matches = matchService.getMatchesByTournament(tournamentId).stream()
                .map(matchMapper::toResponse)
                .toList();

        return ResponseEntity.ok(matches);
    }
    @GetMapping("/ranking")
    public ResponseEntity<List<TeamRankingResponse>> getRanking(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.getRanking(tournamentId));
    }

    @PutMapping("/matches/{matchId}/result")
    public ResponseEntity<MatchResponse> recordResult(
            @PathVariable Long matchId,
            @Valid @RequestBody MatchResultRequest request) {

        Match match = matchService.recordResult(matchId, request.getHomeScore(), request.getAwayScore());
        return ResponseEntity.ok(matchMapper.toResponse(match));
    }

    @GetMapping("/statistics")
    public ResponseEntity<TournamentStatisticsResponse> getStatistics(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.getStatistics(tournamentId));
    }
}