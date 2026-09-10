package com.example.tournament.dto.response;

import com.example.tournament.entity.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class MatchResponse {

    private Long id;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDate matchDate;
    private LocalTime matchTime;
    private String venue;
    private String phase;
    private MatchStatus status;
    private Integer homeScore;
    private Integer awayScore;
    private String winnerTeamName;
}