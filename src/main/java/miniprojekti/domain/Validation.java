package miniprojekti.domain;

import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.validator.routines.*;

public class Validation {
    
    private static ArrayList<String> errorMessages;
    private static Map<String, String> params;
    
    public Validation() {
    }
    
    public ArrayList<String> validate(Map<String, String> queryParams) {
        errorMessages = new ArrayList<>();
        params = queryParams;

        // Kaikilla vinkeilla kaytossa olevat validaatiot
        validateTitle(params.get("title"));
        validateNote(params.get("note"));
        
        switch (params.get("type").toLowerCase()) {
            case "book":
                validateBook();
                break;
            case "blogpost":
                validateBlogpost();
                break;
            case "podcast":
                validatePodcast();
                break;
            case "video":
                validateVideo();
                break;
            default:
        }
        return errorMessages;
    }
    
    private static void validateBook() {
        BookTip book = (BookTip) TipFactory.createTip(params);
        
        validLength(book.getAuthor(), "author", true);
        validISBN(book.getIsbn());
        validUrl(book.getUrl(), false);
    }
    
    private static void validateBlogpost() {
        BlogpostTip blogpost = (BlogpostTip) TipFactory.createTip(params);
        
        validUrl(blogpost.getUrl(), true);
    }
    
    private static void validateVideo() {
        VideoTip video = (VideoTip) TipFactory.createTip(params);
        
        validUrl(video.getUrl(), true);
    }

    private static void validatePodcast() {
        PodcastTip podcast = (PodcastTip) TipFactory.createTip(params);
        
        validLength(podcast.getAuthor(), "author", false);
        validLength(podcast.getDescription(), "description", false);
        validUrl(podcast.getUrl(), true);
    }
    
    private static void validateTitle(String field) {
        validLength(field, "title", true);
    }
    
    private static void validateNote(String field) {
        validLength(field, "note", false);
    }
    
    private static void validUrl(String field, boolean required) {
        if (field.isBlank()) {
            if (required) {
                errorMessages.add(fieldError("url") + "must be filled");
            }
        } else {
            String[] schemes = {"http", "https"};
            UrlValidator urlValidator = new UrlValidator(schemes);
            
            if (!urlValidator.isValid(field)) {
                errorMessages.add(fieldError("url") + "does not contain a valid url: use format http(s)://example.com");
            }
        }
    }
    
    private static void validLength(String field, String attr, boolean required) {
        if (field.isBlank()) {
            if (required) {
                errorMessages.add(fieldError(attr) + "must be filled");
            }
        } else if (field.length() < 3 || field.length() > 50) {
            errorMessages.add(fieldError(attr) + "must be 3-30 characters long");
        }
    }
    
    private static void validISBN(String field) {
        if (field.isBlank()) {
            return;
        }
        
        ISBNValidator isbnValidator = new ISBNValidator();
        if (!isbnValidator.isValid(field)) {
            errorMessages.add(fieldError("isbn") + "does not contain a valid ISBN number");
        }
    }
    
    private static String fieldError(String attr) {
        return "Field " + "'"+attr+"' ";
    }
}
