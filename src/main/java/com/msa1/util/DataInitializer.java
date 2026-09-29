package com.msa1.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.msa1.model.Recipe;
import com.msa1.model.User;
import com.msa1.repository.RecipeRepository;
import com.msa1.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        var author = new User();
        author.setName("Мария Иванова");
        author.setEmail("maria@recipes.local");
        userRepository.save(author);

        recipeRepository.save(recipe(
                author,
                "borscht",
                "Борщ",
                "свекла, капуста, картофель, морковь, лук, томатная паста, говядина, сметана",
                "Сварить мясной бульон. Обжарить лук, морковь и свеклу с томатной пастой. "
                        + "Добавить в бульон картофель и капусту, затем зажарку. Варить до мягкости овощей и подать со сметаной.",
                90
        ));
        recipeRepository.save(recipe(
                author,
                "olivier",
                "Салат Оливье",
                "картофель, морковь, яйца, колбаса, соленые огурцы, зеленый горошек, майонез",
                "Отварить картофель, морковь и яйца. Нарезать все ингредиенты кубиками, "
                        + "добавить горошек и заправить майонезом. Охладить перед подачей.",
                40
        ));
        recipeRepository.save(recipe(
                author,
                "syrniki",
                "Сырники",
                "творог, яйцо, сахар, мука, ванилин, растительное масло",
                "Смешать творог, яйцо, сахар и муку. Сформовать лепешки и обжарить "
                        + "на среднем огне до румяной корочки с двух сторон.",
                25
        ));
    }

    private Recipe recipe(User author, String slug, String name, String ingredients,
                          String instructions, int cookingTimeMinutes) {
        var recipe = new Recipe();
        recipe.setAuthor(author);
        recipe.setSlug(slug);
        recipe.setName(name);
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipe.setCookingTimeMinutes(cookingTimeMinutes);
        return recipe;
    }
}
