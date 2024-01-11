package jaeyong.Test2.Service;

import jaeyong.Test2.Domain.Content;
import jaeyong.Test2.Domain.User;
import jaeyong.Test2.Repository.Repository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;



@Transactional
public class Service {

    private static final CacheData EMPTY_DATA = new CacheData();
    private final Repository repository;

    public Service(Repository repository) {
        this.repository = repository;
    }


    public User getUser(String userid){
        User user = repository.login(userid);
        return user;
    }

    public String check_ID(String id) {
        List<User> users = repository.Find_User();
        for(User user : users) {
            if(user.getID().equals(id)){
                return "400";
            }
        }
        return "200";
    }

    public String savecontent(Content content){
        return repository.savecontent(content);
    }

    public String saveUser(User user){
        user.setUser_Password("1234");
        user.setUser_Name("test");
        return repository.saveUser(user);

    }

    public List Find_Content(){
        return repository.Find_Content();
    }

    public String delete(Long id) {
        return repository.delete(id);
    }

    //캐시 가져오기
    @Cacheable(cacheNames = "exampleStore",key = "#key")
    public CacheData getCacheData(final String key){
        return EMPTY_DATA;
    }

    //캐시 삽입
    @CachePut(cacheNames = "exampleStore",key = "#key")
    public CacheData updateCacheData(final String key, final String value){
        CacheData cache = new CacheData();
        cache.setValue(value);
        cache.setExpirationDate(LocalDateTime.now().plusDays(1));
        return cache;
    }

    //캐시 삭제
    @CacheEvict(cacheNames = "exampleStore",key = "#key")
    public boolean expireCacheData(final String key){
        return true;
    }

}

