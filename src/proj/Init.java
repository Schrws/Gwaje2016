package proj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Init extends Application { //main 메소드를 포함한 가장 먼저 실행되는 클래스
    private static Stage primaryStage, postStage;
    @Override
    public void start(Stage primaryStage) throws Exception { //로그인 stage를 실행시킨다.
        Init.primaryStage = primaryStage;
        postStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/proj/FXML/login.fxml"));
        primaryStage.setTitle("LOGIN");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    } //JAVAFX 실행

    @Override
    public void stop() { //로그인을 유지할 수 있게 모든 클래스에서 접근할 수 있도록 해 두었으므로 프로그램이 종료될 때 닫도록 한다.
        if (Util.httpclient != null) Util.httpclient.getConnectionManager().shutdown();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    } //Controller에서 메인 화면 및 여러 탭을 띄우는 primaryStage를 빈환함.

    public static Stage getPostStage() {
        return postStage;
    } //Controller에서 글을 화면에 표시하는 postStage를 반환함.
}
