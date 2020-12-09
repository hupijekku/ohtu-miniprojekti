package miniprojekti.domain;

// Voitaisiin sijoittaa Mainin ja Dao:n v�linen sovelluslogiikka ainakin aluksi t�nne

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miniprojekti.data_access.ReadingTipDao;
import miniprojekti.database.Database;

public class Logic {

    private static ReadingTipDao readingTipDao = new ReadingTipDao(new Database("jdbc:sqlite:readingtips.db"));

    public Logic() {
    }

    public Logic(ReadingTipDao testiTipDao) {
        this.readingTipDao = testiTipDao;
    }

    public List<HashMap<String, String>> retrieveAllTips(int user_id) {
        List<Tip> tips = readingTipDao.findAll_(user_id);
        ArrayList<HashMap<String, String>> modelTips = new ArrayList<>();
        
        for (Tip tip : tips) {
            HashMap<String, String> tipMap = new HashMap<>();
            
            tipMap.put("id", String.valueOf(tip.getTipId()));
            tipMap.put("title", tip.getTitle());
            tipMap.put("type", tip.getType());
            tipMap.put("note", tip.getNote());
            
            addTypeSpecificParameters(tipMap, tip);
            
            modelTips.add(tipMap);
        }

        return modelTips;
    }

    public List<HashMap<String, String>> retrieveTip(String s, int user_id) {
        int id = Integer.parseInt(s);
        List<Tip> list = readingTipDao.findAll_(user_id);
        ArrayList<HashMap<String, String>> modelTips = new ArrayList<>();
        for (Tip tip : list) {
            if (tip.tipId == id) {
                HashMap<String, String> tipMap = new HashMap<>();
                switch (tip.getType()) {
                    case "Book":
                        BookTip booktip = (BookTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", booktip.getTitle());
                        tipMap.put("type", booktip.getType());
                        tipMap.put("note", booktip.getNote());
                        tipMap.put("author", booktip.getAuthor());
                        tipMap.put("isbn", booktip.getIsbn());
                        tipMap.put("url", booktip.getUrl());
                        break;
                    case "Video":
                        VideoTip videotip = (VideoTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", videotip.getTitle());
                        tipMap.put("type", videotip.getType());
                        tipMap.put("note", videotip.getNote());
                        tipMap.put("url", videotip.getUrl());
                        break;
                    case "Blogpost":
                        BlogpostTip blogposttip = (BlogpostTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", blogposttip.getTitle());
                        tipMap.put("type", blogposttip.getType());
                        tipMap.put("note", blogposttip.getNote());
                        tipMap.put("url", blogposttip.getUrl());
                        break;
                    case "Podcast":
                        PodcastTip podcasttip = (PodcastTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", podcasttip.getTitle());
                        tipMap.put("type", podcasttip.getType());
                        tipMap.put("note", podcasttip.getNote());
                        tipMap.put("author", podcasttip.getAuthor());
                        tipMap.put("description", podcasttip.getDescription());
                        tipMap.put("url", podcasttip.getUrl());
                        break;
                    default:
                        break;
                }
                modelTips.add(tipMap);
            }
        }
        return modelTips;
    }

    public List<HashMap<String, String>> retrieveAllTipsByType(String requested, int user_id) {
        if(requested.equals("All")){
            return retrieveAllTips(user_id);
        }
        List<Tip> tips = readingTipDao.findAll_(user_id);
        ArrayList<HashMap<String, String>> modelTips = new ArrayList<>();

        for (Tip tip : tips) {
            if (tip.getType().equals(requested)) {
                HashMap<String, String> tipMap = new HashMap<>();
                switch (tip.getType()) {
                    case "Book":
                        BookTip booktip = (BookTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", booktip.getTitle());
                        tipMap.put("type", booktip.getType());
                        tipMap.put("note", booktip.getNote());
                        tipMap.put("author", booktip.getAuthor());
                        tipMap.put("isbn", booktip.getIsbn());
                        tipMap.put("url", booktip.getUrl());
                        break;
                    case "Video":
                        VideoTip videotip = (VideoTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", videotip.getTitle());
                        tipMap.put("type", videotip.getType());
                        tipMap.put("note", videotip.getNote());
                        tipMap.put("url", videotip.getUrl());
                        break;
                    case "Blogpost":
                        BlogpostTip blogposttip = (BlogpostTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", blogposttip.getTitle());
                        tipMap.put("type", blogposttip.getType());
                        tipMap.put("note", blogposttip.getNote());
                        tipMap.put("url", blogposttip.getUrl());
                        break;
                    case "Podcast":
                        PodcastTip podcasttip = (PodcastTip) tip;
                        tipMap.put("id", String.valueOf(tip.getTipId()));
                        tipMap.put("title", podcasttip.getTitle());
                        tipMap.put("type", podcasttip.getType());
                        tipMap.put("note", podcasttip.getNote());
                        tipMap.put("author", podcasttip.getAuthor());
                        tipMap.put("description", podcasttip.getDescription());
                        tipMap.put("url", podcasttip.getUrl());
                        break;
                    default:
                        break;
                }
                modelTips.add(tipMap);
            }
        }
        return modelTips;
    }

    public void saveNewTip(Map<String, String> tipAttributes, int user_id) {
        Tip toSave = TipFactory.createTip(tipAttributes);
        readingTipDao.save(toSave, user_id);
    }

    public void deleteTipByID(int id) {
        int rowsDeleted = readingTipDao.deleteByID(id);
        System.out.println("Deleted " + rowsDeleted + " rows.");
    }

    public void deleteTipByTitle(String title) {
        int rowsDeleted = readingTipDao.deleteByTitle(title);
        System.out.println("Deleted " + rowsDeleted + " rows.");
    }

    public void updateTip(Tip tip) {
        int rowsDeleted = readingTipDao.updateTip(tip);
        System.out.println("Updated " + rowsDeleted + " rows.");
    }
    
    private void addTypeSpecificParameters(HashMap<String, String> tipMap, Tip tip) {
        if (tip.getType().equals("Book")) {
            BookTip book = (BookTip) tip;
            
            tipMap.put("author", book.getAuthor());
            tipMap.put("isbn", book.getIsbn());
            tipMap.put("url", book.getUrl());
        } else if (tip.getType().equals("Podcast")) {
            PodcastTip podc = (PodcastTip) tip;
            
            tipMap.put("author", podc.getAuthor());
            tipMap.put("description", podc.getDescription());
            tipMap.put("url", podc.getUrl());
        } else if (tip.getType().equals("Video")) {
            VideoTip vid = (VideoTip) tip;
            
            tipMap.put("url", vid.getUrl());
        } else {
            BlogpostTip blogp = (BlogpostTip) tip;
            
            tipMap.put("url", blogp.getUrl());
        }
    }
    
    public int validateUser(String username, String password) {
        String[] result = readingTipDao.findUser(username);
        if (result != null) {
            if (Authentication.verifyUser(password, result[1], result[0])) {
                System.out.println("Password correct");
                return Integer.parseInt(result[2]);
            }
        }
        System.out.println("Password incorrect");
        return -1;
    }

    public void registerUser(String username, String password) {
        String salt = Authentication.getSalt(128);
        String password_hash = Authentication.generatePassword(password, salt);
        readingTipDao.registerUser(username, password_hash, salt);
    }
}
