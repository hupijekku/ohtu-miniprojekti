package miniprojekti.data_access;

import java.util.List;
import miniprojekti.domain.ReadingTip;
import miniprojekti.domain.Tip;

/**
 *
 * @author Tatu
 */
public interface Dao {

    List<ReadingTip> findAll();

    void save(String author, String title, String url);
    
    int deleteTip(Tip tip);
    
    int deleteByID(int id);
    
    int deleteByTitle(String title);
    
    int updateTip(Tip tip);
    
    String[] findUser(String username);
    
}
