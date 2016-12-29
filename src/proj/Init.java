package proj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Init extends Application {
    private static Stage primaryStage, postStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Init.primaryStage = primaryStage;
        postStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/proj/FXML/login.fxml"));
        primaryStage.setTitle("LOGIN");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (Util.httpclient != null) Util.httpclient.getConnectionManager().shutdown();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Stage getPostStage() {
        return postStage;
    }
}
