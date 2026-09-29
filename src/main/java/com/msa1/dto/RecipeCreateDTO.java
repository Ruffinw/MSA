package com.msa1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeCreateDTO {

    @NotNull(message = "Автор рецепта обязателен")
    private Long authorId;

    @NotBlank(message = "Слаг обязателен")
    private String slug;

    @NotBlank(message = "Название рецепта обязательно")
    private String name;

    @NotBlank(message = "Список ингредиентов обязателен")
    private String ingredients;

    @NotBlank(message = "Инструкция приготовления обязательна")
    private String instructions;

    @NotNull(message = "Время приготовления обязательно")
    @Min(value = 1, message = "Время приготовления должно быть больше нуля")
    private Integer cookingTimeMinutes;
}
