package kr.plea.hella.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.plea.hella.domain.member.dto.MemberFindDto;
import kr.plea.hella.domain.member.dto.MemberResignDto;
import kr.plea.hella.domain.member.dto.MemberSignUpDto;
import kr.plea.hella.domain.member.dto.MemberUpdatePassDto;
import kr.plea.hella.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody MemberSignUpDto dto) {
        memberService.signUp(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberFindDto> findMember() {
        String username = getUsername();
        return ResponseEntity.ok(memberService.getDto(username));
    }

    @PatchMapping("/passwords")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(@RequestBody MemberUpdatePassDto dto) {
        String username = getUsername();
        memberService.updatePassword(dto.currPassword(), dto.newPassword(), username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/resign")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> signOut(@RequestBody MemberResignDto dto) {
        String username = getUsername();
        memberService.signOut(username, dto);
        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();
        return userDetails.getUsername();
    }
}
