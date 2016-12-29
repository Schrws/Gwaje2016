package proj.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import proj.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

public class LoginController extends Controller {
    @FXML
    private Label status;
    @FXML
    private TextField id, passwd;

    @FXML
    public void Login(ActionEvent event) {
        status.setText("Please Wait...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Util.login(id.getText(), passwd.getText())) {
                    Platform.runLater(new FutureTask<>(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Login Success.");
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("/proj/FXML/main.fxml"));
                                Scene scene = new Scene(root);
                                primaryStage.setTitle("SASA BIS");
                                primaryStage.setScene(scene);
                                primaryStage.sizeToScene();
                                primaryStage.show();
                            } catch (IOException ignored) {ignored.printStackTrace();}
                        }
                    }, null));
                } else Platform.runLater(new FutureTask<>(() -> status.setText("Login Failed."), null));
            }
        }).start();
    }

    @Override
    public void loadContent() {}
}
