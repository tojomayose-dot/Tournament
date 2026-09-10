package com.example.tournament.util;

import com.example.tournament.entity.Team;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinScheduler {

    /**
     * Représente un affrontement entre deux équipes pour une journée donnée.
     * homeTeam ou awayTeam peuvent être null en cas de nombre impair d'équipes (BYE = repos).
     */
    public record Fixture(Team homeTeam, Team awayTeam) {
    }

    /**
     * Génère le calendrier complet d'un championnat en aller simple.
     * Retourne une liste de journées, chaque journée étant une liste de rencontres.
     */
    public static List<List<Fixture>> generateSchedule(List<Team> teams) {

        List<Team> workingList = new ArrayList<>(teams);

        // Si nombre impair, on ajoute une équipe fictive "BYE" (null = repos)
        boolean hasBye = workingList.size() % 2 != 0;
        if (hasBye) {
            workingList.add(null);
        }

        int numberOfTeams = workingList.size();
        int numberOfRounds = numberOfTeams - 1;
        int matchesPerRound = numberOfTeams / 2;

        List<List<Fixture>> schedule = new ArrayList<>();

        for (int round = 0; round < numberOfRounds; round++) {

            List<Fixture> roundFixtures = new ArrayList<>();

            for (int i = 0; i < matchesPerRound; i++) {
                Team home = workingList.get(i);
                Team away = workingList.get(numberOfTeams - 1 - i);

                // On ignore les rencontres impliquant l'équipe fictive BYE
                if (home != null && away != null) {
                    // On alterne domicile/extérieur à chaque tour pour plus d'équité
                    if (round % 2 == 0) {
                        roundFixtures.add(new Fixture(home, away));
                    } else {
                        roundFixtures.add(new Fixture(away, home));
                    }
                }
            }

            schedule.add(roundFixtures);

            // Rotation : on fixe la première équipe, on fait tourner les autres
            Team last = workingList.remove(numberOfTeams - 1);
            workingList.add(1, last);
        }

        return schedule;
    }
}