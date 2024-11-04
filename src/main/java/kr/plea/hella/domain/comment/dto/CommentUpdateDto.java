package kr.plea.hella.domain.comment.dto;

import jakarta.validation.constraints.NotEmpty;

public record CommentUpdateDto(@NotEmpty(message = "댓글 내용을 입력해주세요")
                               String content) {
}
