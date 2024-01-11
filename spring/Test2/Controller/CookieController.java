package jaeyong.Test2.Controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CookieController {
    @RequestMapping("/cookietest")
    //쿠키에서 test를 찾아서 value 받아오기
    public String cookietest(@CookieValue(value="test", required = false, defaultValue = "업소용 쿠키") String cookie) {
        return cookie;
    }

    @RequestMapping("/addcookie")
    public String addcookie(HttpServletResponse response){

        Cookie cookie = new Cookie("test", "내가만든쿠키~");
        //쿠키의 유효기간 설정
        cookie.setMaxAge(10 * 60);
        //SSL 통신채널 연결 시에만 쿠키를 전송하도록 설정
        cookie.setSecure(true);
        //자바 스크립트에서 쿠키값을 읽어가지 못하도록 설정
        cookie.setHttpOnly(true);
        //쿠키 추가
        response.addCookie(cookie);

        return cookie.getValue();
    }
}
