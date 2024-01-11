package jaeyong.Test2.Service;

import jaeyong.Test2.Repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.List;

@EnableCaching
@Configuration
public class Config {

    private final EntityManager em;

    @Autowired
    public Config(EntityManager em) {
        this.em = em;
    }

    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(List.of(new ConcurrentMapCache("exampleStore")));
        return simpleCacheManager;
    }

    @Bean
    public Service service(){
        return new Service(repository());
    }

    @Bean
    public SecurityService securityService(){
        return new SecurityService();
    }
    @Bean
    public Repository repository(){
        return new Repository(em);
    }
}
