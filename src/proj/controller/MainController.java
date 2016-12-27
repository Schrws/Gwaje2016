package proj.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.jsoup.Jsoup;
import proj.Init;
import proj.Util;
import proj.item.PostItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class MainController extends Controller {
    private List<PostItem> postItems;
    private List<Label> labels;
    @FXML
    private Label title;
    @FXML
    private VBox meal_list;
    @FXML
    private GridPane post_list;

    @Override
    public void loadContent() {
        postItems = new ArrayList<>();
        labels = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = Util.loadFromWeb("https://bis.sasa.hs.kr/info.php");

                    Source source = new Source(result);
                    Element tbody_post = source.getAllElements(HTMLElementName.TABLE).get(0).getAllElements(HTMLElementName.TBODY).get(0);
                    Element tbody_meal = source.getAllElements(HTMLElementName.TABLE).get(1).getAllElements(HTMLElementName.TBODY).get(0);
                    List<Element> trList_post = tbody_post.getAllElements(HTMLElementName.TR);
                    List<Element> tdList_meal = tbody_meal.getAllElements(HTMLElementName.TD);
                    for (Element tr : trList_post) {
                        List<Element> tdList_post = tr.getAllElements(HTMLElementName.TD);
                        Element a = tdList_post.get(0).getAllElements(HTMLElementName.A).get(0);
                        String title = Jsoup.parse(a.getContent().toString()).text();
                        String link = a.getAttributeValue("href");
                        String author = tdList_post.get(1).getContent().toString();
                        String date = tdList_post.get(2).getContent().toString();

                        postItems.add(new PostItem(title, link, author, date));
                    }
                    for (Element td : tdList_meal) {
                        Label label = new Label(Jsoup.parse(td.getContent().toString()).text());
                        labels.add(label);
                    }
                    Platform.runLater(new FutureTask<>(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < postItems.size(); i++) {
                                PostItem item = postItems.get(i);
                                Hyperlink label_title = new Hyperlink(item.getTitle().trim());
                                label_title.setId(item.getLink());
                                label_title.setOnAction(event -> {
                                    try {
                                        Stage postStage = Init.getPostStage();
                                        postStage.setUserData(((Hyperlink) event.getSource()).getId());
                                        postStage.setTitle(((Hyperlink) event.getSource()).getText());
                                        postStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/proj/post.fxml"))));
                                        postStage.show();
                                    } catch (Exception ignored) {ignored.printStackTrace();}
                                });
                                post_list.add(label_title, 0, i);
                                post_list.add(new Label(item.getAuthor().trim()), 1, i);
                                post_list.add(new Label(item.getDate().trim()), 2, i);
                            }
                            for (Label label : labels)
                                meal_list.getChildren().add(label);
                            primaryStage.sizeToScene();
                        }
                    }, null));
                } catch (Exception ignored) {ignored.printStackTrace();}
            }
        }).start();
    }
}
