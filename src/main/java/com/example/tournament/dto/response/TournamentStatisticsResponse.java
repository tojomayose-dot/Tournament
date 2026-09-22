package com.example.tournament.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TournamentStatisticsResponse {
    private int totalTeams;
    private int totalMatches;
    private int matchesPlayed;
    private int matchesRemaining;
    private int totalGoals;
    private double averageGoalsPerMatch;
}