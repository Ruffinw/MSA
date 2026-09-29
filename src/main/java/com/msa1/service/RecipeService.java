package com.msa1.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.msa1.dto.RecipeCreateDTO;
import com.msa1.dto.RecipeDTO;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.exception.ConflictException;
import com.msa1.exception.ResourceNotFoundException;
import com.msa1.mapper.RecipeMapper;
import com.msa1.model.Recipe;
import com.msa1.repository.RecipeRepository;
import com.msa1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;

    @Transactional(readOnly = true)
    public List<RecipeDTO> findAll() {
        return recipeRepository.findAll().stream()
                .map(recipeMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public RecipeDTO findById(Long id) {
        return recipeMapper.map(getRecipe(id));
    }

    @Transactional(readOnly = true)
    public RecipeDTO findBySlug(String slug) {
        var recipe = recipeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Рецепт не найден: " + slug));
        return recipeMapper.map(recipe);
    }

    public RecipeDTO create(RecipeCreateDTO recipeData) {
        ensureSlugIsFree(recipeData.getSlug(), null);
        var recipe = recipeMapper.map(recipeData);
        if (recipe.getAuthor() == null) {
            throw new ResourceNotFoundException("Автор не найден: " + recipeData.getAuthorId());
        }
        recipeRepository.save(recipe);
        return recipeMapper.map(recipe);
    }

    public RecipeDTO update(Long id, RecipeUpdateDTO recipeData) {
        var recipe = getRecipe(id);
        if (recipeData.getAuthorId() != null && recipeData.getAuthorId().isPresent()) {
            var authorId = recipeData.getAuthorId().get();
            if (authorId == null || !userRepository.existsById(authorId)) {
                throw new ResourceNotFoundException("Автор не найден: " + authorId);
            }
        }
        if (recipeData.getSlug() != null && recipeData.getSlug().isPresent()) {
            ensureSlugIsFree(recipeData.getSlug().get(), id);
        }
        recipeMapper.update(recipeData, recipe);
        recipeRepository.save(recipe);
        return recipeMapper.map(recipe);
    }

    public void delete(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Рецепт не найден: " + id);
        }
        recipeRepository.deleteById(id);
    }

    private Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Рецепт не найден: " + id));
    }

    private void ensureSlugIsFree(String slug, Long currentId) {
        recipeRepository.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ConflictException("Рецепт со слагом уже существует: " + slug);
                });
    }
}
