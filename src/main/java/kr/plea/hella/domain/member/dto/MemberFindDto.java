package kr.plea.hella.domain.member.dto;

import kr.plea.hella.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFindDto {

    private String name;
    private String nickName;
    private Integer age;

    public MemberFindDto(Member member) {
        this.name = member.getName();
        this.nickName = member.getNickName();
        this.age = member.getAge();
    }
}
