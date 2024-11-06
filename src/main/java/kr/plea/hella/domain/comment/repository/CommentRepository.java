package kr.plea.hella.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.plea.hella.domain.comment.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.isChild = false")
    List<Comment> findParentByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId And c.parentComment.commentId = :parentId order by c.childOrder ASC")
    List<Comment> findChildByPostAndParent(@Param("postId") Long postId, @Param("parentId") Long parentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.postId = :postId AND c.isChild = true")
    void deleteChildByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
