package proj.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.jsoup.Jsoup;
import proj.Init;
import proj.Util;
import proj.item.PostItem;

import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Created by SchrwsK on 2016-12-27.
 */
public class PostController extends Controller{
    @FXML
    Text title, author;
    @FXML
    Text content;

    @Override
    public void loadContent() {
        new Thread(() -> {
            try {
                String link = postStage.getUserData().toString();
                String result = Util.loadFromWeb("https://bis.sasa.hs.kr" + link);

                Source source = new Source(result);
                List<Element> divList = source.getAllElementsByClass("viewDiv");
                List<Element> divList2 = source.getAllElementsByClass("viewDiv2");

                Platform.runLater(new FutureTask<>(() -> {
                    title.setText(Jsoup.parse(divList.get(1).getContent().toString()).text());
                    author.setText(Jsoup.parse(divList.get(0).getContent().toString()).text());
                    content.setText(Jsoup.parse(divList2.get(0).getContent().toString()).text());
                    postStage.sizeToScene();
                }, null));
            } catch (Exception ignored) {ignored.printStackTrace();}
        }).start();
    }
}
