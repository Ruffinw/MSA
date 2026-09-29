package com.msa1.dto;

import org.openapitools.jackson.nullable.JsonNullable;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeUpdateDTO {

    @NotNull
    private JsonNullable<Long> authorId = JsonNullable.undefined();

    @NotNull
    private JsonNullable<String> slug = JsonNullable.undefined();

    @NotNull
    private JsonNullable<String> name = JsonNullable.undefined();

    @NotNull
    private JsonNullable<String> ingredients = JsonNullable.undefined();

    @NotNull
    private JsonNullable<String> instructions = JsonNullable.undefined();

    @NotNull
    private JsonNullable<Integer> cookingTimeMinutes = JsonNullable.undefined();
}
