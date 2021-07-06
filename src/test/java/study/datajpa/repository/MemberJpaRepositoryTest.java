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

    // @Autowired MemberJpaRepository memberJpaRepository; /** 순수 jpa */

    @Autowired MemberRepository memberRepository; /** 스프링 데이터 jpa 를 적용해보자! */

    @Test
    public void testMember(){
        Member member = new Member("name1");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // findMember1.setUsername("hello?"); // update


        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 삭제 후에 0개 인지 검증
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }
}