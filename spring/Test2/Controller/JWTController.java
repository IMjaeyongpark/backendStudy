package jaeyong.Test2.Controller;

import jaeyong.Test2.Service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/Security")
@Slf4j
public class JWTController {

    private final SecurityService securityService;

    @Autowired
    public JWTController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @RequestMapping("/Token")
    public Map<String,Object> createToken(@RequestParam("subject")String subject) {
        String token = securityService.createToken(subject,(1*1000*60));
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("result",token);
        return map;
    }

    @RequestMapping("/Subject")
    public Map<String,Object> getToken(@RequestParam("token")String token) {
        String subject = securityService.getSubject(token);
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("result",subject);
        return map;
    }
}
