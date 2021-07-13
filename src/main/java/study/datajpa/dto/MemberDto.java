package study.datajpa.dto;

import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    /**  @Query 에 쓰일 생성자 필요
     *  @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name)
     *          from Member m join m.team t ")
     * */
    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
