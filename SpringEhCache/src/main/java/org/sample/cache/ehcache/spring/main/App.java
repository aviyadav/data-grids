package org.sample.cache.ehcache.spring.main;

import org.sample.cache.ehcache.spring.data.movie.MovieDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MovieDAO obj = (MovieDAO) context.getBean("movieDAO");

        log.debug("Result : {}", obj.findByDirector("dummy"));
        log.debug("Result : {}", obj.findByDirector("dummy"));
        log.debug("Result : {}", obj.findByDirector("dummy"));


        ((ConfigurableApplicationContext) context).close();
    }
}
