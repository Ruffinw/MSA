package com.msa1.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.msa1.dto.RecipeCreateDTO;
import com.msa1.dto.RecipeDTO;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.service.RecipeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipesController {

    private final RecipeService recipeService;

    @GetMapping("/recipes")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDTO> index() {
        return recipeService.findAll();
    }

    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDTO create(@Valid @RequestBody RecipeCreateDTO recipeData) {
        return recipeService.create(recipeData);
    }

    @GetMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO show(@PathVariable Long id) {
        return recipeService.findById(id);
    }

    @GetMapping("/recipes/slug/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO showBySlug(@PathVariable String slug) {
        return recipeService.findBySlug(slug);
    }

    @PutMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO update(@PathVariable Long id, @Valid @RequestBody RecipeUpdateDTO recipeData) {
        return recipeService.update(id, recipeData);
    }

    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        recipeService.delete(id);
    }
}
