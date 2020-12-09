package miniprojekti.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import miniprojekti.database.Database;
import miniprojekti.domain.BlogpostTip;
import miniprojekti.domain.BookTip;
import miniprojekti.domain.PodcastTip;
import miniprojekti.domain.ReadingTip;
import miniprojekti.domain.Tip;
import miniprojekti.domain.VideoTip;

public class ReadingTipDao implements Dao {
    
    private Database database;
    
    public ReadingTipDao(Database database) {
        this.database = database;
    }
    
    @Override
    public List<ReadingTip> findAll() {
        List<ReadingTip> readingTips = new ArrayList<>();
        
        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, author, title, url FROM readingtip").executeQuery()) {
            
            while (result.next()) {
                readingTips.add(new ReadingTip(
                        result.getInt("id"),
                        result.getString("author"),
                        result.getString("title"),
                        result.getString("url")
                    )
                );
            }
        } catch (Exception e) {
            
        }
        return readingTips;
    }
    
    public List<Tip> findAll_(int user_id) {
        List<Tip> tips = new ArrayList<>();
        try (Connection conn = database.getConnection()) {
            PreparedStatement tmp = conn.prepareStatement("SELECT id, title, type, note FROM tip WHERE account_id = ?");
            tmp.setInt(1, user_id);
            
            ResultSet result = tmp.executeQuery();
            while (result.next()) {
                PreparedStatement stmt;
                ResultSet rs;
                switch (result.getString("type")) {
                    case "Book":
                        stmt  = conn.prepareStatement("SELECT id, author, isbn, url FROM book WHERE tip_id = ?");
                        stmt.setInt(1, result.getInt("id"));
                        rs = stmt.executeQuery();
                        rs.next();
                        tips.add(new BookTip(
                                result.getInt("id"), 
                                result.getString("title"), 
                                result.getString("note"),
                                rs.getInt("id"),
                                rs.getString("author"),
                                rs.getString("isbn"),
                                rs.getString("url")
                        ));
                        break;
                    case "Video":
                        stmt  = conn.prepareStatement("SELECT id, url FROM video WHERE tip_id = ?");
                        stmt.setInt(1, result.getInt("id"));
                        rs = stmt.executeQuery();
                        rs.next();
                        tips.add(new VideoTip(
                                result.getInt("id"), 
                                result.getString("title"), 
                                result.getString("note"),
                                rs.getInt("id"),
                                rs.getString("url")
                        ));
                        break;
                    case "Blogpost":
                        stmt  = conn.prepareStatement("SELECT id, url FROM blogpost WHERE tip_id = ?");
                        stmt.setInt(1, result.getInt("id"));
                        rs = stmt.executeQuery();
                        rs.next();
                        tips.add(new BlogpostTip(
                                result.getInt("id"), 
                                result.getString("title"), 
                                result.getString("note"),
                                rs.getInt("id"),
                                rs.getString("url")
                        ));
                        break;
                    case "Podcast":
                        stmt  = conn.prepareStatement("SELECT id, author, description, url FROM podcast WHERE tip_id = ?");
                        stmt.setInt(1, result.getInt("id"));
                        rs = stmt.executeQuery();
                        rs.next();
                        tips.add(new PodcastTip(
                                result.getInt("id"), 
                                result.getString("title"), 
                                result.getString("note"),
                                rs.getInt("id"),
                                rs.getString("author"),
                                rs.getString("description"),
                                rs.getString("url")
                        ));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return tips;
    }
    
    public void save(Tip tip, int user_id) {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tip (account_id, title, type, note) "
                    + "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user_id);
            stmt.setString(2, tip.getTitle());
            stmt.setString(3, tip.getType());
            stmt.setString(4, tip.getNote());
            stmt.executeUpdate();
            
            int tipId = 0;
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tipId = (int)generatedKeys.getLong(1);
                }
                else {
                    return;
                }
            }
            
            switch (tip.getType()) {
                case "Book":
                    BookTip bookTip = (BookTip) tip;
                    stmt = conn.prepareStatement("INSERT INTO book (tip_id, author, isbn, url) "
                            + "VALUES (?, ?, ?, ?)");
                    stmt.setInt(1, tipId);
                    stmt.setString(2, bookTip.getAuthor());
                    stmt.setString(3, bookTip.getIsbn());
                    stmt.setString(4, bookTip.getUrl());
                    stmt.executeUpdate();
                    break;
                case "Video":
                    VideoTip videoTip = (VideoTip) tip;
                    stmt = conn.prepareStatement("INSERT INTO video (tip_id, url) "
                            + "VALUES (?, ?)");
                    stmt.setInt(1, tipId);
                    stmt.setString(2, videoTip.getUrl());
                    stmt.executeUpdate();
                    break;
                case "Blogpost":
                    BlogpostTip blogpostTip = (BlogpostTip) tip;
                    stmt = conn.prepareStatement("INSERT INTO blogpost (tip_id, url) "
                            + "VALUES (?, ?)");
                    stmt.setInt(1, tipId);
                    stmt.setString(2, blogpostTip.getUrl());
                    stmt.executeUpdate();
                    break;
                case "Podcast":
                    PodcastTip podcastTip = (PodcastTip) tip;
                    stmt = conn.prepareStatement("INSERT INTO podcast (tip_id, author, description, url) "
                            + "VALUES (?, ?, ?, ?)");
                    stmt.setInt(1, tipId);
                    stmt.setString(2, podcastTip.getAuthor());
                    stmt.setString(3, podcastTip.getDescription());
                    stmt.setString(4, podcastTip.getUrl());
                    stmt.executeUpdate();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void save(String author, String title, String url) {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO readingtip (author, title, url) "
                    + "VALUES (?, ?, ?)");
            stmt.setString(1, author);
            stmt.setString(2, title);
            stmt.setString(3, url);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Deletes the tip with the ID given as parameter if found.
     * @param id ID to look for and delete from the database.
     * @return Number of rows deleted (-1 if an error happens)
     */
    @Override
    public int deleteByID(int id) {
        int rowsDeleted = 0;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tip WHERE id=?");
            stmt.setInt(1, id);
            rowsDeleted = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
        return rowsDeleted;
    }
    
    /**
     * Deletes the tip with the title given as parameter if found.
     * @param title Title to look for and delete from the database.
     * @return Number of rows deleted (-1 if an error happens)
     */
    @Override
    public int deleteByTitle(String title) {
        int rowsDeleted = 0;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tip WHERE title=?");
            stmt.setString(1, title);
            rowsDeleted = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
        return rowsDeleted;
    }
    
    @Override
    public int deleteTip(Tip tip) {
        int rowsDeleted = 0;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tip WHERE id=?");
            stmt.setInt(1, tip.getTipId());
            rowsDeleted = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
        return rowsDeleted;
    }
    
    /**
     * Updates the given tip with its set values in the database by ID
     * @param tip The tip with the id that is being updated by its values
     * @return Number of rows updated (Should be 1 as IDs are unique, -1 if an error happens)
     */
    @Override
    public int updateTip(Tip tip) {
        int rowsUpdated = 0;
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE tip SET title=?, type=?, note=? WHERE id=?");
            stmt.setString(1, tip.getTitle());
            stmt.setString(2, tip.getType());
            stmt.setString(3, tip.getNote());
            stmt.setInt(4, tip.getTipId());
            rowsUpdated += stmt.executeUpdate();
            
            switch (tip.getType()) {
                case "Book":
                    BookTip bookTip = (BookTip) tip;
                    stmt = conn.prepareStatement("UPDATE book SET author=?, isbn=?, url=? WHERE tip_id=?");
                    stmt.setString(1, bookTip.getAuthor());
                    stmt.setString(2, bookTip.getIsbn());
                    stmt.setString(3, bookTip.getUrl());
                    stmt.setInt(4, bookTip.getTipId());
                    rowsUpdated += stmt.executeUpdate();
                    break;
                case "Video":
                    VideoTip videoTip = (VideoTip) tip;
                    stmt = conn.prepareStatement("UPDATE video SET url=? WHERE tip_id=?");
                    stmt.setString(1, videoTip.getUrl());
                    stmt.setInt(2, videoTip.getTipId());
                    rowsUpdated += stmt.executeUpdate();
                    break;
                case "Blogpost":
                    BlogpostTip blogpostTip = (BlogpostTip) tip;
                    stmt = conn.prepareStatement("UPDATE blogpost SET url=? WHERE tip_id=?");
                    stmt.setString(1, blogpostTip.getUrl());
                    stmt.setInt(2, blogpostTip.getTipId());
                    rowsUpdated += stmt.executeUpdate();
                    break;
                case "Podcast":
                    PodcastTip podcastTip = (PodcastTip) tip;
                    stmt = conn.prepareStatement("UPDATE podcast SET author=?, description=?, url=? WHERE tip_id=?");
                    stmt.setString(1, podcastTip.getAuthor());
                    stmt.setString(2, podcastTip.getDescription());
                    stmt.setString(3, podcastTip.getUrl());
                    stmt.setInt(4, podcastTip.getTipId());
                    rowsUpdated += stmt.executeUpdate();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        
        return rowsUpdated;
    }
    
    
    @Override
    public String[] findUser(String username) {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, password_hash, salt FROM account WHERE name=?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String[] returnValue = new String[3];
                returnValue[0] = rs.getString("password_hash");
                returnValue[1] = rs.getString("salt");
                returnValue[2] = ""+rs.getInt("id");
                return returnValue;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    public void registerUser(String username, String password_hash, String salt) {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO account (name, password_hash, salt) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password_hash);
            stmt.setString(3, salt);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
}
