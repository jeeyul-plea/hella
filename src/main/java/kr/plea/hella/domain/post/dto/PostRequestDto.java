package kr.plea.hella.domain.post.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;

public record PostRequestDto(@Size(min = 1, max = 20, message = "1자 이상 20자 이하로 작성해주세요.")
                             @NotEmpty String title,
                             @Size(min = 1, message = "1자 이상 작성해주세요.")
                             @NotEmpty String content,
                             @NotEmpty @Enumerated(EnumType.STRING)
                             Category category) {

    public Post ToEntity() {
        return Post.builder()
            .title(this.title)
            .content(this.content)
            .category(this.category)
            .likeCount(0)
            .build();
    }
}
