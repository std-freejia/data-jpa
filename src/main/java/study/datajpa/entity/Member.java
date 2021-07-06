package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter // 실무에서는 가급적 @Setter 사용 자제.
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;

    /** JPA 엔티티는 반드시 기본 생성자가 필요 */
    protected Member(){
    }

    public Member(String username){
        this.username = username;
    }

    /** setter 사용을 피하기 위함 */
    public void changeUsername(String username){
        this.username = username;
    }
}
