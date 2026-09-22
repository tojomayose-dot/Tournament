package com.example.tournament.service.impl;

import com.example.tournament.dto.response.TeamRankingResponse;
import com.example.tournament.dto.response.TournamentStatisticsResponse;
import com.example.tournament.entity.*;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.TournamentRegistrationRepository;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.service.MatchService;
import com.example.tournament.util.KnockoutScheduler;
import com.example.tournament.util.RankingCalculator;
import com.example.tournament.util.RoundRobinScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tournament.dto.response.TeamRankingResponse;
import com.example.tournament.util.RankingCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;

    @Override
    @Transactional
    public List<Match> generateMatches(Long tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi introuvable avec l'id : " + tournamentId));

        if (matchRepository.existsByTournamentId(tournamentId)) {
            throw new BusinessException("Les matchs ont déjà été générés pour ce tournoi.");
        }

        List<Team> teams = registrationRepository.findByTournamentId(tournamentId).stream()
                .map(TournamentRegistration::getTeam)
                .collect(Collectors.toList());

        if (teams.size() < 2) {
            throw new BusinessException("Il faut au moins 2 équipes inscrites pour générer les matchs.");
        }

        List<Match> generatedMatches = switch (tournament.getType()) {
            case CHAMPIONNAT -> generateChampionshipMatches(tournament, teams);
            case ELIMINATION_DIRECTE -> generateKnockoutFirstRound(tournament, teams);
        };

        tournament.setStatus(TournamentStatus.PROGRAMME);
        tournamentRepository.save(tournament);

        return matchRepository.saveAll(generatedMatches);
    }

    private List<Match> generateChampionshipMatches(Tournament tournament, List<Team> teams) {

        List<List<RoundRobinScheduler.Fixture>> schedule = RoundRobinScheduler.generateSchedule(teams);

        List<Match> matches = new ArrayList<>();

        for (int round = 0; round < schedule.size(); round++) {
            for (RoundRobinScheduler.Fixture fixture : schedule.get(round)) {

                Match match = new Match();
                match.setTournament(tournament);
                match.setHomeTeam(fixture.homeTeam());
                match.setAwayTeam(fixture.awayTeam());
                match.setPhase("Journée " + (round + 1));
                match.setRoundNumber(round + 1);
                match.setStatus(MatchStatus.PLANIFIE);

                matches.add(match);
            }
        }

        return matches;
    }

    private List<Match> generateKnockoutFirstRound(Tournament tournament, List<Team> teams) {

        KnockoutScheduler.KnockoutRound firstRound = KnockoutScheduler.generateFirstRound(teams);

        List<Match> matches = new ArrayList<>();

        boolean hasPreliminaryRound = !firstRound.byeTeams().isEmpty();
        String phaseLabel = hasPreliminaryRound ? "Tour préliminaire" : roundLabel(teams.size());

        for (KnockoutScheduler.Fixture fixture : firstRound.fixtures()) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setHomeTeam(fixture.homeTeam());
            match.setAwayTeam(fixture.awayTeam());
            match.setPhase(phaseLabel);
            match.setRoundNumber(1);
            match.setStatus(MatchStatus.PLANIFIE);
            matches.add(match);
        }

        // Équipes exemptées : matchs fictifs déjà gagnés, sans adversaire
        for (Team byeTeam : firstRound.byeTeams()) {
            Match byeMatch = new Match();
            byeMatch.setTournament(tournament);
            byeMatch.setHomeTeam(byeTeam);
            byeMatch.setAwayTeam(null);
            byeMatch.setPhase("Exemption");
            byeMatch.setRoundNumber(1);
            byeMatch.setStatus(MatchStatus.TERMINE);
            byeMatch.setWinnerTeam(byeTeam);
            matches.add(byeMatch);
        }

        return matches;
    }

    private String roundLabel(int numberOfTeamsInRound) {
        return switch (numberOfTeamsInRound) {
            case 2 -> "Finale";
            case 4 -> "Demi-finale";
            case 8 -> "Quart de finale";
            case 16 -> "Huitième de finale";
            default -> "Tour à " + numberOfTeamsInRound + " équipes";
        };
    }

    @Override
    public List<Match> getMatchesByTournament(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    @Override
    @Transactional
    public Match recordResult(Long matchId, Integer homeScore, Integer awayScore) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match introuvable avec l'id : " + matchId));

        if (match.getStatus() == MatchStatus.TERMINE) {
            throw new BusinessException("Ce match a déjà un résultat enregistré.");
        }

        if (match.getTournament().getType() == TournamentType.ELIMINATION_DIRECTE
                && homeScore.equals(awayScore)) {
            throw new BusinessException("Un match nul n'est pas autorisé en élimination directe.");
        }

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(MatchStatus.TERMINE);

        Team winner = homeScore > awayScore ? match.getHomeTeam() : match.getAwayTeam();
        match.setWinnerTeam(winner);

        Match savedMatch = matchRepository.save(match);

        if (match.getTournament().getType() == TournamentType.ELIMINATION_DIRECTE) {
            tryGenerateNextKnockoutRound(match.getTournament(), match.getRoundNumber());
        }

        if (match.getAwayTeam() == null) {
            throw new BusinessException("Ce match est une exemption automatique, aucun résultat à enregistrer.");
        }

        return savedMatch;
    }

    private void tryGenerateNextKnockoutRound(Tournament tournament, Integer completedRoundNumber) {

        List<Match> roundMatches = matchRepository.findByTournamentIdAndRoundNumber(
                tournament.getId(), completedRoundNumber);

        boolean allFinished = roundMatches.stream()
                .allMatch(m -> m.getStatus() == MatchStatus.TERMINE);

        if (!allFinished) {
            return;
        }

        List<Team> winners = roundMatches.stream()
                .map(Match::getWinnerTeam)
                .collect(Collectors.toList());

        if (winners.size() == 1) {
            tournament.setStatus(TournamentStatus.TERMINE);
            tournamentRepository.save(tournament);
            return;
        }

        List<KnockoutScheduler.Fixture> nextFixtures = KnockoutScheduler.generateNextRound(winners);

        int nextRoundNumber = completedRoundNumber + 1;
        String phaseLabel = roundLabel(winners.size());

        List<Match> nextMatches = new ArrayList<>();
        for (KnockoutScheduler.Fixture fixture : nextFixtures) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setHomeTeam(fixture.homeTeam());
            match.setAwayTeam(fixture.awayTeam());
            match.setPhase(phaseLabel);
            match.setRoundNumber(nextRoundNumber);
            match.setStatus(MatchStatus.PLANIFIE);
            nextMatches.add(match);
        }

        matchRepository.saveAll(nextMatches);
    }

    @Override
    public List<TeamRankingResponse> getRanking(Long tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi introuvable avec l'id : " + tournamentId));

        if (tournament.getType() != TournamentType.CHAMPIONNAT) {
            throw new BusinessException("Le classement n'est disponible que pour les tournois de type championnat.");
        }

        List<Team> teams = registrationRepository.findByTournamentId(tournamentId).stream()
                .map(TournamentRegistration::getTeam)
                .collect(Collectors.toList());

        List<Match> matches = matchRepository.findByTournamentId(tournamentId);

        List<RankingCalculator.TeamStats> statsList = RankingCalculator.computeStats(teams, matches);
        RankingCalculator.sortByStandardCriteria(statsList);

        return statsList.stream()
                .map(stats -> TeamRankingResponse.builder()
                        .teamId(stats.team.getId())
                        .teamName(stats.team.getName())
                        .played(stats.played)
                        .won(stats.won)
                        .drawn(stats.drawn)
                        .lost(stats.lost)
                        .goalsFor(stats.goalsFor)
                        .goalsAgainst(stats.goalsAgainst)
                        .goalDifference(RankingCalculator.goalDifference(stats))
                        .points(stats.points)
                        .build())
                .toList();
    }

    @Override
    public TournamentStatisticsResponse getStatistics(Long tournamentId) {

        List<Match> matches = matchRepository.findByTournamentId(tournamentId);
        int totalTeams = registrationRepository.findByTournamentId(tournamentId).size();

        int totalMatches = matches.size();
        long played = matches.stream().filter(m -> m.getStatus() == MatchStatus.TERMINE).count();
        int totalGoals = matches.stream()
                .filter(m -> m.getStatus() == MatchStatus.TERMINE)
                .mapToInt(m -> (m.getHomeScore() != null ? m.getHomeScore() : 0)
                        + (m.getAwayScore() != null ? m.getAwayScore() : 0))
                .sum();

        double average = played > 0 ? (double) totalGoals / played : 0.0;

        return TournamentStatisticsResponse.builder()
                .totalTeams(totalTeams)
                .totalMatches(totalMatches)
                .matchesPlayed((int) played)
                .matchesRemaining(totalMatches - (int) played)
                .totalGoals(totalGoals)
                .averageGoalsPerMatch(Math.round(average * 100.0) / 100.0)
                .build();
    }
}