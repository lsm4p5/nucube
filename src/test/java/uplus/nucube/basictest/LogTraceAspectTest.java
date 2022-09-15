package uplus.nucube.basictest;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.domain.Address;
import uplus.nucube.domain.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@Transactional
@Import(LogTraceAspectTest.TESTAOP.class)
class LogTraceAspectTest {

    @Autowired
    TESTAOP testaop;

    @Test
    @Commit
    public void logAsprceTest() {


        Member member = new Member("member1",new Address("city1", "street1","zipcode"));

        System.out.println( "memberA = " + testaop );
       // Member members = testaop.members( member, 12, "babo", 15L, new Address( "city2", "street2", "zipcode2" ) );

   //     testaop.members2( member, 121, "BBBB", 300, null );

       List<Member> list = testaop.createLit();
//
        testaop.members3(list,12, "babo", 15L, new Address( "city2", "street2", "zipcode2" ));

        Member member1 = new Member( "member2", null );
        Member member2 = new Member( "member3", null );
        Member member3 = new Member("member4", new Address("서울","종로가","123405"));

        List<Member> members = new ArrayList<>();
        members.add( member1 );
        members.add( member2 );
        members.add( member3 );
        testaop.members3( members , 0,null,0,null);

    }

    @Test
    void mapTest() {
        Map<Object, String> mapTest = new HashMap<>();

        String a = null;
        Integer b = null;

        mapTest.put( a, "a" );
        mapTest.put( b, "b" );

        for (Object o : mapTest.keySet()) {
            log.info("s ={},", o);
        }
        log.info( "mapTest.count = {} ", mapTest.size() );

    }
    @Component
    @NoArgsConstructor
    static class TESTAOP {

        int aa;

        public Member members(Member member,int a, String b, long c, Address address){
            member.setName( b );
            member.setAddress( address);
            member.setId( c );
            return member;
        }

        public List<Member> members2(Member member,int a, String b, long c, Address address){
            Member member1 = new Member( "member2", null );
            Member member2 = new Member( "member3", null );
            Member member3 = new Member("member4", new Address("서울","종로가","123405"));

            List<Member> members = new ArrayList<>();
            members.add( member1 );
            members.add( member2 );
            members.add( member3 );
            return members;
        }

        public List<Member> members3(List<Member> members2 ,int a, String b, long c, Address address){
            Member member1 = new Member( "member2", null );
            Member member2 = new Member( "member3", null );
            Member member3 = new Member("member4", new Address("서울","종로가","123405"));

            List<Member> members = new ArrayList<>();
            members.add( member1 );
            members.add( member2 );
            members.add( member3 );
            return members;
        }

        public List<Member> createLit(){
            Member member1 = new Member( "member2", null );
            Member member2 = new Member( "member3", null );
            Member member3 = new Member("member4", new Address("서울","종로가","123405"));

            List<Member> members = new ArrayList<>();
            members.add( member1 );
            members.add( member2 );
            members.add( member3 );
            return members;
        }

    }

}