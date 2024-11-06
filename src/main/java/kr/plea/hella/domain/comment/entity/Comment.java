package kr.plea.hella.domain.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.member.entity.Member;
import kr.plea.hella.global.entity.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_nickname", referencedColumnName = "nickName", nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "postId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    Comment parentComment;

    private Boolean isChild;

    private Integer childOrder = 0;

    private Boolean isDeleted;

    private Integer likeCount;

    public void updateContent(String content) {
        this.content = content;
    }

    public void setWriter(Member writer) {
        this.writer = writer;
        writer.getComments().add(this);
    }

    public void setPost(Post post) {
        this.post = post;
        post.getComment().add(this);
    }

    public int getNextOrder() {
        return ++childOrder;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void addLike() {
        likeCount += 1;
    }

    public void removeLike() {
        likeCount -= 1;
    }
}
