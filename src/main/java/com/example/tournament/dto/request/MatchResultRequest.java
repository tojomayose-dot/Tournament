package com.example.tournament.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResultRequest {

    @NotNull(message = "Le score de l'équipe à domicile est obligatoire.")
    @Min(value = 0, message = "Le score ne peut pas être négatif.")
    private Integer homeScore;

    @NotNull(message = "Le score de l'équipe à l'extérieur est obligatoire.")
    @Min(value = 0, message = "Le score ne peut pas être négatif.")
    private Integer awayScore;
}