package kr.plea.hella.domain.post.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.plea.hella.domain.post.dto.PostCommentDto;
import kr.plea.hella.domain.post.dto.PostFindDto;
import kr.plea.hella.domain.post.dto.PostRequestDto;
import kr.plea.hella.domain.post.dto.PostUpdateDto;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;
import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.domain.comment.dto.CommentChildDto;
import kr.plea.hella.domain.comment.dto.CommentFindDto;
import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.service.MemberService;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

    @Transactional
    public void savePost(PostRequestDto dto, String username) {
        Member member = memberService.findMember(username);
        Post post = dto.ToEntity();
        post.setMember(member);
        postRepository.save(post);
    }

    public Post findPostById(Long postId) {
        Post findPost = postRepository.findById(postId).orElse(null);
        if (findPost != null) {
            return findPost;
        } else {
            throw new RootException(ExceptionCode.POST_NOT_FOUND);
        }
    }

    public List<PostFindDto> findPost(String username) {
        Member findMember = memberService.findMember(username);
        List<Post> postList = postRepository.findByWriter(findMember);
        return postList.stream().map(PostFindDto::new).toList();
    }

    public List<PostFindDto> findByCategory(String category) {
        Category enumValue = Category.valueOf(category.toUpperCase());
        List<Post> postList = postRepository.findByCategory(enumValue);
        return postList.stream().map(PostFindDto::new).toList();
    }

    public PostCommentDto findPostWithComment(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new RootException(ExceptionCode.POST_NOT_FOUND);
        }
        List<Comment> commnetList = commentRepository.findParentByPostId(postId);
        PostCommentDto postCommentDto = new PostCommentDto(post);
        List<CommentFindDto> commentFindDtoList = new ArrayList<>();
        for (Comment comment : commnetList) {
            List<CommentChildDto> childCommentList = new ArrayList<>();
            if (comment.getChildOrder() != 0) {
                List<Comment> findChildCommentList = commentRepository.findChildByPostAndParent(postId,
                    comment.getCommentId());
                for (Comment child : findChildCommentList) {
                    CommentChildDto childFindDto = new CommentChildDto(child);
                    childCommentList.add(childFindDto);
                }
            }
            CommentFindDto commentFindDto = new CommentFindDto(comment);
            if (!childCommentList.isEmpty()) {
                commentFindDto.setChildList(childCommentList);
            }
            commentFindDtoList.add(commentFindDto);
        }
        postCommentDto.setCommentList(commentFindDtoList);
        return postCommentDto;
    }

    @Transactional
    public PostFindDto updatePost(PostUpdateDto dto, Long postId) {
        Post findPost = getPost(postId);
        findPost.updatePost(dto);
        return new PostFindDto(findPost);
    }

    @Transactional
    public void deletePost(Long postId) {
        commentRepository.deleteByPostId(postId);
        postRepository.deleteByPostId(postId);
    }

    private Post getPost(Long postId) {
        Post findPost = postRepository.findById(postId).orElse(null);
        if (findPost == null) {
            throw new RootException(ExceptionCode.POST_NOT_FOUND);
        }
        return findPost;
    }
}
