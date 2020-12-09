package miniprojekti.domain;

import java.util.HashMap;
import miniprojekti.data_access.*;
import org.junit.*;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.anyInt;

public class LogicTest {
    ReadingTipDao testDao;
    Logic l;

    public LogicTest() {
    }
    
    @Before
    public void setUp() {
        testDao = mock(ReadingTipDao.class);
        l = new Logic(testDao);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void newBookTipIsSaved() {
        HashMap<String, String> tipMap = new HashMap<>();
        tipMap.put("id", "0");
        tipMap.put("title", "title");
        tipMap.put("type", "book");
        tipMap.put("note", "note");
        tipMap.put("author", "author");
        tipMap.put("isbn", "isbn");
        tipMap.put("url", "url");
        l.saveNewTip(tipMap, 0);
        verify(testDao).save(any(BookTip.class), anyInt());
    }

    @Test
    public void tipsRetrievedByType(){
        l.retrieveAllTipsByType("author", 0);
        verify(testDao).findAll_(0);
    }
    
    @Test
    public void allTipsRetrieved(){
        l.retrieveAllTips(0);
        verify(testDao).findAll_(0);
    }

    @Test
    public void newPodcastTipIsSaved() {
        HashMap<String, String> tipMap = new HashMap<>();
        tipMap.put("id", "0");
        tipMap.put("title", "title");
        tipMap.put("note", "note");
        tipMap.put("author", "author");
        tipMap.put("type", "podcast");
        tipMap.put("description", "description");
        tipMap.put("url", "url");
        l.saveNewTip(tipMap, 0);
        verify(testDao).save(any(PodcastTip.class), anyInt());
    }

    @Test
    public void newVideoTipIsSaved() { 
        HashMap<String, String> tipMap = new HashMap<>();
        tipMap.put("id", "1");
        tipMap.put("title", "title");
        tipMap.put("note", "note");
        tipMap.put("type", "video");
        tipMap.put("url", "url");
        l.saveNewTip(tipMap, 0);
        verify(testDao).save(any(VideoTip.class), anyInt());
    }

    @Test
    public void newBlogpostTipIsSaved() {
        HashMap<String, String> tipMap = new HashMap<>();
        tipMap.put("id", "1");
        tipMap.put("title", "title");
        tipMap.put("type", "blogpost");
        tipMap.put("note", "note");
        tipMap.put("url", "url");
        l.saveNewTip(tipMap, 0);
        verify(testDao).save(any(BlogpostTip.class), anyInt());
    }
   
    @Test
    public void tipIsDeletedByID() {
        l.deleteTipByID(0);
        verify(testDao).deleteByID(0);
    }

    @Test
    public void tipIsDeletedByTitle() {
        String title = "title";
        l.deleteTipByTitle(title);
        verify(testDao).deleteByTitle(title);
    }  
}
