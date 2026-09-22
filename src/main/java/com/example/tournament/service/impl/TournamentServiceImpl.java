package com.example.tournament.service.impl;

import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.TournamentStatus;
import com.example.tournament.exception.BusinessException;
import com.example.tournament.exception.ResourceNotFoundException;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    @Override
    @Transactional
    public Tournament createTournament(Tournament tournament) {

        if (tournament.getStartDate() != null && tournament.getEndDate() != null
                && tournament.getStartDate().isAfter(tournament.getEndDate())) {
            throw new BusinessException("La date de début doit être avant la date de fin.");
        }

        if (tournamentRepository.existsByNameIgnoreCase(tournament.getName())) {
            throw new BusinessException("Un tournoi avec ce nom existe déjà.");
        }

        tournament.setStatus(TournamentStatus.BROUILLON);

        return tournamentRepository.save(tournament);
    }

    @Override
    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournoi introuvable avec l'id : " + id));
    }

    @Override
    public Page<Tournament> getAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Tournament updateTournament(Long id, Tournament updatedData) {

        Tournament existing = getTournamentById(id);

        if (existing.getStatus() == TournamentStatus.TERMINE
                || existing.getStatus() == TournamentStatus.ANNULE) {
            throw new BusinessException("Impossible de modifier un tournoi terminé ou annulé.");
        }

        existing.setName(updatedData.getName());
        existing.setSport(updatedData.getSport());
        existing.setDescription(updatedData.getDescription());
        existing.setStartDate(updatedData.getStartDate());
        existing.setEndDate(updatedData.getEndDate());
        existing.setLocation(updatedData.getLocation());
        existing.setMaxTeams(updatedData.getMaxTeams());
        existing.setType(updatedData.getType());

        return tournamentRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTournament(Long id) {
        Tournament existing = getTournamentById(id);
        tournamentRepository.delete(existing);
    }

    @Override
    public Page<Tournament> searchTournaments(String sport, TournamentStatus status, Pageable pageable) {

        Specification<Tournament> spec = (root, query, cb) -> cb.conjunction();

        if (sport != null && !sport.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("sport")), sport.toLowerCase()));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return tournamentRepository.findAll(spec, pageable);
    }
}