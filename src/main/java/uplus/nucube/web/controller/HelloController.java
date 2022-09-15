package uplus.nucube.web.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.query.JpaQueryCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uplus.nucube.domain.Member;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute( "data", "hello!!!!" );
        return "hello";
    }
}
