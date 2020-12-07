package miniprojekti.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationTest {
    
    private Validation testVal = new Validation();
    
    private ArrayList<String> errors;
    
    private Map<String, String> bookParams;
    private Map<String, String> podcastParams;
    private Map<String, String> videoParams;
    private Map<String, String> blogpostParams;
    
    @Before
    public void setUp() {
        bookParams = new HashMap<>(Map.of("title", "The Pragmatic Programmer",
                "type", "Book",
                "note", "A must-read for all programmers",
                "author", "Thomas David, Hunt Andrew", "isbn", "9780201616224",
                "url", "https://www.amazon.com/Pragmatic-Programmer-journey-mastery-Anniversary-ebook/dp/B07VRS84D1"));
        
        blogpostParams = new HashMap<>(Map.of("title", "InversionOfControl", 
                "type", "Blogpost",
                "note", "A very easy to understand explanation",
                "url", "https://martinfowler.com/bliki/InversionOfControl.html"));
        
        podcastParams = new HashMap<>(Map.of("title", "Developer Tea", 
                "type", "Podcast",
                "note", "Over 5 years of content",
                "author", "Jonathan Cutrell",
                "description", "Wide range of topics. Relatively short episodes",
                "url", "https://spec.fm/podcasts/developer-tea"));
        
        videoParams = new HashMap<>(Map.of("title", "Clean Code - Uncle Bob / Lesson 1",
                "type", "Video",
                "note", "Series contains 6 lessons",
                "url", "https://www.youtube.com/watch?v=7EmboKQH8lM&t"));
        
    }
    
    // yhteiset title-validaatiot
    @Test
    public void tipWithoutTitleGivesAnError() {
        updateParamAndValidate(bookParams, "title", "");
        testForMessage("'title' must be filled");
    }
    
    @Test
    public void tooShortATitleGivesAnError() {
        updateParamAndValidate(bookParams, "title", "42");
        testForMessage("'title' must be 3-50 characters");
    }
    
    // yhteiset note-validaatiot
    @Test
    public void noteDoesNotNeedToBeFilled() {
        updateParamAndValidate(bookParams, "note", "");
        assertEquals(0, errors.size());
    }
    
    @Test
    public void tooShortANoteGivesAnError() {
        updateParamAndValidate(bookParams, "note", "5h");
        testForMessage("'note' must be 3-50 characters");
    }
    
    @Test
    public void tooLongANoteGivesAnError() {
        updateParamAndValidate(bookParams, "note", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor");
        testForMessage("'note' must be 3-50 characters");
    }
    
    @Test
    public void tooLongATitleGivesAnError() {
        updateParamAndValidate(bookParams, "title", "The historical development of the Heart i.e. from its formation from Annelida: ...");
        testForMessage("'title' must be 3-50 characters");
    }
    
    // BookTip-validoinnit
    @Test
    public void bookWithPassableValuesGivesNoErrors() {
        errors = testVal.validate(bookParams);
        assertEquals(0, errors.size());
    }

    @Test
    public void bookWithoutAuthorGivesAnError() {
        updateParamAndValidate(bookParams, "author", "");
        testForMessage("'author' must be filled");
    }
    
    @Test
    public void bookDoesNotRequireISBN() {
        updateParamAndValidate(bookParams, "isbn", "");
        assertEquals(0, errors.size());
    }
    
    @Test
    public void bookWithIncorrectlyFormattedISBNGivesAnError() {
        updateParamAndValidate(bookParams, "isbn", "12345678");
        testForMessage("does not contain a valid ISBN number");
    }
    
    @Test
    public void bookDoesNotRequireUrl() {
        updateParamAndValidate(bookParams, "url", "");
        assertEquals(0, errors.size());
    }
    
    // BlogpostTip-validoinnit
    @Test
    public void blogpostWithPassableValuesGivesNoErrors() {
        errors = testVal.validate(blogpostParams);
        assertEquals(0, errors.size());
    }
    
    @Test
    public void blogpostWithoutUrlGivesAnError() {
        updateParamAndValidate(blogpostParams, "url", "");
        testForMessage("'url' must be filled");
    }
    
    @Test
    public void blogpostWithIncorrectlyFormattedUrlGivesAnError() {
        updateParamAndValidate(blogpostParams, "url", "ftp://downloadthisfile.com");
        testForMessage("does not contain a valid url");
    }
    
    // PodcastTip-validoinnit
    @Test
    public void podcastWithPassableValuesGivesNoErrors() {
        errors = testVal.validate(podcastParams);
        assertEquals(0, errors.size());
    }
    
    @Test
    public void podcastWithoutUrlGivesAnError() {
        updateParamAndValidate(podcastParams, "url", "");
        testForMessage("'url' must be filled");
    }
    
    // VideoTip-validoinnit
    @Test
    public void videoWithPassableValuesGivesNoErrors() {
        errors = testVal.validate(videoParams);
        assertEquals(0, errors.size());
    }
    
    @Test
    public void videoWithoutUrlGivesAnError() {
        updateParamAndValidate(videoParams, "url", "");
        testForMessage("'url' must be filled");
    }
    
    public void updateParamAndValidate(Map<String, String> paramMap, String fieldName, String newValue) {
        paramMap.put(fieldName, newValue);
        errors = testVal.validate(paramMap);
    }
    
    public void testForMessage(String msg) {
        assertTrue(errors.get(0).contains(msg));
    }
}
