package uplus.nucube.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Member;
import uplus.nucube.service.MemberService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberContrloller {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute( "memberForm", new MemberForm() );
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }
        /** 글로벌인경우는 아래와 같이 bindingResult를 만들어주면 된다.
         if (resultPrice < 10000) {
         bindingResult.reject( "totalPriceMin",new Object[]{10000, resultPrice},"Total 금액 이상" );
         return "validation/v4/addForm";
         }
         */
        Address address = new Address( memberForm.getCity(),
                memberForm.getStreet(),
                memberForm.getZipcode() );
        Member member = new Member( memberForm.getName(), address );
        memberService.join( member );

        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute( "members", members );

        return "members/memberList";
    }

    @GetMapping("/members/member")
    @ResponseBody
    public Member memberSearch(Member member,String name, Integer a,BindingResult bindingResult) {
       log.info( "member = " + member.getName() );
        member.setName( "babo" );
       return member;
    }
}
