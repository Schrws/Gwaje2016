package proj.item;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class PostItem {
    private String title, link, author, date, content, attachment;

    public PostItem(String title, String link, String author, String date) {
        this.title = title;
        this.link = link;
        this.author = author;
        this.date = date;
    }

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

    public String getContent() {
        return content;
    }

    public String getAttachment() {
        return attachment;
    }
}
