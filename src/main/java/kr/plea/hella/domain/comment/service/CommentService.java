package kr.plea.hella.domain.comment.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.plea.hella.domain.comment.dto.CommentPostDto;
import kr.plea.hella.domain.comment.dto.CommentUpdateDto;
import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.like.entity.CommentLike;
import kr.plea.hella.domain.like.repository.CommentLikeRepository;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.service.MemberService;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.service.PostService;
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
    private final CommentLikeRepository commentLikeRepository;

    public void postComment(CommentPostDto dto, Long postId, String username) {
        Member findMember = memberService.findMember(username);
        Post findPost = postService.findPostById(postId);
        Comment comment = dto.toEntity();
        comment.setPost(findPost);
        comment.setWriter(findMember);
        commentRepository.save(comment);
    }

    public void postChildComment(CommentPostDto dto, Long postId, Long commentId, String username) {
        Member findMember = memberService.findMember(username);
        Post findPost = postService.findPostById(postId);
        Comment parent = getCommentById(commentId);
        if(is_Deleted(parent)){
            throw new RootException(ExceptionCode.COMMENT_DELETED);
        }
        int order = parent.getNextOrder();
        Comment child = Comment.builder()
            .content(dto.content())
            .isChild(true)
            .isDeleted(false)
            .childOrder(order)
            .likeCount(0)
            .build();
        child.setWriter(findMember);
        child.setPost(findPost);
        child.setParentComment(parent);
        commentRepository.save(child);
    }

    public void updateComment(String username, CommentUpdateDto dto, Long commentId) {
        Member findMember = memberService.findMember(username);
        Comment comment = getCommentById(commentId);
        if(is_Deleted(comment)){
            throw new RootException(ExceptionCode.COMMENT_DELETED);
        }
        if (!Objects.equals(findMember.getNickName(), comment.getWriter().getNickName())) {
            throw new RootException(ExceptionCode.ACCESS_DENIED);
        }
        comment.updateContent(dto.content());
    }

    public void deleteComment(Long commentId) {
        Comment findComment = getCommentById(commentId);
        findComment.delete();
    }

    public void postLike(String username, Long commentId) {
        Member findMember = memberService.findMember(username);
        Comment comment = getCommentById(commentId);
        if(is_Deleted(comment)){
            throw new RootException(ExceptionCode.COMMENT_DELETED);
        }
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndMember(commentId,
            findMember.getNickName());
        if (existingLike.isPresent()) {
            throw new RootException(ExceptionCode.COMMENT_ALREADY_LIKED);
        }
        comment.addLike();
        CommentLike commentLike = CommentLike.builder()
            .member(findMember)
            .comment(comment).build();
        commentLikeRepository.save(commentLike);
    }

    public void undoLike(String username, Long commentId) {
        Member findMember = memberService.findMember(username);
        Comment comment = getCommentById(commentId);
        if(is_Deleted(comment)){
            throw new RootException(ExceptionCode.COMMENT_DELETED);
        }
        comment.removeLike();
        commentLikeRepository.findByCommentAndMember(commentId, findMember.getNickName()).ifPresent(commentLikeRepository::delete);
    }

    @Transactional(readOnly = true)
    protected Comment getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new RootException(ExceptionCode.COMMENT_NOT_FOUND);
        }
        return comment;
    }

    protected boolean is_Deleted(Comment comment) {
        return comment.getIsDeleted();
    }
}
