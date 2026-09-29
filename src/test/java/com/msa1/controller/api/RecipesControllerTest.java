package com.msa1.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa1.config.FakerConfig;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.mapper.RecipeMapper;
import com.msa1.model.Recipe;
import com.msa1.model.User;
import com.msa1.repository.RecipeRepository;
import com.msa1.repository.UserRepository;

import net.datafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(FakerConfig.class)
@Transactional
public class RecipesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;

    private User testUser;

    private Recipe testRecipe;

    @BeforeEach
    public void setUp() {
        recipeRepository.deleteAll();
        userRepository.deleteAll();

        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getEmail), () -> "cook-" + UUID.randomUUID() + "@recipes.test")
                .supply(Select.field(User::getName), () -> faker.name().fullName())
                .create();
        testUser = userRepository.save(testUser);

        testRecipe = Instancio.of(Recipe.class)
                .ignore(Select.field(Recipe::getId))
                .ignore(Select.field(Recipe::getCreatedAt))
                .ignore(Select.field(Recipe::getUpdatedAt))
                .supply(Select.field(Recipe::getName), () -> faker.food().dish())
                .supply(Select.field(Recipe::getSlug), () -> "recipe-" + UUID.randomUUID())
                .supply(Select.field(Recipe::getIngredients), () -> faker.food().ingredient())
                .supply(Select.field(Recipe::getInstructions), () -> faker.lorem().paragraph())
                .supply(Select.field(Recipe::getCookingTimeMinutes), () -> faker.number().numberBetween(10, 120))
                .supply(Select.field(Recipe::getAuthor), () -> testUser)
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        recipeRepository.save(testRecipe);

        var result = mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThatJson(body).node("[0].slug").isEqualTo(testRecipe.getSlug());
    }

    @Test
    public void testCreate() throws Exception {
        var dto = recipeMapper.map(testRecipe);
        var request = post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var recipe = recipeRepository.findBySlug(dto.getSlug()).orElseThrow();
        assertThat(recipe.getName()).isEqualTo(dto.getName());
        assertThat(recipe.getIngredients()).isEqualTo(dto.getIngredients());
        assertThat(recipe.getInstructions()).isEqualTo(dto.getInstructions());
        assertThat(recipe.getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        recipeRepository.save(testRecipe);

        var dto = new RecipeUpdateDTO();
        dto.setName(JsonNullable.of("Сырники с изюмом"));

        var request = put("/api/recipes/" + testRecipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var recipe = recipeRepository.findById(testRecipe.getId()).orElseThrow();
        assertThat(recipe.getName()).isEqualTo("Сырники с изюмом");
        assertThat(recipe.getSlug()).isEqualTo(testRecipe.getSlug());
        assertThat(recipe.getIngredients()).isEqualTo(testRecipe.getIngredients());
    }

    @Test
    public void testShow() throws Exception {
        recipeRepository.save(testRecipe);

        var result = mockMvc.perform(get("/api/recipes/" + testRecipe.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                value -> value.node("slug").isEqualTo(testRecipe.getSlug()),
                value -> value.node("name").isEqualTo(testRecipe.getName()),
                value -> value.node("ingredients").isEqualTo(testRecipe.getIngredients()),
                value -> value.node("instructions").isEqualTo(testRecipe.getInstructions())
        );
    }

    @Test
    public void testShowBySlug() throws Exception {
        recipeRepository.save(testRecipe);

        var result = mockMvc.perform(get("/api/recipes/slug/" + testRecipe.getSlug()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo(testRecipe.getName());
    }

    @Test
    public void testDestroy() throws Exception {
        recipeRepository.save(testRecipe);

        mockMvc.perform(delete("/api/recipes/" + testRecipe.getId()))
                .andExpect(status().isNoContent());

        assertThat(recipeRepository.existsById(testRecipe.getId())).isFalse();
    }

    @Test
    public void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateValidation() throws Exception {
        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
