package com.example.tournament.controller.rest;

import com.example.tournament.dto.request.MatchResultRequest;
import com.example.tournament.dto.response.MatchResponse;
import com.example.tournament.entity.Match;
import com.example.tournament.mapper.MatchMapper;
import com.example.tournament.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchResultRestController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;

    @PutMapping("/{id}/result")
    public ResponseEntity<MatchResponse> recordResult(
            @PathVariable Long id,
            @Valid @RequestBody MatchResultRequest request) {

        Match match = matchService.recordResult(id, request.getHomeScore(), request.getAwayScore());
        return ResponseEntity.ok(matchMapper.toResponse(match));
    }
}