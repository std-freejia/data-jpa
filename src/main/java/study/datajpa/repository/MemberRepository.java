package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

/** 인터페이스 JpaRepository를 상속받는다 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /** 쿼리 메소드 */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    // ERROR : No property greaterThen found for type int!   메서드이름에 Than을 Then으로 써서 메소드가 만들어지지 않는 에러.

}
