package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest // JUnit5 부터는 @RunWith 애노테이션 없어도 junit 동작함
@Transactional // JPA의 모든 데이터변경으니 트랜젝션 안에서 이루어진다. @Test가 끝나면, 롤백한다.
@Rollback(value = false) // @Test가 끝나도 롤백 안한다.
class MemberJpaRepositoryTest {

    /** MemberJpaRepository Test : 순수 jpa 적용 */
    @Autowired MemberJpaRepository memberJpaRepository;

    // @Autowired MemberRepository memberRepository; /** 스프링 데이터 jpa 를 적용해보자! */

    @Test
    public void testMember(){
        Member member = new Member("name1");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // findMember1.setUsername("hello?"); // update


        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        // 삭제 후에 0개 인지 검증
        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    /** 페이징 */
    @Test
    public void paging(){
        // given : 데이터 5개 만들어서 저장
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 1;
        int limit = 3;

        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit); // 컨텐츠 가져옴
        long totalCount = memberJpaRepository.totalCount(age); // 데이터 총 개수 가져옴

        // 페이지 계산 공식 적용..
        // totalPage, 마지막페이지, 최초 페이지 확인 로직 찾아서 구현하기.

        // then
        assertThat(members.size()).isEqualTo(3); // 0번째 데이터에서 3개를 가져옴
        assertThat(totalCount).isEqualTo(5); // 데이터 총 개수
    }

    @Test
    public void bulkUpdateTest(){  /** 벌크성 수정 쿼리 순수 JPA */
        // given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 20));
        memberJpaRepository.save(new Member("member3", 21));
        memberJpaRepository.save(new Member("member4", 19));
        memberJpaRepository.save(new Member("member5", 25));

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);// 20살 이상인 사람들 모두 +1

        // then
        assertThat(resultCount).isEqualTo(3);
    }
}