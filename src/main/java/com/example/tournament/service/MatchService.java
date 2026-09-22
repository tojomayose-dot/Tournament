package com.example.tournament.service;

import com.example.tournament.dto.response.TeamRankingResponse;
import com.example.tournament.dto.response.TournamentStatisticsResponse;
import com.example.tournament.entity.Match;

import java.util.List;

public interface MatchService {

    List<Match> generateMatches(Long tournamentId);

    List<Match> getMatchesByTournament(Long tournamentId);

    Match recordResult(Long matchId, Integer homeScore, Integer awayScore);

    List<TeamRankingResponse> getRanking(Long tournamentId);
    
    TournamentStatisticsResponse getStatistics(Long tournamentId);
}