package com.msa1.controller.api;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa1.config.JacksonConfig;
import com.msa1.dto.RecipeCreateDTO;
import com.msa1.dto.RecipeDTO;
import com.msa1.dto.RecipeUpdateDTO;
import com.msa1.service.RecipeService;

@WebMvcTest(RecipesController.class)
@Import(JacksonConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipesControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecipeDTO recipe;

    private RecipeCreateDTO createDto;

    @BeforeEach
    public void setup() {
        recipe = new RecipeDTO();
        recipe.setId(1L);
        recipe.setAuthorId(1L);
        recipe.setSlug("borscht");
        recipe.setName("Борщ");
        recipe.setIngredients("свекла, капуста, картофель");
        recipe.setInstructions("Сварить бульон и добавить овощи");
        recipe.setCookingTimeMinutes(90);

        createDto = new RecipeCreateDTO();
        createDto.setAuthorId(1L);
        createDto.setSlug("borscht");
        createDto.setName("Борщ");
        createDto.setIngredients("свекла, капуста, картофель");
        createDto.setInstructions("Сварить бульон и добавить овощи");
        createDto.setCookingTimeMinutes(90);
    }

    @Test
    @Order(1)
    public void saveRecipeTest() throws Exception {
        given(recipeService.create(any(RecipeCreateDTO.class))).willReturn(recipe);

        ResultActions response = mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(recipe.getName())))
                .andExpect(jsonPath("$.slug", is(recipe.getSlug())))
                .andExpect(jsonPath("$.ingredients", is(recipe.getIngredients())));
    }

    @Test
    @Order(2)
    public void getRecipeTest() throws Exception {
        List<RecipeDTO> recipesList = new ArrayList<>();
        recipesList.add(recipe);

        RecipeDTO second = new RecipeDTO();
        second.setId(2L);
        second.setAuthorId(1L);
        second.setSlug("olivier");
        second.setName("Оливье");
        second.setIngredients("картофель, горошек");
        second.setInstructions("Нарезать и смешать");
        second.setCookingTimeMinutes(40);
        recipesList.add(second);

        given(recipeService.findAll()).willReturn(recipesList);

        ResultActions response = mockMvc.perform(get("/api/recipes"));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(recipesList.size())));
    }

    @Test
    @Order(3)
    public void getByIdRecipeTest() throws Exception {
        given(recipeService.findById(recipe.getId())).willReturn(recipe);

        ResultActions response = mockMvc.perform(get("/api/recipes/{id}", recipe.getId()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(recipe.getName())))
                .andExpect(jsonPath("$.slug", is(recipe.getSlug())))
                .andExpect(jsonPath("$.ingredients", is(recipe.getIngredients())));
    }

    @Test
    @Order(4)
    public void getBySlugRecipeTest() throws Exception {
        given(recipeService.findBySlug(recipe.getSlug())).willReturn(recipe);

        ResultActions response = mockMvc.perform(get("/api/recipes/slug/{slug}", recipe.getSlug()));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.slug", is(recipe.getSlug())))
                .andExpect(jsonPath("$.name", is(recipe.getName())));
    }

    @Test
    @Order(5)
    public void updateRecipeTest() throws Exception {
        recipe.setName("Сырники");
        recipe.setCookingTimeMinutes(25);
        given(recipeService.update(any(Long.class), any(RecipeUpdateDTO.class))).willReturn(recipe);

        ResultActions response = mockMvc.perform(put("/api/recipes/{id}", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Сырники",
                          "cookingTimeMinutes": 25
                        }
                        """));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(recipe.getName())))
                .andExpect(jsonPath("$.cookingTimeMinutes", is(recipe.getCookingTimeMinutes())));
    }

    @Test
    @Order(6)
    public void deleteRecipeTest() throws Exception {
        willDoNothing().given(recipeService).delete(recipe.getId());

        ResultActions response = mockMvc.perform(delete("/api/recipes/{id}", recipe.getId()));

        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}
