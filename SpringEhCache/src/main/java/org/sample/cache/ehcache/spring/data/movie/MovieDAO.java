package org.sample.cache.ehcache.spring.data.movie;

public interface MovieDAO {
    Movie findByDirector(String name);
}
