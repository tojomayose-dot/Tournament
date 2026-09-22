package com.example.tournament.service.impl;

import com.example.tournament.entity.*;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.repository.TeamRepository;
import com.example.tournament.repository.TournamentRegistrationRepository;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;

    @Override
    @Transactional
    public Team createTeam(Team team) {

        if (teamRepository.existsByNameIgnoreCaseAndArchivedFalse(team.getName())) {
            throw new BusinessException("Une équipe avec ce nom existe déjà.");
        }

        team.setStatus(TeamStatus.ACTIVE);

        return teamRepository.save(team);
    }

    @Override
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Équipe introuvable avec l'id : " + id));
    }

    @Override
    public Page<Team> getAllTeams(Pageable pageable) {
        return teamRepository.findByArchivedFalse(pageable);
    }

    @Override
    @Transactional
    public Team updateTeam(Long id, Team updatedData) {

        Team existing = getTeamById(id);

        existing.setName(updatedData.getName());
        existing.setLogo(updatedData.getLogo());
        existing.setCity(updatedData.getCity());
        existing.setCoachName(updatedData.getCoachName());

        return teamRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        Team existing = getTeamById(id);

        // Soft delete : on archive au lieu de supprimer,
        // pour préserver les matchs et les inscriptions
        existing.setArchived(true);
        teamRepository.save(existing);
    }

    @Override
    @Transactional
    public void registerTeamToTournament(Long tournamentId, Long teamId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi introuvable avec l'id : " + tournamentId));

        Team team = getTeamById(teamId);

        if (tournament.isArchived()) {
            throw new BusinessException("Ce tournoi a été archivé, il n'accepte plus d'inscriptions.");
        }

        if (team.isArchived()) {
            throw new BusinessException("Cette équipe a été archivée, elle ne peut plus s'inscrire.");
        }

        if (tournament.getStatus() != TournamentStatus.INSCRIPTION
                && tournament.getStatus() != TournamentStatus.BROUILLON) {
            throw new BusinessException("Les inscriptions ne sont plus ouvertes pour ce tournoi.");
        }

        if (registrationRepository.existsByTournamentIdAndTeamId(tournamentId, teamId)) {
            throw new BusinessException("Cette équipe est déjà inscrite à ce tournoi.");
        }

        long currentCount = registrationRepository.findByTournamentId(tournamentId).size();
        if (tournament.getMaxTeams() != null && currentCount >= tournament.getMaxTeams()) {
            throw new BusinessException("Le nombre maximum d'équipes est atteint pour ce tournoi.");
        }

        TournamentRegistration registration = new TournamentRegistration();
        registration.setTournament(tournament);
        registration.setTeam(team);
        registration.setRegisteredAt(LocalDateTime.now());

        registrationRepository.save(registration);
    }

    @Override
    @Transactional
    public void withdrawTeamFromTournament(Long tournamentId, Long teamId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi introuvable avec l'id : " + tournamentId));

        if (tournament.getStatus() != TournamentStatus.INSCRIPTION
                && tournament.getStatus() != TournamentStatus.BROUILLON) {
            throw new BusinessException("Impossible de retirer une équipe après le début du tournoi.");
        }

        TournamentRegistration registration = registrationRepository.findByTournamentId(tournamentId).stream()
                .filter(r -> r.getTeam().getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cette équipe n'est pas inscrite à ce tournoi."));

        registrationRepository.delete(registration);
    }
}