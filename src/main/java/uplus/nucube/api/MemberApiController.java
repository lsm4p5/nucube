package uplus.nucube.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uplus.nucube.common.utils.HttpPrint;
import uplus.nucube.domain.Member;
import uplus.nucube.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map( m -> new MemberDto( m.getName(),m.getId()) )
                .collect( Collectors.toList() );

        return new Result( collect.size(),String.valueOf( HttpStatus.OK ),"정상", collect );

    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
        private Long id;
    }
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private String rsp_code;
        private String rsp_msg;
        private T data;

    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody Member member,
                                             BindingResult bindingResult,
                                             HttpServletRequest request) {

        HttpPrint.printHeaders( request );
        HttpPrint.printHeaderUtils( request );
        HttpPrint.printStartLine( request );
        if (bindingResult.hasErrors()) {
            log.info( "error = {}", bindingResult );
        }
        Long id = memberService.join( member );
        return new CreateMemberResponse( id );

    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request){

        Member member = new Member();
        member.setName( request.getName() );
        Long id = memberService.join( member );
        return new CreateMemberResponse( id );
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update( id, request.getName() );
        Member findMember = memberService.findOne( id );
        return new UpdateMemberResponse(findMember.getId(),findMember.getName());
    }

    @Data
    static class UpdateMemberResponse{
        private Long id;
        private String name;

        public UpdateMemberResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    @Data
    static class UpdateMemberRequest{
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;

    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
