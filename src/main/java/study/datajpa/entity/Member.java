package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter // 실무에서는 가급적 @Setter 사용 자제.
@NoArgsConstructor(access = AccessLevel.PROTECTED) /** JPA 엔티티의 기본 생성자가 만들어줌 */
@ToString(of = {"id", "username", "age"}) /** 참조 필드 Team 은 무한루프 탈 수 있으니 지양하기 */
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username=:username"
)
@NamedEntityGraph(name="Member.all", attributeNodes = @NamedAttributeNode("team") )
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    /** [지연로딩]
     * JPA의 모든 연관관계는 LAZY로 세팅하기!
     * Member 객체를 사용할 때, 참조필드 Team을 제외하고 생성한다.
     * 실제 Team 객체를 참조할 때 실제 DB에서 Team 엔티티를 로딩한다. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    /** JPA 엔티티는 반드시 기본 생성자가 필요 */
    /* protected Member(){ //  -> @@NoArgsConstructor(access = AccessLevel.PROTECTED) 로 대체하자.
    } */

    public Member(String username){
        this.username = username;
    }

    public Member(String username, int age){
        this.username = username;
        this.age= age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){ // team이 null이라도 무시한다.
            changeTeam(team);
        }
    }

    /** 연관관계 편의 메소드 */
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
