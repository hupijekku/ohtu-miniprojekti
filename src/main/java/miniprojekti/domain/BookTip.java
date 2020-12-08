package miniprojekti.domain;

import java.util.Map;

public class BookTip extends Tip{
    
    private int id;
    private String author;
    private String isbn;
    private String url;
    
    public BookTip(int tipId, String title, String note, int id, String author, String isbn, String url) {
        super(tipId, title, "Book", note);
        this.id = id;
        this.author = author;
        this.isbn = isbn;
        this.url = url;
    }
    
    public static BookTip createBook(Map<String, String> attr) {
        return new BookTip(0, attr.get("title"), attr.get("note"), 0, attr.get("author"), attr.get("isbn"), attr.get("url"));
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getUrl() {
        return url;
    }
    
}
