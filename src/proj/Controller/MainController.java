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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class MainController extends Controller { //글 목록 등 여러 탭의 내용을 관리하는 Controller.
    private List<PostItem> postItems;
    private List<Label> labels;
    private Calendar now;
    private SimpleDateFormat sdfDate;
    @FXML
    private VBox meal_list;
    @FXML
    private GridPane post_list, notice_list, lost_list, subj_list, free_list;
    @FXML
    private Tab maintab, noticetab, losttab, subjtab, freetab;
    @FXML
    private Button ndate, pdate;
    @FXML
    private Pagination pagination_notice, pagination_lost, pagination_subj, pagination_free;

    @FXML
    @Override
    public void initialize() throws Exception { //메인 화면 구성 시 초기화 - 각종 이벤트 설정
        primaryStage = Init.getPrimaryStage();
        postStage = Init.getPostStage();
        now = Calendar.getInstance();
        sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        pagination_notice.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(noticetab, new BoardRunnable(notice_list, newValue.intValue() + 1, 1)));
        pagination_lost.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(losttab, new BoardRunnable(lost_list, newValue.intValue() + 1, 2)));
        pagination_subj.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(subjtab, new BoardRunnable(subj_list, newValue.intValue() + 1, 9)));
        pagination_free.currentPageIndexProperty()
                .addListener((observable, oldValue, newValue) -> loadContent(freetab, new BoardRunnable(free_list, newValue.intValue() + 1, 5)));
    }

    @Override
    public void loadContent() { //탭이 여러개이므로 각각 표시해야할 위치가 다르므로 사용하지 않음.
    }

    private void loadContent(Tab tab, Runnable runnable) { // loacContent() 대신 표시해야 할 위치 및 내용을 받아 다른 Thread를 실행시킨다.
        if (tab.isSelected()) new Thread(runnable).start();
    }

    @FXML
    public void tabChanged(Event event) { //Tab이 바뀐 것을 감지하는 이벤트. 탭이 바뀌면 해당 탭의 내용을 불러오도록 함.
        Tab tab = (Tab) event.getSource();
        switch (tab.getId()) {
            case "maintab": loadContent(tab, new MainRunnable()); break;
            case "noticetab": loadContent(tab, new BoardRunnable(notice_list, 1, 1)); break;
            case "losttab": loadContent(tab, new BoardRunnable(lost_list, 1, 2)); break;
            case "subjtab": loadContent(tab, new BoardRunnable(subj_list, 1, 9)); break;
            case "freetab": loadContent(tab, new BoardRunnable(free_list, 1, 5)); break;
        }
    }

    @FXML
    public void dateClicked(Event event) { //날짜 변경 버튼이 눌렸을 때 새로 로드
        Button button = (Button) event.getSource();
        switch (button.getId()) {
            case "ndate": now.add(Calendar.DATE, 1); loadContent(maintab, new MainRunnable(sdfDate.format(now.getTime()))); break;
            case "pdate": now.add(Calendar.DATE, -1); loadContent(maintab, new MainRunnable(sdfDate.format(now.getTime()))); break;
        }
    }

    private void addPost(GridPane pane) { //공지사항, 분실물 등 같은 내용의 코드가 있어 하나의 메소드로 처리함. 글의 목록을 화면에 표시함.
        pane.getChildren().clear();
        for (int i = 0; i < postItems.size(); i++) {
            PostItem item = postItems.get(i);
            Hyperlink label_title = new Hyperlink(item.getTitle().trim());
            label_title.setId(item.getLink());
            label_title.setOnAction(event -> { //HyperLink가 클릭되면 글의 내용을 표시하는 postStage가 뜨도록 함.
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

    private class MainRunnable implements Runnable { //메인화면의 내용을 불러와 표시함.
        String link = "https://bis.sasa.hs.kr/info.php";

        public MainRunnable() {}
        public MainRunnable(String date) {this.link = link + "?date=" + date;}

        @Override
        public void run() {
            try {
                postItems = new ArrayList<>();
                labels = new ArrayList<>();
                String result = Util.loadFromWeb(link);

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
                    addPost(post_list); //게시글 Pane에 추가
                    meal_list.getChildren().clear();
                    for (Label label : labels)
                        meal_list.getChildren().add(label); //급식 VBox에 추가
                    primaryStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    };

    private class BoardRunnable implements Runnable { //각 게시판의 내용을 불러와 화면에 표시/
        int page;
        int board_id;
        GridPane pane;

        public BoardRunnable(GridPane pane, int page, int board_id) {this.pane = pane; this.page = page; this.board_id = board_id;} //게시판, 페이지, 게시판 id를 받아 해당 게시판의 내용을 표시함.

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
                    postItems.add(new PostItem(title, link, author, date)); //게시글 List에 추가
                }

                Platform.runLater(new FutureTask<>(() -> {
                    addPost(pane); //실제 게시판 Pane에 추가
                    primaryStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {ignored.printStackTrace();}
        }
    }
}