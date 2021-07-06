package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository; // 인터페이스

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
}
