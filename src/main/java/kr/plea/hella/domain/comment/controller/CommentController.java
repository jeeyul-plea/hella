package kr.plea.hella.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.plea.hella.domain.comment.dto.CommentPostDto;
import kr.plea.hella.domain.comment.dto.CommentUpdateDto;
import kr.plea.hella.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> postComment(@RequestBody CommentPostDto dto, @PathVariable("postId") Long postId) {
        String username = getUsername();
        commentService.postComment(dto, postId, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/children/{postId}/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> postChildComment(@RequestBody CommentPostDto dto, @PathVariable("postId") Long postId,
        @PathVariable("commentId") Long commentId) {
        String username = getUsername();
        commentService.postChildComment(dto, postId, commentId, username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateComment(@RequestBody CommentUpdateDto dto) {
        String username = getUsername();
        commentService.updateComment(username, dto);
        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new UsernameNotFoundException("사용자가 인증되지 않았습니다.");
    }
}
