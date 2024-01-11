package jaeyong.Test2.Controller;

import jaeyong.Test2.Domain.Content;
import jaeyong.Test2.Domain.User;
import jaeyong.Test2.Service.CacheData;
import jaeyong.Test2.Service.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@CrossOrigin("*")
@RestController
@Slf4j
public class TestController {

    private final Service service;

    @Autowired
    public TestController(Service service) {
        this.service = service;
    }
    //아이디 중복확인
    @RequestMapping("/ha")
    public String test(@RequestParam("User_ID") String userId) {
        String sol = service.check_ID(userId);
        System.out.println(userId);
        return sol;
    }


    //컨텐츠 추가
    @RequestMapping("/CreateContent")
    public String create(@RequestParam("Content") String con) {
        Content content = new Content();
        content.setContent(con);
        return service.savecontent(content);

    }

    //유저추가
    @RequestMapping("/CreateUser")
    public String createtest(@RequestParam("ID") String id) {
        User user = new User();
        user.setID(id);
        return service.saveUser(user);
    }

    //User 모두가져오기
    @RequestMapping("/findall")
    public String list() {

        List<Content> result = service.Find_Content();
        StringBuilder sb = new StringBuilder();
        for (Content content : result) {
            sb.append(content.getId() + " : " + content.getContent() + "<br>");
        }
        return sb.toString();
    }

    //content삭제
    @RequestMapping("/DeleteContent")
    public String Delete(@RequestParam("ID") Long id) {

        return service.delete(id);

    }

    @RequestMapping("/upCache")
    public String cachetest(@RequestParam("ID") String id) {
        service.updateCacheData(id, "test");
        return id;
    }

    @RequestMapping("/Cache")
    public String cacheserch(@RequestParam("ID") String id) {
        CacheData cacheData = service.getCacheData(id);
        return cacheData.getValue() + "<br>" + cacheData.getExpirationDate();
    }

    @RequestMapping("/delCache")
    public String delcache(@RequestParam("ID") String id) {
        service.expireCacheData(id);
        return "삭제 성공";
    }


    @RequestMapping("/login")
    public String login(@RequestParam("id") String id, HttpServletRequest request) {

        if (service.check_ID(id).equals("200")) {
            return "no id";
        }

        HttpSession session = request.getSession();

        User user = service.getUser(id);
        session.setAttribute("login", user);
        return "login";
    }

    @RequestMapping("/session")
    public String getSession(@SessionAttribute(name = "login", required = false) User login) {
        return login == null ? "not login" : login.getID() + "  " + login.getUser_Name();
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return "logout";
        }
        return "not login";
    }

}
