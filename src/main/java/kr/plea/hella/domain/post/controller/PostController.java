package kr.plea.hella.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.plea.hella.domain.post.dto.PostCommentDto;
import kr.plea.hella.domain.post.dto.PostFindDto;
import kr.plea.hella.domain.post.dto.PostRequestDto;
import kr.plea.hella.domain.post.dto.PostUpdateDto;
import kr.plea.hella.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> savePost(@RequestBody PostRequestDto dto) {
        String username = getUsername();
        postService.savePost(dto, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostFindDto>> findPost() {
        String username = getUsername();
        List<PostFindDto> findDtoList = postService.findPost(username);
        return ResponseEntity.ok(findDtoList);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<PostFindDto>> findPostByCategory(@PathVariable("category") String category) {
        List<PostFindDto> findDtoList = postService.findByCategory(category);
        return ResponseEntity.ok(findDtoList);
    }

    @GetMapping("/all/{postId}")
    public ResponseEntity<PostCommentDto> findPostWithComment(@PathVariable("postId") Long postId) {
        PostCommentDto findDtoList = postService.findPostWithComment(postId);
        return ResponseEntity.ok(findDtoList);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostFindDto> updatePost(@PathVariable("postId") Long postId,
        @RequestBody PostUpdateDto dto) {
        PostFindDto postFindDto = postService.updatePost(dto, postId);
        return ResponseEntity.ok(postFindDto);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();
        return userDetails.getUsername();
    }

}
