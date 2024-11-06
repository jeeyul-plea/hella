package kr.plea.hella.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.plea.hella.domain.like.entity.PostLike;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT l FROM PostLike l WHERE l.post.postId =:postId AND l.member.nickName = :username")
    Optional<PostLike> findByPostIdAndMember(@Param("postId") Long postId, @Param("username") String username);

    @Query("SELECT COUNT(*) FROM PostLike l WHERE l.post.postId in :postIdList group by l.post.postId order by l.post.postId")
    List<Integer> getCountByPostIdList(@Param("postIdList") List<Long> postIdList);

    @Query("SELECT COUNT(*) FROM PostLike l WHERE l.post.postId = :postId ")
    Integer getCountByPostId(@Param("postId") Long postId);
}
