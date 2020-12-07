package miniprojekti.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationTest {
    
    private Validation testVal = new Validation();
    
    ArrayList<String> errors;
    
    Map<String, String> bookParams;
    Map<String, String> podcastParams;
    Map<String, String> videoParams;
    Map<String, String> blogpostParams;
    
    
    public ValidationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        bookParams = new HashMap<>(Map.of("title", "The Pragmatic Programmer",
                "type", "Book",
                "note", "A must-read for all programmers",
                "author", "Thomas David, Hunt Andrew", "isbn", "9780201616224",
                "url", "https://www.amazon.com/Pragmatic-Programmer-journey-mastery-Anniversary-ebook/dp/B07VRS84D1"));
        
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
    
    public void updateParamAndValidate(Map<String, String> paramMap, String fieldName, String newValue) {
        paramMap.put(fieldName, newValue);
        errors = testVal.validate(paramMap);
    }
    
    public void testForMessage(String msg) {
        assertTrue(errors.get(0).contains(msg));
    }
}
