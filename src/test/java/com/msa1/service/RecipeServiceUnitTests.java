package com.msa1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import com.msa1.dto.RecipeCreateDTO;
import com.msa1.dto.RecipeDTO;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.mapper.RecipeMapper;
import com.msa1.model.Recipe;
import com.msa1.model.User;
import com.msa1.repository.RecipeRepository;
import com.msa1.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeServiceUnitTests {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;

    private RecipeDTO recipeDto;

    @BeforeEach
    public void setup() {
        User author = User.builder()
                .id(1L)
                .name("Мария Иванова")
                .email("maria@gmail.com")
                .build();

        recipe = Recipe.builder()
                .id(1L)
                .author(author)
                .slug("borscht")
                .name("Борщ")
                .ingredients("свекла, капуста, картофель")
                .instructions("Сварить бульон и добавить овощи")
                .cookingTimeMinutes(90)
                .build();

        recipeDto = new RecipeDTO();
        recipeDto.setId(1L);
        recipeDto.setAuthorId(1L);
        recipeDto.setSlug("borscht");
        recipeDto.setName("Борщ");
        recipeDto.setIngredients("свекла, капуста, картофель");
        recipeDto.setInstructions("Сварить бульон и добавить овощи");
        recipeDto.setCookingTimeMinutes(90);
    }

    @Test
    @Order(1)
    public void saveRecipeTest() {
        RecipeCreateDTO createDto = new RecipeCreateDTO();
        createDto.setAuthorId(1L);
        createDto.setSlug("borscht");
        createDto.setName("Борщ");
        createDto.setIngredients("свекла, капуста, картофель");
        createDto.setInstructions("Сварить бульон и добавить овощи");
        createDto.setCookingTimeMinutes(90);

        given(recipeMapper.map(any(RecipeCreateDTO.class))).willReturn(recipe);
        given(recipeRepository.save(recipe)).willReturn(recipe);
        given(recipeMapper.map(recipe)).willReturn(recipeDto);

        RecipeDTO savedRecipe = recipeService.create(createDto);

        System.out.println(savedRecipe);
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getName()).isEqualTo("Борщ");
    }

    @Test
    @Order(2)
    public void getRecipeById() {
        given(recipeRepository.findById(1L)).willReturn(Optional.of(recipe));
        given(recipeMapper.map(recipe)).willReturn(recipeDto);

        RecipeDTO existingRecipe = recipeService.findById(recipe.getId());

        System.out.println(existingRecipe);
        assertThat(existingRecipe).isNotNull();
        assertThat(existingRecipe.getId()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    public void getAllRecipes() {
        Recipe recipe1 = Recipe.builder()
                .id(2L)
                .author(recipe.getAuthor())
                .slug("olivier")
                .name("Оливье")
                .ingredients("картофель, горошек")
                .instructions("Нарезать и смешать")
                .cookingTimeMinutes(40)
                .build();

        given(recipeRepository.findAll()).willReturn(List.of(recipe, recipe1));
        given(recipeMapper.map(any(Recipe.class))).willReturn(recipeDto);

        List<RecipeDTO> recipeList = recipeService.findAll();

        System.out.println(recipeList);
        assertThat(recipeList).isNotNull();
        assertThat(recipeList.size()).isGreaterThan(1);
    }

    @Test
    @Order(4)
    public void updateRecipe() {
        given(recipeRepository.findById(recipe.getId())).willReturn(Optional.of(recipe));
        given(recipeRepository.save(recipe)).willReturn(recipe);
        given(recipeMapper.map(recipe)).willAnswer(invocation -> {
            Recipe source = invocation.getArgument(0);
            RecipeDTO dto = new RecipeDTO();
            dto.setName(source.getName());
            dto.setSlug(source.getSlug());
            return dto;
        });

        RecipeUpdateDTO updateDto = new RecipeUpdateDTO();
        updateDto.setName(JsonNullable.of("Сырники"));

        doAnswer(invocation -> {
            Recipe target = invocation.getArgument(1);
            target.setName("Сырники");
            return null;
        }).when(recipeMapper).update(any(RecipeUpdateDTO.class), any(Recipe.class));

        RecipeDTO updatedRecipe = recipeService.update(recipe.getId(), updateDto);

        System.out.println(updatedRecipe);
        assertThat(updatedRecipe.getName()).isEqualTo("Сырники");
        assertThat(updatedRecipe.getSlug()).isEqualTo("borscht");
    }

    @Test
    @Order(5)
    public void deleteRecipe() {
        given(recipeRepository.existsById(recipe.getId())).willReturn(true);
        willDoNothing().given(recipeRepository).deleteById(recipe.getId());

        recipeService.delete(recipe.getId());

        verify(recipeRepository, times(1)).deleteById(recipe.getId());
    }
}
