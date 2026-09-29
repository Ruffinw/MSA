package com.msa1.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeDTO {

    private Long id;

    private Long authorId;

    private String slug;

    private String name;

    private String ingredients;

    private String instructions;

    private Integer cookingTimeMinutes;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
