package proj.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.jsoup.Jsoup;
import proj.Util;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by SchrwsK on 2016-12-27.
 */
public class PostController extends Controller { //게시글의 내용을 화면에 표시하는 Controller.
    @FXML
    Text title, author;
    @FXML
    WebView content;
    @FXML
    Hyperlink browser;

    @Override
    public void loadContent() { //Controller에서 상속받아 반드시 구현하도록 되어 있으며, 게시글의 내용을 불러와 표시한다.
        new Thread(() -> {
            try {
                String link = "https://bis.sasa.hs.kr" + postStage.getUserData().toString();
                String result = Util.loadFromWeb(link);

                Source source = new Source(result);
                List<Element> divList = source.getAllElementsByClass("viewDiv");
                List<Element> divList2 = source.getAllElementsByClass("viewDiv2");
                Platform.runLater(new FutureTask<>(() -> {
                    title.setText(Jsoup.parse(divList.get(1).getContent().toString()).text());
                    author.setText(Jsoup.parse(divList.get(0).getContent().toString()).text());
                    content.getEngine().loadContent(Jsoup.parse(divList2.get(0).getContent().toString()).text());
                    browser.setText("브라우저에서 열기");
                    browser.setOnAction(event -> {
                        try {
                            Desktop.getDesktop().browse(new URI(link));
                        } catch (Exception ignored) {ignored.printStackTrace();}
                    });
                    postStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {}
        }).start();
    }
}
