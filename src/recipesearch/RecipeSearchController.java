
package recipesearch;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;

//just for fun

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class RecipeSearchController implements Initializable {

    RecipeDatabase db = RecipeDatabase.getSharedInstance();
    RecipeBackendController backendController = new RecipeBackendController();

    @FXML
    FlowPane recipeFlowpane;
    @FXML
    ComboBox ingredientBox;
    @FXML
    ComboBox cuisineBox;
    @FXML
    RadioButton diffAll;
    @FXML
    RadioButton diffEasy;
    @FXML
    RadioButton diffIntermediate;
    @FXML
    RadioButton diffHard;
    @FXML
    Spinner<Integer> priceSpinner;
    @FXML
    Slider timeSlider;
    @FXML
    Label timeLabel;
    @FXML
    javafx.scene.image.ImageView recipeImage;
    @FXML
    Label recipeLabel;
    @FXML
    SplitPane searchPane;
    @FXML
    StackPane recipeDetailPane;
    @FXML
    Label ingredientsLabel;
    @FXML
    Label servingsLabel;
    @FXML
    ImageView cuisineImageView;
    @FXML
    ImageView closeImageView;
    @FXML
    Label descriptionLabel;
    @FXML
    Label instructionsLabel;
    @FXML
    Label timeDetailLabel;
    @FXML
    ImageView difficultyDetail;
    @FXML
    ImageView mainIngredientDetail;
    @FXML
    Label priceLabelDetail;
    @FXML
    AnchorPane totoPane;

    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();

    //Just for fun

    String loc = "src\\recipesearch\\resources\\africa.mp3";
    Media africa = new Media(new File(loc).toURI().toString());
    MediaPlayer africaPlayer = new MediaPlayer(africa);

    @FXML
    Button stopButton;
    @FXML
    ImageView totoImage;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        africaPlayer.setVolume(0.3);


        Platform.runLater(()->ingredientBox.requestFocus());

        for (Recipe recipe : backendController.getRecipes()) {
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }

        updateRecipeList();
        populateMainIngredientComboBox();
        populateCuisineComboBox();
        timeLabel.setText("0 minuter");

        ingredientBox.getItems().addAll("Visa alla", "Kött", "Fisk", "Kyckling", "Vegetarisk");
        ingredientBox.getSelectionModel().select("Visa alla");
        cuisineBox.getItems().addAll("Visa alla", "Frankrike", "Sverige", "Indien", "Asien", "Afrika", "Grekland");
        cuisineBox.getSelectionModel().select("Visa alla");

        ingredientBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                backendController.setMainIngredient(newValue);
                updateRecipeList();
            }
        });

        cuisineBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("Afrika")){
                    playAfrica();
                }
                if (!newValue.equals("Afrika")){
                    stopAfrica();
                }
                backendController.setCuisine(newValue);
                updateRecipeList();
            }
        });

        ToggleGroup difficultyToggleGroup = new ToggleGroup();
        diffAll.setToggleGroup(difficultyToggleGroup);
        diffEasy.setToggleGroup(difficultyToggleGroup);
        diffIntermediate.setToggleGroup(difficultyToggleGroup);
        diffHard.setToggleGroup(difficultyToggleGroup);
        diffAll.setSelected(true);

        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if (difficultyToggleGroup.getSelectedToggle() != null) {
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    backendController.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, 0, 5);

        priceSpinner.setValueFactory(valueFactory);
        priceSpinner.valueProperty().addListener(new ChangeListener<Integer>() {

            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (!(oldValue.equals(newValue))) {
                    backendController.setMaxPrice(priceSpinner.getValue());
                    updateRecipeList();
                }
            }
        });

        priceSpinner.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {
                    //focusgained - do nothing
                } else {
                    Integer value = Integer.valueOf(priceSpinner.getEditor().getText());
                    backendController.setMaxPrice(value);
                    updateRecipeList();
                }

            }
        });


        //TODO Här är slidern som inte lyckas uppdatera värdet via backendcontroller

        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {


                if (newValue.intValue() > 0 && newValue.intValue() % 10 == 0 && newValue.intValue() <= 150) { //Vi kollar om värdet är enligt villkoret
                    timeLabel.setText(newValue.intValue() + " minuter");
                    backendController.setMaxTime(newValue.intValue());
                    updateRecipeList();

                    /*if ((newValue != null && !newValue.equals(oldValue) && !timeSlider.isValueChanging())) { //om villkoret ovan gäller och den inte rör på sig, uppdatera värdet
                        backendController.setMaxTime(newValue.intValue());
                        System.out.println(newValue.intValue() +  "IN ");
                        updateRecipeList();
                    }*/

                }
            }
        });



    }

    public Image getDifficultyImage(String difficulty) {
        String iconPath;
        switch (difficulty) {
            case "Lätt":
                iconPath = "RecipeSearch/resources/icon_difficulty_easy.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Mellan":
                iconPath = "RecipeSearch/resources/icon_difficulty_medium.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Svår":
                iconPath = "RecipeSearch/resources/icon_difficulty_hard.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                default:return new Image(getClass().getClassLoader().getResourceAsStream("RecipeSearch/resources/icon_difficulty_hard.png"));


        }

    }
    public Image getIngredientImage(String ingredient) {
        String iconPath;
        switch (ingredient) {

            case "Kött":
                iconPath = "RecipeSearch/resources/icon_main_meat.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Fisk":
                iconPath = "RecipeSearch/resources/icon_main_fish.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Kyckling":
                iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
            case "Vegetarisk":
                iconPath = "RecipeSearch/resources/icon_main_veg.png";
                return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                default:return null;
        }
    }

    public Image getCuisineImage(String cuisine) {
        String iconPath;
            switch (cuisine) {
                case "Sverige":
                    iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                case "Grekland":
                    iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                case "Frankrike":
                    iconPath = "RecipeSearch/resources/icon_flag_france.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                case "Indien":
                    iconPath = "RecipeSearch/resources/icon_flag_india.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                case "Asien":
                    iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                case "Afrika":
                    iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                    return new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                    default: return new Image(getClass().getClassLoader().getResourceAsStream("RecipeSearch/resources/icon_flag_africa.png"));


            }

    }


    private void populateMainIngredientComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {

                                    case "Kött":
                                        iconPath = "RecipeSearch/resources/icon_main_meat.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Fisk":
                                        iconPath = "RecipeSearch/resources/icon_main_fish.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Kyckling":
                                        iconPath = "RecipeSearch/resources/icon_main_chicken.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Vegetarisk":
                                        iconPath = "RecipeSearch/resources/icon_main_veg.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        ingredientBox.setButtonCell(cellFactory.call(null));
        ingredientBox.setCellFactory(cellFactory);

    }

    private void populateCuisineComboBox() {
        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        setText(item);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            Image icon = null;
                            String iconPath;
                            try {
                                switch (item) {
                                    case "Sverige":
                                        iconPath = "RecipeSearch/resources/icon_flag_sweden.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Grekland":
                                        iconPath = "RecipeSearch/resources/icon_flag_greece.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Frankrike":
                                        iconPath = "RecipeSearch/resources/icon_flag_france.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Indien":
                                        iconPath = "RecipeSearch/resources/icon_flag_india.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Asien":
                                        iconPath = "RecipeSearch/resources/icon_flag_asia.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;
                                    case "Afrika":
                                        iconPath = "RecipeSearch/resources/icon_flag_africa.png";
                                        icon = new Image(getClass().getClassLoader().getResourceAsStream(iconPath));
                                        break;

                                }
                            } catch (NullPointerException ex) {
                                //This should never happen in this lab but could load a default image in case of a NullPointer
                            }
                            ImageView iconImageView = new ImageView(icon);
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };
        cuisineBox.setButtonCell(cellFactory.call(null));
        cuisineBox.setCellFactory(cellFactory);

    }


    //Gammal updateRecipes, fungerar

   /* private void updateRecipeListold() {
        recipeFlowpane.getChildren().clear();
        List<Recipe> recipes = backendController.getRecipes();
        for (Recipe r : recipes) {
            RecipeListItem item = new RecipeListItem(r, this);
            recipeFlowpane.getChildren().add(item);
        }
    }
    */

   //Nya updaterecipes m.h.a hashmap, verkar sortera som den ska
    private void updateRecipeList() {
        recipeFlowpane.getChildren().clear();
        List<Recipe> recipes = backendController.getRecipes();

            for (Recipe r : recipes) {
                recipeFlowpane.getChildren().add(recipeListItemMap.get(r.getName()));



        }
    }

    private void populateRecipeDetailView(Recipe r) {
        //Setup for text
        recipeLabel.setText(r.getName());
        servingsLabel.setText(Integer.toString(r.getServings()) + " portioner");
        ingredientsLabel.setText(formatIngredients(r));
        instructionsLabel.setText(r.getInstruction());
        descriptionLabel.setText(r.getDescription());
        timeDetailLabel.setText(Integer.toString(r.getTime()) + " minuter");
        priceLabelDetail.setText(Integer.toString(r.getPrice()) + " kr");

        //Setup for images

        cuisineImageView.setImage(getCuisineImage(r.getCuisine()));
        difficultyDetail.setImage(getDifficultyImage(r.getDifficulty()));
        mainIngredientDetail.setImage(getIngredientImage(r.getMainIngredient()));
        recipeImage.setImage(getSquareImage(r.getFXImage()));
        //System.out.println(r.getIngredients().toString());
    }

    private String formatIngredients(Recipe r){

        StringBuilder sb = new StringBuilder();
        String ingredients;

        for (int i = 0; i < r.getIngredients().size();i++){
            sb.append(r.getIngredients().get(i));
            sb.append(System.getProperty("line.separator"));

        }

        ingredients = sb.toString();

        return ingredients;
    }

    @FXML
    public void closeRecipeView() {
        searchPane.toFront();
        ingredientBox.requestFocus();
    }


    public void openRecipeView(Recipe recipe) {
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
    }

    public void playAfrica(){
        totoPane.toFront();
        africaPlayer.play();
    }

    public void stopAfrica(){
       totoPane.toBack();
        africaPlayer.stop();
        ingredientBox.requestFocus();
    }

    public Image getSquareImage(Image image){

        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if(image.getWidth() > image.getHeight()){
            width = (int) image.getHeight();
            height = (int) image.getHeight();
            x = (int)(image.getWidth() - width)/2;
            y = 0;
        }

        else if(image.getHeight() > image.getWidth()){
            width = (int) image.getWidth();
            height = (int) image.getWidth();
            x = 0;
            y = (int) (image.getHeight() - height)/2;
        }

        else{
            //Width equals Height, return original image
            return image;
        }
        return new WritableImage(image.getPixelReader(), x, y, width, height);
    }



    @FXML
    public void closeButtonMouseEntered(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_hover.png")));
    }

    @FXML
    public void closeButtonMousePressed(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close_pressed.png")));
        closeRecipeView();
    }

    @FXML
    public void closeButtonMouseExited(){
        closeImageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(
                "RecipeSearch/resources/icon_close.png")));
    }

    public void mouseTrap(Event event){
        event.consume();
    }


}
