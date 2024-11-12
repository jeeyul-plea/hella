package kr.plea.hella.domain.member.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.plea.hella.domain.comment.entity.Comment;
import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.repository.EmitterRepository;
import kr.plea.hella.domain.post.entity.Post;
import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final static Long DEFAULT_TIMEOUT = 3600000L;
    private final static String NOTIFICATION_NAME = "notify";
    private final static String NOTIFICATION_CHANNEL = "notification-channel";

    private final EmitterRepository emitterRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void sendNotificationToRedis(String username, Long notificationId, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("username", username);
        notificationData.put("notificationId", notificationId);
        notificationData.put("message", message);
        try {
            String jsonMessage = objectMapper.writeValueAsString(notificationData);
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to send notification to Redis", e);
        }
    }

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

    public void sendCommentNotification(Long postId, Long commentId, String CommentWriter) {
        String postWriterUsername = getPostWriterUsername(postId);
        if (CommentWriter.equals(postWriterUsername)) {
            return;
        }
        sendNotificationToRedis(postWriterUsername, commentId, "게시글에 새로운 댓글이 달렸습니다.");
    }

    public void sendChildCommentNotification(Long postId, Long commentId, String childCommentWriter) {
        String parentWriterUsername = getParentWriterUsername(commentId);
        List<String> commentWriterUsernames = getChildWriterUsernames(commentId);
        if(!parentWriterUsername.equals(childCommentWriter)) {
            sendNotificationToRedis(parentWriterUsername, commentId, "내가 댓글을 남긴 댓글에 새로운 답글이 달렸습니다.");
        }
        if(commentWriterUsernames == null) {
            return;
        }
        for (String commentWriterUsername : commentWriterUsernames) {
            if(commentWriterUsername.equals(childCommentWriter)) {
                continue;
            }
            sendNotificationToRedis(commentWriterUsername, commentId, "내가 댓글을 남긴 댓글에 새로운 답글이 달렸습니다.");
        }
    }

    // private void doNotification(String username, Long notificationId, String message) {
    //     emitterRepository.get(username).ifPresentOrElse(sseEmitter -> {
    //         try {
    //             sseEmitter.send(
    //                 SseEmitter.event()
    //                     .id(notificationId.toString())
    //                     .name(NOTIFICATION_NAME)
    //                     .data(message));
    //         } catch (IOException e) {
    //             emitterRepository.delete(username);
    //             throw new RootException(ExceptionCode.NOTIFICATION_CONNECTION_ERROR);
    //         }
    //     }, () -> log.info("No  emitter found"));
    // }

    private String getParentWriterUsername(Long postId) {
        Comment findComment = commentRepository.findById(postId).orElseThrow(() -> new RootException(ExceptionCode.COMMENT_NOT_FOUND));
        return findComment.getWriter().getUsername();
    }

    private List<String> getChildWriterUsernames(Long commentId) {
        List<Comment> findComment = commentRepository.findChildIds(commentId);
        if (findComment.isEmpty()) {
            return null;
        }
        return findComment.stream().map(comment -> comment.getWriter().getUsername()).toList();
    }

    private String getPostWriterUsername(Long postId) {
        Post findPost = postRepository.findById(postId)
            .orElseThrow(() -> new RootException(ExceptionCode.POST_NOT_FOUND));
        return findPost.getWriter().getUsername();
    }
}
