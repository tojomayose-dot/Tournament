package com.example.tournament.util;

import com.example.tournament.entity.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnockoutScheduler {

    public record Fixture(Team homeTeam, Team awayTeam) {
    }

    /**
     * Calcule la taille de bracket (puissance de 2) immédiatement supérieure
     * ou égale au nombre d'équipes fourni.
     * Exemple : 10 équipes -> 16 ; 8 équipes -> 8 ; 5 équipes -> 8.
     */
    public static int calculateBracketSize(int numberOfTeams) {
        int size = 1;
        while (size < numberOfTeams) {
            size *= 2;
        }
        return size;
    }

    /**
     * Calcule le nombre total de tours nécessaires pour une taille de bracket donnée.
     * Exemple : bracket de 8 -> 3 tours (quarts, demies, finale).
     */
    public static int calculateNumberOfRounds(int bracketSize) {
        return (int) (Math.log(bracketSize) / Math.log(2));
    }

    /**
     * Génère les affrontements du premier tour (tour 1), en tenant compte
     * des exemptions nécessaires si le nombre d'équipes n'est pas une puissance de 2.
     * Les équipes exemptées sont retournées séparément (elles n'ont pas de match ce tour-ci).
     */
    public static KnockoutRound generateFirstRound(List<Team> teams) {

        List<Team> shuffledTeams = new ArrayList<>(teams);
        Collections.shuffle(shuffledTeams);

        int bracketSize = calculateBracketSize(shuffledTeams.size());
        int numberOfByes = bracketSize - shuffledTeams.size();

        List<Team> byeTeams = new ArrayList<>();
        List<Team> playingTeams = new ArrayList<>(shuffledTeams);

        for (int i = 0; i < numberOfByes; i++) {
            byeTeams.add(playingTeams.remove(playingTeams.size() - 1));
        }

        List<Fixture> fixtures = new ArrayList<>();
        for (int i = 0; i < playingTeams.size(); i += 2) {
            fixtures.add(new Fixture(playingTeams.get(i), playingTeams.get(i + 1)));
        }

        return new KnockoutRound(fixtures, byeTeams);
    }

    /**
     * Génère les affrontements du tour suivant à partir de la liste des équipes
     * qualifiées (vainqueurs du tour précédent + équipes exemptées éventuelles).
     */
    public static List<Fixture> generateNextRound(List<Team> qualifiedTeams) {

        List<Fixture> fixtures = new ArrayList<>();
        for (int i = 0; i < qualifiedTeams.size(); i += 2) {
            fixtures.add(new Fixture(qualifiedTeams.get(i), qualifiedTeams.get(i + 1)));
        }
        return fixtures;
    }

    public record KnockoutRound(List<Fixture> fixtures, List<Team> byeTeams) {
    }
}