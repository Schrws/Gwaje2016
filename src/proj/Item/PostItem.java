package proj.Item;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class PostItem { //여러 게시글을 받아올 때 List로 저장하기 위한 클래스.
    private String title, link, author, date;

    public PostItem(String title, String link, String author, String date) { //값 저장
        this.title = title;
        this.link = link;
        this.author = author;
        this.date = date;
    }

    //해당 값을 반환.
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }
}
