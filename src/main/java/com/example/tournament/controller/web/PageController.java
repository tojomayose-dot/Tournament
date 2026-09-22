package com.example.tournament.controller.web;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.Team;
import com.example.tournament.service.TournamentService;
import com.example.tournament.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.tournament.entity.TournamentStatus;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final TournamentService tournamentService;
    private final TeamService teamService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Tournament> allTournaments =
                tournamentService.searchTournaments(null, null, Pageable.unpaged()).getContent();
        List<Team> teams = teamService.getAllTeams(Pageable.unpaged()).getContent();

        long ongoing = allTournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.EN_COURS)
                .count();
        long upcoming = allTournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.INSCRIPTION || t.getStatus() == TournamentStatus.PROGRAMME)
                .count();
        long finished = allTournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.TERMINE)
                .count();

        model.addAttribute("ongoingCount", ongoing);
        model.addAttribute("upcomingCount", upcoming);
        model.addAttribute("finishedCount", finished);
        model.addAttribute("teamCount", teams.size());
        model.addAttribute("recentTournaments",
                allTournaments.stream()
                        .sorted(Comparator.comparing(Tournament::getStartDate, Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(Tournament::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(5)
                        .toList());

        return "dashboard";
    }
}
