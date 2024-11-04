package kr.plea.hella.domain.post.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PostUpdateDto(@Size(min = 1, max = 20, message = "1자 이상 20자 이하로 작성해주세요.")
                            @NotEmpty String title,
                            @Size(min = 1, message = "1자 이상 작성해주세요.")
                            @NotEmpty String content) {
}
