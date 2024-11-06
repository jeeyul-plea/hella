package kr.plea.hella.domain.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import kr.plea.hella.domain.comment.entity.Comment;

public record CommentPostDto(@NotEmpty(message = "댓글 내용을 입력해주세요")
                             @Size(min = 1, max = 1000, message = "1000자 내외로 댓글 내용을 입력하세요")
                             String content) {
    public Comment toEntity() {
        return Comment.builder()
            .content(this.content())
            .childOrder(0)
            .isChild(false)
            .isDeleted(false)
            .likeCount(0)
            .build();
    }
}
