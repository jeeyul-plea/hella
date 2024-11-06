package kr.plea.hella.domain.post.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.domain.post.dto.PostUpdateDto;
import kr.plea.hella.domain.post.enums.Category;
import kr.plea.hella.global.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_nickname", referencedColumnName = "nickName", nullable = false)
    private Member writer;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comment = new ArrayList<>();

    public void setMember(Member member) {
        this.writer = member;
        this.writer.getPosts().add(this);
    }

    public void updatePost(PostUpdateDto dto) {
        this.title = dto.title();
        this.content = dto.content();
    }

    public void addLike() {
        this.likeCount += 1;
    }

    public void removeLike() {
        this.likeCount -= 1;
    }

}
