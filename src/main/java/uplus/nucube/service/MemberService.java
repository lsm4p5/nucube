package uplus.nucube.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.Member;
import uplus.nucube.repository.MemberRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     *  회원 가입
     */
    @Transactional
    public Long join(Member member) {
        ValidateDuplicateMember( member );
        memberRepository.save( member );
        return member.getId();
    }

    private void ValidateDuplicateMember(Member member) {
        // Exception DB unique 제약 조건을 걸어주면 동시 Thread 문제는 해결된다.
        List<Member> findMembers = memberRepository.findByName( member.getName() );
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException( "이미 존재하는 회원입니다");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById( memberId ).orElse(null);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById( id ).orElse( null );
        if(member != null){
            member.setName( name );
            return;
        }
        log.info( "member not found member={}", member );

    }
}
