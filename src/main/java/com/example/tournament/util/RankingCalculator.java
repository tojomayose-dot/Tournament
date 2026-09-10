package com.example.tournament.util;

import com.example.tournament.entity.Match;
import com.example.tournament.entity.MatchStatus;
import com.example.tournament.entity.Team;

import java.util.*;

public class RankingCalculator {

    /**
     * Représente les statistiques accumulées d'une équipe, avant transformation en DTO.
     * Objet interne mutable utilisé uniquement pendant le calcul.
     */
    public static class TeamStats {
        public Team team;
        public int played = 0;
        public int won = 0;
        public int drawn = 0;
        public int lost = 0;
        public int goalsFor = 0;
        public int goalsAgainst = 0;
        public int points = 0;
    }

    /**
     * Calcule le classement à partir de la liste des équipes inscrites
     * et de la liste des matchs déjà joués (les matchs non terminés sont ignorés).
     * Points : victoire = 3, nul = 1, défaite = 0 (règle standard football).
     */
    public static List<TeamStats> computeStats(List<Team> teams, List<Match> matches) {

        Map<Long, TeamStats> statsByTeamId = new LinkedHashMap<>();

        for (Team team : teams) {
            TeamStats stats = new TeamStats();
            stats.team = team;
            statsByTeamId.put(team.getId(), stats);
        }

        for (Match match : matches) {
            if (match.getStatus() != MatchStatus.TERMINE) {
                continue;
            }

            TeamStats homeStats = statsByTeamId.get(match.getHomeTeam().getId());
            TeamStats awayStats = statsByTeamId.get(match.getAwayTeam().getId());

            if (homeStats == null || awayStats == null) {
                continue;
            }

            int homeScore = match.getHomeScore();
            int awayScore = match.getAwayScore();

            homeStats.played++;
            awayStats.played++;
            homeStats.goalsFor += homeScore;
            homeStats.goalsAgainst += awayScore;
            awayStats.goalsFor += awayScore;
            awayStats.goalsAgainst += homeScore;

            if (homeScore > awayScore) {
                homeStats.won++;
                homeStats.points += 3;
                awayStats.lost++;
            } else if (awayScore > homeScore) {
                awayStats.won++;
                awayStats.points += 3;
                homeStats.lost++;
            } else {
                homeStats.drawn++;
                awayStats.drawn++;
                homeStats.points += 1;
                awayStats.points += 1;
            }
        }

        return new ArrayList<>(statsByTeamId.values());
    }

    /**
     * Trie une liste de statistiques d'équipes selon les critères standards :
     * 1. Points (décroissant)
     * 2. Différence de buts (décroissant)
     * 3. Buts marqués (décroissant)
     */
    public static void sortByStandardCriteria(List<TeamStats> statsList) {
        statsList.sort(
                Comparator.<TeamStats>comparingInt(s -> s.points).reversed()
                        .thenComparing(Comparator.<TeamStats>comparingInt(RankingCalculator::goalDifference).reversed())
                        .thenComparing(Comparator.<TeamStats>comparingInt(s -> s.goalsFor).reversed())
        );
    }

    public static int goalDifference(TeamStats stats) {
        return stats.goalsFor - stats.goalsAgainst;
    }
}