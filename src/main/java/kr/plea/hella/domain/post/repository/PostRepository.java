package kr.plea.hella.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.enums.Category;
import kr.plea.hella.domain.member.entity.Member;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByWriter(Member member);
    List<Post> findByCategory(Category category);

    @Modifying
    @Query("delete FROM Post b WHERE b.writer = :member")
    void deletePostByMember(@Param("member") Member member);

    @Modifying
    @Query("DELETE FROM Post b WHERE b.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}