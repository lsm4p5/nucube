package uplus.nucube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uplus.nucube.domain.Member;
import uplus.nucube.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/list")
    public List<Member> getMemberList() {
        return memberRepository.findAll();
    }
}
