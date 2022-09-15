package uplus.nucube.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Slf4j
public class SimpleController {

    @GetMapping("/hello2")
    @ResponseBody
    public String indexReturn() {
        return "ok";
    }
}
