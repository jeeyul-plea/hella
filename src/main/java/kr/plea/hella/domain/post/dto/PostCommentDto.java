package kr.plea.hella.domain.post.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;
import kr.plea.hella.domain.comment.dto.CommentFindDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCommentDto {

    private Long postId;
    private String title;
    private String content;
    private String Nickname;

    @Enumerated(EnumType.STRING)
    private Category category;

    private List<CommentFindDto> commentList = new ArrayList<>();

    public PostCommentDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.Nickname = post.getWriter().getNickName();
    }

}