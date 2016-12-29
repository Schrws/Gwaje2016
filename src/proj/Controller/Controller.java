package proj.Controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import proj.Init;

/**
 * Created by SchrwsK on 2016-12-27.
 */
public abstract class Controller { //공통적인 Controller
    Stage primaryStage, postStage;

    @FXML
    public void initialize() throws Exception { // 모든 Controller에서 공통적으로 사용하는 초기화 메소드.
        this.primaryStage = Init.getPrimaryStage();
        this.postStage = Init.getPostStage();
        loadContent();
    }

    abstract void loadContent(); //각 탭의 내용을 불러오는 메소드 - 상속받아 구현해야 함.
}
