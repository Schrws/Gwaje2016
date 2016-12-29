package proj.Controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import proj.Init;

/**
 * Created by SchrwsK on 2016-12-27.
 */
public abstract class Controller {
    Stage primaryStage, postStage;

    @FXML
    public void initialize() throws Exception {
        primaryStage = Init.getPrimaryStage();
        postStage = Init.getPostStage();
        loadContent();
    }

    abstract void loadContent();
}
