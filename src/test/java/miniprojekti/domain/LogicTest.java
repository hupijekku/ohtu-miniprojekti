package miniprojekti.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miniprojekti.data_access.*;
import org.junit.*;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.mockito.Matchers.anyInt;

public class LogicTest {
    ReadingTipDao testDao;
    Logic l;
    Authentication auth;

    private static HashMap<String, String> videoMap;
    private static HashMap<String, String> bookMap;
    private static HashMap<String, String> blogpostMap;
    private static HashMap<String, String> podcastMap;
    
    public LogicTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        videoMap = new HashMap<>();
        videoMap.put("id", "0");
        videoMap.put("title", "title");
        videoMap.put("note", "note");
        videoMap.put("type", "video");
        videoMap.put("url", "url");
        
        bookMap = new HashMap<>();
        bookMap.put("id", "0");
        bookMap.put("title", "title");
        bookMap.put("type", "book");
        bookMap.put("note", "note");
        bookMap.put("author", "author");
        bookMap.put("isbn", "isbn");
        bookMap.put("url", "url");
        
        blogpostMap = new HashMap<>();
        blogpostMap.put("id", "0");
        blogpostMap.put("title", "title");
        blogpostMap.put("type", "blogpost");
        blogpostMap.put("note", "note");
        blogpostMap.put("url", "url");
        
        podcastMap = new HashMap<>();
        podcastMap.put("id", "0");
        podcastMap.put("title", "title");
        podcastMap.put("note", "note");
        podcastMap.put("author", "author");
        podcastMap.put("type", "podcast");
        podcastMap.put("description", "description");
        podcastMap.put("url", "url");
    }
    
    @Before
    public void setUp() {
        testDao = mock(ReadingTipDao.class);
        auth = mock(Authentication.class);
        l = new Logic(testDao);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void newBookTipIsSaved() {
        l.saveNewTip(bookMap, 0);
        verify(testDao).save(any(BookTip.class), anyInt());
    }

    @Test
    public void tipsRetrievedByType(){
        l.retrieveAllTipsByType("author", 0);
        verify(testDao).findAll_(0);
    }
    
    @Test
    public void allTipsRetrieved(){
        HashMap<String, String> tipMap = new HashMap<>();
        tipMap.put("id", "0");
        tipMap.put("title", "title");
        tipMap.put("note", "note");
        tipMap.put("author", "author");
        tipMap.put("type", "podcast");
        tipMap.put("description", "description");
        tipMap.put("url", "url");
        l.saveNewTip(tipMap, 0);
        
        l.retrieveAllTips(0);
        verify(testDao).findAll_(0);
    }

    @Test
    public void newPodcastTipIsSaved() {
        l.saveNewTip(podcastMap, 0);
        verify(testDao).save(any(PodcastTip.class), anyInt());
    }

    @Test
    public void newVideoTipIsSaved() { 
        l.saveNewTip(videoMap, 0);
        verify(testDao).save(any(VideoTip.class), anyInt());
    }

    @Test
    public void newBlogpostTipIsSaved() {
        l.saveNewTip(blogpostMap, 0);
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
    
    @Test
    public void tipIsUpdated() {
        Tip toUpdate = TipFactory.createTip(videoMap);
        
        l.updateTip(toUpdate);
        verify(testDao).updateTip(toUpdate);
    }
    
    @Test
    public void unregisteredUserIsNotFound() {
        int result = l.validateUser("testUser", "testPassword");
        assertEquals(-1, result);
    }
    
    @Test
    public void userIsRegistered() {
        l.registerUser("testUser", "testPassword");
    }
    
    // Kaikkien lukuvinkkien haku, eri lukuvinkkityyppejä "tallennettuna"
    @Test
    public void videoTipsAreReturnedWithAllTips() {
        setfindAllMockList(videoMap);
        retrieveAllTipsAndVerify();
    } 
    
    @Test
    public void bookTipsAreReturnedWithAllTips() {
        setfindAllMockList(bookMap);
        retrieveAllTipsAndVerify();
    }
     
    @Test
    public void blogpostTipsAreReturnedWithAllTips() {
        setfindAllMockList(blogpostMap);
        retrieveAllTipsAndVerify();
    }
    
    @Test
    public void podcastTipsAreReturnedWithAllTips() {
        setfindAllMockList(podcastMap);
        retrieveAllTipsAndVerify();
    }
    
    // Haku lukuvinkkityyppien mukaan
    @Test
    public void videoTipsAreReturnedByType() {
        setfindAllMockList(videoMap);
        retrieveTipsByTypeAndVerify("Video");
    }
    
    @Test
    public void blogpostTipsAreReturnedByType() {
        setfindAllMockList(blogpostMap);
        retrieveTipsByTypeAndVerify("Blogpost");
    }
    
    @Test
    public void bookTipsAreReturnedByType() {
        setfindAllMockList(bookMap);
        retrieveTipsByTypeAndVerify("Book");
    }
    
    @Test
    public void podcastTipsAreReturnedByType() {
        setfindAllMockList(podcastMap);
        retrieveTipsByTypeAndVerify("Podcast");
    }
    
    // Yksittäisten vinkkien haku
    @Test
    public void podcastTipIsRetrieved() {
        setfindAllMockList(podcastMap);
        retrieveSingleTipAndVerify();
    }
    
    @Test
    public void bookTipIsRetrieved() {
        setfindAllMockList(bookMap);
        retrieveSingleTipAndVerify();
    }
    
    @Test
    public void blogpostTipIsRetrieved() {
        setfindAllMockList(blogpostMap);
        retrieveSingleTipAndVerify();
    }
    
    @Test
    public void videoTipIsRetrieved() {
        setfindAllMockList(videoMap);
        retrieveSingleTipAndVerify();
    }
    
    private void setfindAllMockList(HashMap<String, String> tipMap) {
        List<Tip> returnList = new ArrayList<>();
        returnList.add(TipFactory.createTip(tipMap));
        
        when(testDao.findAll_(0)).thenReturn(returnList);
    }
    
    private void retrieveAllTipsAndVerify() {
        l.retrieveAllTips(0);
        verify(testDao).findAll_(0);
    }
    
    private void retrieveTipsByTypeAndVerify(String type) {
        l.retrieveAllTipsByType(type, 0);
        verify(testDao).findAll_(0);
    }
    
    private void retrieveSingleTipAndVerify() {
        l.retrieveTip("0", 0);
        verify(testDao).findAll_(0);
    }
}
