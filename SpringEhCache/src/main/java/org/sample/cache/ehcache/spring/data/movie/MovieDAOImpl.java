package org.sample.cache.ehcache.spring.data.movie;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository("movieDAO")
public class MovieDAOImpl implements MovieDAO {

    @Override
    @Cacheable(value = "movieFindCache", key = "#name")
    public Movie findByDirector(String name) {
        slowQuery(2000L);
        System.out.println("findByDirector is running...");
        return new Movie(1, "Forrest Gump", "Robert Zemeckis");
    }
    
    private void slowQuery(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(MovieDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
