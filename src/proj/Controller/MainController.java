package proj.Controller;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import proj.Init;
import proj.Item.PostItem;
import proj.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class MainController extends Controller {
    List<PostItem> postItems;
    List<Label> labels;
    @FXML
    private Label title;
    @FXML
    private VBox meal_list;
    @FXML
    private TabPane tabpane;
    @FXML
    private GridPane post_list, notice_list, lost_list, subj_list, free_list;
    @FXML
    private Tab maintab, noticetab, losttab, subjtab, freetab;
    @FXML
    private Pagination pagination_notice, pagination_lost, pagination_subj, pagination_free;

    @FXML
    @Override
    public void initialize() throws Exception {
        primaryStage = Init.getPrimaryStage();
        postStage = Init.getPostStage();
        pagination_notice.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(noticetab, new BoardRunnable(notice_list, newValue.intValue() + 1, 1)));
        pagination_lost.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(losttab, new BoardRunnable(lost_list, newValue.intValue() + 1, 2)));
        pagination_subj.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(subjtab, new BoardRunnable(subj_list, newValue.intValue() + 1, 9)));
        pagination_free.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(freetab, new BoardRunnable(free_list, newValue.intValue() + 1, 5)));
        loadContent();
    }

    @Override
    public void loadContent() {
    }

    private void loadContent(Tab tab, Runnable runnable) {
        if (tab.isSelected()) new Thread(runnable).start();
    }

    @FXML
    public void tabChanged(Event event) {
        Tab tab = (Tab) event.getSource();
        switch (tab.getId()) {
            case "maintab": loadContent(tab, mainRunnable); break;
            case "noticetab": loadContent(tab, new BoardRunnable(notice_list, 1, 1)); break;
            case "losttab": loadContent(tab, new BoardRunnable(lost_list, 1, 2)); break;
            case "subjtab": loadContent(tab, new BoardRunnable(subj_list, 1, 9)); break;
            case "freetab": loadContent(tab, new BoardRunnable(free_list, 1, 5)); break;
        }
    }

    private void addPost(GridPane pane) {
        pane.getChildren().clear();
        for (int i = 0; i < postItems.size(); i++) {
            PostItem item = postItems.get(i);
            Hyperlink label_title = new Hyperlink(item.getTitle().trim());
            label_title.setId(item.getLink());
            label_title.setOnAction(event -> {
                try {
                    postStage.setUserData(((Hyperlink) event.getSource()).getId());
                    postStage.setTitle(((Hyperlink) event.getSource()).getText());
                    postStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/proj/FXML/post.fxml"))));
                    postStage.show();
                } catch (Exception ignored) {ignored.printStackTrace();}
            });
            pane.add(label_title, 0, i);
            pane.add(new Label(item.getAuthor().trim()), 1, i);
            pane.add(new Label(item.getDate().trim()), 2, i);
        }
    }

    private Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                postItems = new ArrayList<>();
                labels = new ArrayList<>();
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
                Platform.runLater(new FutureTask<>(() -> {
                    addPost(post_list);
                    meal_list.getChildren().clear();
                    for (Label label : labels)
                        meal_list.getChildren().add(label);
                    primaryStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    };

    private class BoardRunnable implements Runnable {
        int page;
        int board_id;
        GridPane pane;

        public BoardRunnable(GridPane pane, int page, int board_id) {this.pane = pane; this.page = page; this.board_id = board_id;}

        @Override
        public void run() {
            try {
                postItems = new ArrayList<>();
                labels = new ArrayList<>();
                String result = Util.loadFromWeb("https://bis.sasa.hs.kr/common/board/list.php?board_id=" + board_id + "&page=" + page + "&search=");

                Document document = Jsoup.parse(result);
                Elements elements = document.select("tbody tr");
                for (org.jsoup.nodes.Element tr : elements) {
                    org.jsoup.nodes.Element a = tr.select("a").get(0);
                    Elements tdList = tr.select("td");
                    String title = Jsoup.parse(a.text()).text();
                    String link = "/common/board/" + a.attr("href");
                    String author = tdList.get(2).text();
                    String date = tdList.get(3).text();
                    postItems.add(new PostItem(title, link, author, date));
                }

                Platform.runLater(new FutureTask<>(() -> {
                    addPost(pane);
                    primaryStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    }
}