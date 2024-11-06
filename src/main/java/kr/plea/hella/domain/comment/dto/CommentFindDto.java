package kr.plea.hella.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kr.plea.hella.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentFindDto {
    private String content;
    private String writerNickName;
    private LocalDateTime lastModifiedDate;
    private Integer like;
    private List<CommentChildDto> childList = new ArrayList<>();

    public CommentFindDto(Comment comment) {
        this.content = comment.getContent();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.writerNickName = comment.getWriter().getNickName();
        this.like = comment.getLikeCount();
    }

    public void setDeleted() {
        this.content = "삭제된 댓글입니다.";
        this.writerNickName = "";
    }
}
