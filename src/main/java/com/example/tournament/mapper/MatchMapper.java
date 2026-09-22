package com.example.tournament.mapper;

import com.example.tournament.dto.response.MatchResponse;
import com.example.tournament.entity.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .homeTeamName(match.getHomeTeam().getName())
                .awayTeamName(match.getAwayTeam() != null ? match.getAwayTeam().getName() : "Exempté")
                .matchDate(match.getMatchDate())
                .matchTime(match.getMatchTime())
                .venue(match.getVenue())
                .phase(match.getPhase())
                .status(match.getStatus())
                .homeScore(match.getHomeScore())
                .awayScore(match.getAwayScore())
                .winnerTeamName(match.getWinnerTeam() != null ? match.getWinnerTeam().getName() : null)
                .build();
    }
}