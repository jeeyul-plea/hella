package kr.plea.hella.domain.member.component;

import java.io.IOException;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.repository.EmitterRepository;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NotificationBaseComponent {
    protected final PostRepository postRepository;
    protected final CommentRepository commentRepository;
    protected static final Long DEFAULT_TIMEOUT = 3600000L;
    protected static final String NOTIFICATION_NAME = "notify";
    protected final EmitterRepository emitterRepository;

    public SseEmitter connectNotification(String username) {
        SseEmitter sseEmitter = emitterRepository.save(username, new SseEmitter(DEFAULT_TIMEOUT));

        sseEmitter.onCompletion(() -> emitterRepository.delete(username));
        sseEmitter.onTimeout(() -> emitterRepository.delete(username));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
        } catch (IOException e) {
            throw new RootException(ExceptionCode.NOTIFICATION_CONNECTION_ERROR);
        }
        return sseEmitter;
    }


    protected String getParentWriterUsername(Long postId) {
        Comment findComment = commentRepository.findById(postId).orElseThrow(() -> new RootException(ExceptionCode.COMMENT_NOT_FOUND));
        return findComment.getWriter().getUsername();
    }

    protected List<String> getChildWriterUsernames(Long commentId) {
        List<Comment> findComment = commentRepository.findChildIds(commentId);
        if (findComment.isEmpty()) {
            return null;
        }
        return findComment.stream().map(comment -> comment.getWriter().getUsername()).toList();
    }

    protected String getPostWriterUsername(Long postId) {
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new RootException(ExceptionCode.POST_NOT_FOUND));
        return findPost.getWriter().getUsername();
    }
}
