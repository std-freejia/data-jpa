package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext EntityManager em;

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

    /** 데이터 JPA 페이징 : Page */
    @Test
    public void pagingPage(){
        // given : 데이터 5개 만들어서 저장
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        /** [주의] 스프링 데이터 JPA에서는 페이지 인덱스를 0부터 센다. */
        // 0페이지에 3개 가져오기. 소팅 조건(기준과 오름차순 내림차순)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // 컨텐츠 가져옴
        // 반환 타입이 Page라면, 스프링 데이터JPA가 count 쿼리를 알아서 실행한다.

        /** [Page를 DTO로 변환하기] map()사용 */
        Page<MemberDto> memberDtos = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        /** 데이터를 가져오는 것 보다, totalCount를 가져오는 것이 더 DB에 부담이 된다. */

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        // 검증
        assertThat(content.size()).isEqualTo(3); // 3개씩 가져오니까.
        assertThat(page.getTotalElements()).isEqualTo(5); // 데이터 총 개수
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 넘버
        assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지 개수
        assertThat(page.isFirst()).isTrue(); // 여기가 첫 페이지인지
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 존재하는지
    }

    /** 데이터 JPA 페이징 : Slice */
    @Test
    public void pagingSlice() {
        // given : 데이터 5개 만들어서 저장
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        /** 스프링 데이터 JPA에서는 페이지 인덱스를 0부터 센다. */
        // 0페이지에 3개 가져오기. 소팅 조건(기준과 오름차순 내림차순)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findMemberSliceByAge(age, pageRequest); // 컨텐츠 가져옴
        // 반환 타입이 Slice라면, total count 쿼리를 실행하지 않는다.

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); // 3개씩 가져오니까.
        //assertThat(page.getTotalElements()).isEqualTo(5); // 데이터 총 개수 -> /** Slice에 없는 기능*/
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 넘버
        //assertThat(page.getTotalPages()).isEqualTo(2); // 총 페이지 개수 -> /** Slice에 없는 기능*/
        assertThat(page.isFirst()).isTrue(); // 여기가 첫 페이지인지
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 존재하는지

        /**
         * 한 페이지에 요청을 3개 하더라도, +1씩 더 가져오는 Slice 방식
         * -> '더보기'기능 유무 보여주는 것이 편리하다.
         *
         * select member0_.member_id as member_i1_0_, member0_.age as age2_0_, ...
         * from member member0_ where member0_.age=10
         * order by member0_.username desc limit 4;
         * */
    }

    @Test
    public void bulkAgeAdd(){ /** 벌크성 수정 쿼리 */
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 21));
        memberRepository.save(new Member("member4", 19));
        memberRepository.save(new Member("member5", 25));

        /** 같은것 동시 수정: shift + F6 */

        // when
        int resultCount = memberRepository.bulkAgePlus(20);// 20살 이상인 사람들 모두 +1
        em.flush(); /** DB에 반영되지 않은 SQL들이 있다면 수행한다. */
        em.clear(); /** 영속성 컨텍스트를 초기화한다. */

        /**
         * 벌크 연산은, DB에 수정쿼리를 바로 수행하기 때문에 DB에는 수정이 되지만, 영속성 컨텍스트에 변경사항이 반영되지 않는다.
         * 출력 해보면 영속성 컨텍스트에 member5의 나이는 25로 출력된다. DB에 직접 조회하면 26살임.
         * 따라서 벌크 연산 직후에 영속성 컨텍스트를 초기화해야 한다. em.flush(), em.clear().
         */
        List<Member> members = memberRepository.findByUsername("member5");
        Member member5 = members.get(0);
        System.out.println("member5.getAge() = " + member5.getAge());


        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        /**  @EntityGraph
         * : 내부적으로 패치조인 쓰는 애노테이션. fetch join 을 이해해야 JPA를 실무에서 쓸 수 있다. */

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush(); // 변경 사항 반영할 sql 실행
        em.clear(); // 영속성 컨텍스트 초기화

        // when
        // List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        // List<Member> members = memberRepository.findNamedEntityGraphByUsername("member1");


        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // 가짜객체(프록시객체)
            System.out.println("member.team = " + member.getTeam().getName()); // 패치조인. 이 때 Team DB 조회.
        }
        /**
         * [ N+1 문제 ]
         * Member 를 조회하려고 했는데 Team 을 조회할 쿼리도 추가 실행.
         * 네트웍 타는 성능 소모
         * -> fetch join 으로 해결 : 연관관계 물려있는 엔티티를 한번에 조인하여 select 절에 데이터 넣어서 조회한다.
         *
         * [중요] Fetch Join 으로 N+1 문제를 해결 : Member 조회 시 연관된 Team도 같이 끌고와 조회함
         * @Query("select m from Member m left join fetch m.team")
         * List<Member> findMemberFetchJoin ();
         */
    }

    @Test
    public void queryHint(){ /**  QueryHints JPA Hint & Lock */
        // given
        Member member1 = new Member("member1", 10);
        Member savedMember = memberRepository.save(member1);
        em.flush(); // JPA의 영속성 컨텍스트를 DB에 반영하는 쿼리 실행
        em.clear(); // 영속성 컨텍스트 초기화. 비운다.

        //when
        // 실무에서는 get() 이런거 바로 쓰면 안됨.
        //Member findMember = memberRepository.findById(savedMember.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        /** 이름 변경 후, "flush()"하면 변경감지(더티체킹)가 동작해서 update 쿼리를 실행함.
         * 변경감지라는 것이 원본 데이터와 변경후 데이터 이렇게 2개를 관리해야 한다. 메모리를 소모하는 작업이다.
         * 변경이 아니라 단순히 조회만 하는 경우에 하이버네이트가 최적화 할 수 있도록 Hint기능을 제공.
         *
         * @QueryHints 애노테이션 옵션을 readOnly 로 해두면 스냅샷을 안만든다.
         * 읽기 전용. 변경감지 체크도 안한다. */
        em.flush();
    }

    @Test
    public void queryLock(){
         /**  JPA Lock
         애노테이션으로 편리하게 락을 걸 수 있다. 좀 깊은 내용.
         DB 방언에 따라 동작방식이 조금다름. 정확성이 중요한 경우에 적합.
         실시간 트래픽이 많은 서비스인 경우 되도록 락은 피하는게 좋음. */

        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom(){
        /** querydsl, 복잡한 쿼리, DB에 직접 붙어야 한다거나.. 할 때.
         * 인터페이스로 해결이 안되는 경우가 실무에서 은근 있다. 이럴 때 사용자 정의 레포지토리를 쓴다.
         *
         * [사용자 정의 레포지토리 '구현체' 명명 규칙 중요한거.]
         * MemberRepositoryImpl
         *
         * 1. 사용자 정의 레포지토리 '구현체' 마지막에 'Impl' 을 붙여줘야한다.
         * 2. 상속받을 인터페이스의 이름을 포함해서 '구현체' 이름을 지어야 한다.
         * public interface MemberRepository extends MemberRepositoryCustom
         * public class MemberRepositoryImpl implements MemberRepositoryCustom
         *
         * 사용자 정의 레포지토리 인터페이스의 이름은 상관없다.
         * */
        List<Member> result = memberRepository.findMemberCustom();
    }
}