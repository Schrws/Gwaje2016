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

public class LoginController extends Controller { //로그인을 할 수 있도록 하는 Controller.
    @FXML
    private Label status;
    @FXML
    private TextField id, passwd;

    @FXML
    public void Login(ActionEvent event) { //로그인 버튼이 눌렸을 때 로그인이 되도록 함. 로그인에 성공하면 primaryStage를 바꿔 메인 화면으로 이동함.
        status.setText("Please Wait...");
        new Thread(() -> {
            if (Util.login(id.getText(), passwd.getText())) {
                Platform.runLater(new FutureTask<>(() -> {
                    status.setText("Login Success.");
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/proj/FXML/main.fxml"));
                        Scene scene = new Scene(root);
                        primaryStage.setTitle("SASA BIS");
                        primaryStage.setScene(scene);
                        primaryStage.sizeToScene();
                        primaryStage.show();
                    } catch (IOException ignored) {ignored.printStackTrace();}
                }, null));
            } else Platform.runLater(new FutureTask<>(() -> status.setText("Login Failed."), null));
        }).start();
    }

    @Override
    public void loadContent() {} //로그인 시에 내용을 불러올 것이 없다.
}