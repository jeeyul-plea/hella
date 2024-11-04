package kr.plea.hella.domain.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.member.entity.Member;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.isChild = false")
    List<Comment> findParentByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId And c.parentComment.commentId = :parentId order by c.childOrder ASC")
    List<Comment> findChildByPostAndParent(@Param("postId") Long postId, @Param("parentId") Long parentId);

    Optional<Comment> findByWriter(Member writer);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
