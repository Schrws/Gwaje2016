package proj.controller;

import com.sun.istack.internal.NotNull;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import proj.Util;

import java.util.List;

public class LoginController {
    @FXML
    private Label status;
    @FXML
    private TextField id, passwd;

    @FXML
    public void Login(ActionEvent event) throws Exception {
        if (Util.login(id.getText(), passwd.getText())) {
            status.setText("Login Success.");

            Stage primaryStage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
            Parent root = FXMLLoader.load(getClass().getResource("/proj/main.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("SASA BIS");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            status.setText("Login Failed.");
        }
    }
}
