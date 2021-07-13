package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

/** 인터페이스 JpaRepository를 상속받는다 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /** 쿼리 메소드 */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    // ERROR : No property greaterThen found for type int!   메서드이름에 Than을 Then으로 써서 메소드가 만들어지지 않는 에러.

    /** NamedQuery  */
    //@Query(name="Member.findByUsername")  /** 이 애노테이션 지워도 동작함! */
    List<Member> findByUsername(@Param("username")String username); // JPQL의 파라미터를 넣을 때 @Param를 명시한다.

    /** @Query : 긴 JPQL 작성 가능하면서, 함수명을 짧게 작성 가능.*/
    @Query("select m from Member m  where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age")int age);

}
