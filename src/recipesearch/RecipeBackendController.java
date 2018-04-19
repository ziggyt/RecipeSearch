package recipesearch;

import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;
import se.chalmers.ait.dat215.lab2.SearchFilter;

import java.util.List;

public class RecipeBackendController {
    RecipeDatabase db = RecipeDatabase.getSharedInstance();

    private String cuisine = null;
    private String mainIngredient = null;
    private String difficulty = null;
    private int maxPrice = 0;
    private int maxTime = 0;


    public List<Recipe> getRecipes() {


        List<Recipe> recipes = db.search(new SearchFilter(difficulty, maxTime, cuisine, maxPrice, mainIngredient));

        return recipes;
    }


    public void setCuisine(String cuisine) {
        switch (cuisine) {
            case "Frankrike":
                this.cuisine = cuisine;
                break;
            case "Sverige":
                this.cuisine = cuisine;
                break;
            case "Indien":
                this.cuisine = cuisine;
                break;
            case "Asien":
                this.cuisine = cuisine;
                break;
            case "Afrika":
                this.cuisine = cuisine;
                break;
            case "Grekland":
                this.cuisine = cuisine;
                break;
            default:
                this.cuisine = null;
                break;


        }
    }


    public void setMainIngredient(String mainIngredient) {
        switch (mainIngredient) {
            case "Kött":
                this.mainIngredient = mainIngredient;
                break;
            case "Fisk":
                this.mainIngredient = mainIngredient;
                break;
            case "Kyckling":
                this.mainIngredient = mainIngredient;
                break;
            case "Vegetarisk":
                this.mainIngredient = mainIngredient;
                break;
            default:
                this.mainIngredient = null;
                break;

        }
    }


    public void setDifficulty(String difficulty) {
        switch (difficulty) {
            case "Lätt":
                this.difficulty = difficulty;
                break;
            case "Mellan":
                this.difficulty = difficulty;
                break;
            case "Svår":
                this.difficulty = difficulty;
                break;
            default:
                this.difficulty = null;
                break;

        }

    }


    public void setMaxPrice(int maxPrice) {
        if (maxPrice > 0) {
            this.maxPrice = maxPrice;


        }
        else this.maxPrice = 0;


    }


    public void setMaxTime(int maxTime) {
        if (maxTime > 0 && maxTime % 10 == 0 && maxTime <= 150) {
            this.maxTime = maxTime;
            System.out.println(maxTime);
        }

    }

}
