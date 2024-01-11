package jaeyong.Test2.Repository;

import jaeyong.Test2.Domain.Content;
import jaeyong.Test2.Domain.User;

import javax.persistence.EntityManager;
import java.util.List;

public class Repository {

    public Repository(EntityManager em) {
        this.em = em;
    }

    private final EntityManager em;

    public User login(String userid){
        User user = em.find(User.class, userid);
        return user;
    }

    public List Find_User() {

        List<User> result = em.createQuery("select m from User m", User.class)
                .getResultList();
        return result;
    }

    public List Find_Content() {

        List<Content> result = em.createQuery("select m from Content m", Content.class)
                .getResultList();
        return result;
    }

    public String savecontent(Content content){
        try {
            em.persist(content);
            return  "success";
        }catch(Exception e){
            return "error";
        }
    }


    public String delete(Long id){
        try {
            em.remove(em.find(Content.class, id));
            return id+" 삭제 성공";
        }catch(Exception e){
            return "실패";
        }
    }

    public String saveUser(User user) {
        try {
            em.persist(user);
            return  "success";
        }catch(Exception e){
            return "error";
        }
    }
}
