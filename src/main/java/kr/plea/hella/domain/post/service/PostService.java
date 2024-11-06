package kr.plea.hella.domain.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.plea.hella.domain.comment.dto.CommentChildDto;
import kr.plea.hella.domain.comment.dto.CommentFindDto;
import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.like.entity.PostLike;
import kr.plea.hella.domain.like.repository.PostLikeRepository;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.member.service.MemberService;
import kr.plea.hella.domain.post.dto.PostCommentDto;
import kr.plea.hella.domain.post.dto.PostFindDto;
import kr.plea.hella.domain.post.dto.PostRequestDto;
import kr.plea.hella.domain.post.dto.PostUpdateDto;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;
import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostService {
    private final String LIKE_KEY_PREFIX = "like-";
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

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
        return postList.stream()
            .map(PostFindDto::new).toList();
    }

    public List<PostFindDto> findByCategory(String category) {
        Category enumValue = Category.valueOf(category.toUpperCase());
        List<Post> postList = postRepository.findByCategory(enumValue);
        return getPostFindDtos(postList);
    }

    public PostCommentDto findPostWithComment(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RootException(ExceptionCode.POST_NOT_FOUND));
        List<Comment> commnetList = commentRepository.findParentByPostId(postId);
        PostCommentDto postCommentDto = new PostCommentDto(post);
        List<CommentFindDto> commentFindDtoList = new ArrayList<>();
        Integer likeCount = postLikeRepository.getCountByPostId(post.getPostId());
        for (Comment comment : commnetList) {
            List<CommentChildDto> childCommentList = new ArrayList<>();
            if (comment.getChildOrder() != 0) {
                List<Comment> findChildCommentList = commentRepository.findChildByPostAndParent(postId,
                    comment.getCommentId());
                for (Comment child : findChildCommentList) {
                    CommentChildDto childFindDto = new CommentChildDto(child);
                    if (child.getIsDeleted()) {
                        childFindDto.setDeleted();
                    }
                    childCommentList.add(childFindDto);
                }
            }
            CommentFindDto commentFindDto = new CommentFindDto(comment);
            if (!childCommentList.isEmpty()) {
                commentFindDto.setChildList(childCommentList);
            }
            if (comment.getIsDeleted()) {
                commentFindDto.setDeleted();
            }
            commentFindDtoList.add(commentFindDto);
        }
        postCommentDto.setCommentList(commentFindDtoList);
        postCommentDto.setLike(likeCount);
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
        commentRepository.deleteChildByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postRepository.deleteByPostId(postId);
    }

    @Transactional
    public void postLike(Long postId, String username) {
        Member findMember = memberService.findMember(username);
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndMember(postId, findMember.getNickName());
        if (existingLike.isPresent()) {
            throw new RootException(ExceptionCode.POST_ALREADY_LIKED);
        }
        Post findPost = getPost(postId);
        findPost.addLike();
        PostLike postLike = PostLike.builder()
            .member(findMember)
            .post(findPost)
            .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void undoLike(Long postId, String username) {
        Member findMember = memberService.findMember(username);
        Post findPost = getPost(postId);
        PostLike postLike = postLikeRepository.findByPostIdAndMember(postId, findMember.getNickName())
            .orElseThrow(() -> new RootException(ExceptionCode.POST_LIKE_NOT_FOUND));
        findPost.removeLike();
        postLikeRepository.delete(postLike);
    }

    private Post getPost(Long postId) {
        Post findPost = postRepository.findById(postId).orElse(null);
        if (findPost == null) {
            throw new RootException(ExceptionCode.POST_NOT_FOUND);
        }
        return findPost;
    }

    private List<PostFindDto> getPostFindDtos(List<Post> postList) {
        List<Long> postIds = postList.stream().map(Post::getPostId).toList();
        List<Integer> countByPostId = postLikeRepository.getCountByPostIdList(postIds);
        return postList.stream()
            .map(post -> {
                Integer likeCount = 0;
                if (!countByPostId.isEmpty()) {
                    likeCount = countByPostId.get(0);
                    countByPostId.remove(0);
                }
                PostFindDto postFindDto = new PostFindDto(post);
                postFindDto.setLike(likeCount);
                return postFindDto;
            })
            .collect(Collectors.toList());
    }
}
