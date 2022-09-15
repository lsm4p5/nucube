package uplus.nucube.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Member;
import uplus.nucube.repository.MemberJpaRepository;
import uplus.nucube.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member( "kim",
                new Address( "seoul1", "jonno", "zipcode1" ) );
        //when
        Long saveId = memberService.join( member );
        //then
        assertThat( saveId ).isEqualTo( member.getId() );
        org.junit.jupiter.api.Assertions.assertEquals( member, memberRepository.findById( member.getId() ).get() );
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member( "kim1",
                new Address( "seoul", "jonno1", "zipcode2" ) );

        Member member2 = new Member( "kim1",
                new Address( "seoul22", "jonno122", "zipcode2222" ) );


        //when
        memberService.join( member1 );
        //memberService.join( member2 );

        Assertions.assertThatThrownBy( () -> memberService.join( member2 ) )
                .isInstanceOf( IllegalStateException.class );

        //then

     //   fail( "에외가 발생해야 한다" );
    }

}