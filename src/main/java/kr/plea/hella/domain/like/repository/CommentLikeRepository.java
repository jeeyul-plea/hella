package kr.plea.hella.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.plea.hella.domain.like.entity.CommentLike;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("SELECT l FROM CommentLike l WHERE l.comment.commentId = :commentId AND l.member.nickName = :nickName")
    Optional<CommentLike> findByCommentAndMember(@Param("commentId") Long commentId,
        @Param("nickName") String nickName);
}
