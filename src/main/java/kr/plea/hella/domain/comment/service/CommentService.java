package kr.plea.hella.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.service.PostService;
import kr.plea.hella.domain.comment.dto.CommentPostDto;
import kr.plea.hella.domain.comment.dto.CommentUpdateDto;
import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.service.MemberService;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MemberService memberService;

    public void postComment(CommentPostDto dto, Long postId, String username) {
        Member findMember = memberService.findMember(username);
        Post findPost = postService.findPostById(postId);
        Comment comment = Comment.builder()
            .content(dto.content())
            .childOrder(0)
            .isChild(false)
            .build();
        comment.setPost(findPost);
        comment.setWriter(findMember);
        commentRepository.save(comment);
    }

    public void postChildComment(CommentPostDto dto, Long postId, Long commentId, String username) {
        Member findMember = memberService.findMember(username);
        Post findPost = postService.findPostById(postId);
        Comment parent = commentRepository.findById(commentId).orElse(null);
        if (parent == null) {
            throw new RootException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        int order = parent.getNextOrder();
        Comment child = Comment.builder()
            .content(dto.content())
            .isChild(true)
            .childOrder(order).build();
        child.setWriter(findMember);
        child.setPost(findPost);
        child.setParentComment(parent);
        commentRepository.save(child);
    }

    public void updateComment(String username, CommentUpdateDto dto) {
        Member findMember = memberService.findMember(username);
        Comment comment = commentRepository.findByWriter(findMember).orElse(null);
        if (comment == null) {
            throw new RootException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        comment.updateContent(dto.content());
    }
}
