package kr.plea.hella.domain.post.dto;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFindDto {

    private Long postId;
    private String title;
    private String content;
    private String Nickname;
    private LocalDateTime lastModified;
    private Integer like;

    @Enumerated(EnumType.STRING)
    private Category category;

    public PostFindDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.Nickname = post.getWriter().getNickName();
        this.lastModified = post.getLastModifiedDate();
        this.like = post.getLikeCount();
    }

}
