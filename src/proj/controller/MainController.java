package proj.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.jsoup.Jsoup;
import proj.Util;

import java.util.List;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class MainController {
    @FXML
    private Label title, meal;

    @FXML
    public void initialize() throws Exception {
        String result = Util.loadFromWeb("https://bis.sasa.hs.kr/info.php");

        Source source = new Source(result);
        Element tbody = source.getAllElements(HTMLElementName.TABLE).get(1).getAllElements(HTMLElementName.TBODY).get(0);
        List<Element> tdList = tbody.getAllElements(HTMLElementName.TD);
        String t = "";
        for (Element td : tdList) t += td.getContent().toString();

        meal.setText(Jsoup.parse(t).text());
    }
}
