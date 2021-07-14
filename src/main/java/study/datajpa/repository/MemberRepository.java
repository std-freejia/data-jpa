package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    /** 값 조회 */
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    /** DTO 조회 */
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t ")
    List<MemberDto> findMemberDto();

    /** 파라미터 바인딩 : 이름 기반으로 쓸 것! */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByName(@Param("names") Collection<String> names);

    /** 반환 타입 */
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

    /** 페이징 : Page Pageable 인터페이스를 넘기는데, PageRequest 구현체를 주로 씀. */
    @Query(value = "select m from Member m left join m.team t",
            countQuery="select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable); // Pageable : 현재 페이지

    /** 자동으로 count  쿼리를 실행하는 Page의 경우,
     * count 쿼리 실행 시, 종종 불필요한 join 등과 같은 비효율을 줄여야 할 경우가 있다.
     * (쿼리가 복잡할 경우, count 쿼리에 join이 들어가지 않도록 쿼리를 분리하면 좋다.)
     * */

    /** 페이징 : Slice  */
    Slice<Member> findMemberSliceByAge(int age, Pageable pageable); // Pageable : 현재 페이지

    /** 벌크성 수정 쿼리 : @Modifying 애노테이션 붙여야 수정 실행됨을 유의.  */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}
