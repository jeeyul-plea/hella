package kr.plea.hella.domain.member.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.plea.hella.domain.member.entity.Member;

public record MemberSignUpDto(
    @NotEmpty(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    @Size(min = 7, max = 25, message = "아이디는 7~25자 내외로 영문과 숫자를 최소 1자이상씩 포함하여 입력해주세요")
    String username,

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
        message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    String password,

    @NotEmpty(message = "이름을 입력해주세요") @Size(min = 2, message = "사용자 이름이 너무 짧습니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
    String name,

    @NotEmpty(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 15, message = "닉네임은 2~15자 사이로 입력해주세요.")
    @NotEmpty String nickName,

    @NotEmpty(message = "나이를 입력해주세요")
    @Range(min = 0, max = 150)
    Integer age) {

    public Member toEntity() {
        return Member.builder().username(username).password(password).name(name).nickName(nickName).age(age).build();
    }
}