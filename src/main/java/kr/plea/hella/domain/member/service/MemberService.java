package kr.plea.hella.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.domain.member.dto.MemberFindDto;
import kr.plea.hella.domain.member.dto.MemberResignDto;
import kr.plea.hella.domain.member.dto.MemberSignUpDto;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.repository.MemberRepository;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    public void signUp(MemberSignUpDto dto) {
        Member member = dto.toEntity();
        member.setUserAuthority();
        member.encodePassword(passwordEncoder);
        if (isMemberExist(member.getUsername())) {
            throw new RootException(ExceptionCode.ALREADY_EXISTS_ID);
        }
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MemberFindDto getDto(String username) {
        Member findMember = memberRepository.findByUsername(username).orElse(null);
        if (findMember != null) {
            return new MemberFindDto(findMember);
        } else {
            throw new RootException(ExceptionCode.USER_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public Member findMember(String username) {
        Member findMember = memberRepository.findByUsername(username).orElse(null);
        if (findMember != null) {
            return findMember;
        } else {
            throw new RootException(ExceptionCode.USER_NOT_FOUND);
        }

    }

    public void updatePassword(String currPassword, String newPassword, String username) {
        Member findMember = findMember(username);
        if (!findMember.matchPassword(passwordEncoder, currPassword)) {
            throw new RootException(ExceptionCode.MISS_MATCH_PASSWORD);
        }
        if (findMember.matchPassword(passwordEncoder, newPassword)) {
            throw new RootException(ExceptionCode.NOT_CHANGED_PASSWORD);
        }
        findMember.updatePassword(passwordEncoder, newPassword);
    }

    public void signOut(String username, MemberResignDto dto) {
        Member findMember = findMember(username);
        if (!findMember.matchPassword(passwordEncoder, dto.password())) {
            throw new RootException(ExceptionCode.MISS_MATCH_PASSWORD);
        }
        postRepository.deletePostByMember(findMember);
        memberRepository.delete(findMember);
    }

    public boolean isMemberExist(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

}
