package jaeyong.Test2.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String home() {
        log.info("This is home!");
        return "hi";
    }

    @GetMapping("/ee")
    public String aa(){
        return "ee";
    }

}
