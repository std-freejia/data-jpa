package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository; // 인터페이스
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        /** null 처리를 감안하여 Optional 사용이 더 낫다. */
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

        /** 콘솔에 JPA쿼리에 파라미터 들어가는 것을 확인 !
         * p6spy-spring-boot-starter:1.5.7  를 build.gradle에 추가
         * 아래 처럼 출력된다.
         * insert into member (username, id) values (?, ?)
         * insert into member (username, id) values ('memberA', 1); <-- 콘솔에 출력
         */
    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void namedQuery(){ //NamedQuery
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){ // @Query
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testfindUsernameList(){ // @Query
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void testMemberDto(){ // @Query
        Team t1 = new Team("teamA"); //team
        teamRepository.save(t1);

        Member m1 = new Member("AAA", 10); // member
        m1.setTeam(t1);
        memberRepository.save(m1);

        List<MemberDto> findMemberDto = memberRepository.findMemberDto();
        for (MemberDto memberDto : findMemberDto) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames(){ // @Query
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> resultList = memberRepository.findByName(Arrays.asList("AAA", "BBB"));
        for (Member member : resultList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void testReturnType(){ // 반환타입을 유연하게 받을 수 있다.
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        /** 중요!
         * 조회 결과가 없다면, null 이 아니라, 빈 컬렉션을 반환한다!
         * */
        List<Member> list = memberRepository.findListByUsername("AAA");
        System.out.println("list.get(0).getUsername() = " + list.get(0).getUsername());
        System.out.println("list.size() = " + list.size());

        /** 중요!
         *  컬렉션 으로 받지 않을 때, 단건 조회 시 NULL 반환한다.
         * */
        Member result = memberRepository.findMemberByUsername("AAA");
        System.out.println("result = " + result);

        /** 좋은 방법!
         * 조회할 때 데이터가 있을지 없을지 모르면, Optional 로 감싸는 것이 가장 안전하다. */
        Optional<Member> optional = memberRepository.findOptionalByUsername("BBB");
        System.out.println("optional = " + optional);
    }

    @Test
    public void testReturnTypeError(){ // 반환타입을 유연하게 받을 수 있다.
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        /** AAA가 2명이라서, 에러 발생
         * NotUniqueResultException(JPA error) -> IncorrectResultSizeException  (Spring error로 감싸서 출력)*/
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");
        System.out.println("optional = " + optional);
    }


}
