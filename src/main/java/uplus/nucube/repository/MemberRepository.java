package uplus.nucube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uplus.nucube.domain.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByName(String name);

}
