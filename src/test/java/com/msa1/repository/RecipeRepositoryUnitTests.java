package com.msa1.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.msa1.model.Recipe;
import com.msa1.model.User;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeRepositoryUnitTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test 1: Save Recipe Test")
    @Order(1)
    @Rollback(value = false)
    public void saveRecipeTest() {
        User author = User.builder()
                .name("Мария Иванова")
                .email("maria@gmail.com")
                .build();
        userRepository.save(author);

        Recipe recipe = Recipe.builder()
                .author(author)
                .slug("borscht")
                .name("Борщ")
                .ingredients("свекла, капуста, картофель")
                .instructions("Сварить бульон и добавить овощи")
                .cookingTimeMinutes(90)
                .build();

        recipeRepository.save(recipe);

        System.out.println(recipe);
        Assertions.assertThat(recipe.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getRecipeTest() {
        Recipe recipe = recipeRepository.findById(1L).get();

        System.out.println(recipe);
        Assertions.assertThat(recipe.getId()).isEqualTo(1L);

        Recipe bySlug = recipeRepository.findBySlug("borscht").get();
        Assertions.assertThat(bySlug.getName()).isEqualTo("Борщ");
    }

    @Test
    @Order(3)
    public void getListOfRecipesTest() {
        List<Recipe> recipes = recipeRepository.findAll();

        System.out.println(recipes);
        Assertions.assertThat(recipes.size()).isGreaterThan(0);
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void updateRecipeTest() {
        Recipe recipe = recipeRepository.findById(1L).get();
        recipe.setName("Борщ украинский");

        Recipe recipeUpdated = recipeRepository.save(recipe);

        System.out.println(recipeUpdated);
        Assertions.assertThat(recipeUpdated.getName()).isEqualTo("Борщ украинский");
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void deleteRecipeTest() {
        recipeRepository.deleteById(1L);

        Optional<Recipe> recipeOptional = recipeRepository.findById(1L);

        Assertions.assertThat(recipeOptional).isEmpty();
    }
}
