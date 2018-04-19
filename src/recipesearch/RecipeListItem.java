package recipesearch;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.io.IOException;

public class RecipeListItem extends AnchorPane {
    private RecipeSearchController parentController;
    private Recipe recipe;

    @FXML
    ImageView recipeImage;
    @FXML
    Label recipeName;
    @FXML
    Label recipeDescription;
    @FXML
    Label recipeTime;
    @FXML
    ImageView countryImage;
    @FXML
    ImageView difficultyImage;
    @FXML
    Label priceLabel;
    @FXML
    ImageView ingredientImage;


    public RecipeListItem(Recipe recipe, RecipeSearchController recipeSearchController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recipe_listitem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.recipe = recipe;
        this.parentController = recipeSearchController;

        //Setup for text fields
        recipeName.setText(recipe.getName());
        recipeImage.setImage(recipeSearchController.getSquareImage(recipe.getFXImage()));
        recipeDescription.setText(recipe.getDescription());
        recipeTime.setText(Integer.toString(recipe.getTime()) + " minuter");
        priceLabel.setText(Integer.toString(recipe.getPrice()) + " kr");

        //Setup for images
        countryImage.setImage(recipeSearchController.getCuisineImage(recipe.getCuisine()));
        difficultyImage.setImage(recipeSearchController.getDifficultyImage(recipe.getDifficulty()));
        ingredientImage.setImage(recipeSearchController.getIngredientImage(recipe.getMainIngredient()));


    }

    @FXML
    protected void onClick(Event event) {
        parentController.openRecipeView(recipe);
    }

}