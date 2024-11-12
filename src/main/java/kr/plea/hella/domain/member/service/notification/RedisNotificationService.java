package kr.plea.hella.domain.member.service.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.component.Notification;
import kr.plea.hella.domain.member.component.NotificationBaseComponent;
import kr.plea.hella.domain.member.repository.EmitterRepository;
import kr.plea.hella.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedisNotificationService extends NotificationBaseComponent implements Notification {
    private static final String NOTIFICATION_CHANNEL = "notification-channel"; // redis channel
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisNotificationService(PostRepository postRepository,
        CommentRepository commentRepository,
        EmitterRepository emitterRepository, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(postRepository, commentRepository, emitterRepository);
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendNotification(String username, Long notificationId, String message) {
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

    @Override
    public void sendCommentNotification(Long postId, Long commentId, String CommentWriter) {
        String postWriterUsername = getPostWriterUsername(postId);
        if (CommentWriter.equals(postWriterUsername)) {
            return;
        }
        sendNotification(postWriterUsername, commentId, "게시글에 새로운 댓글이 달렸습니다.");
    }

    @Override
    public void sendChildCommentNotification(Long commentId, String childCommentWriter) {
        String parentWriterUsername = getParentWriterUsername(commentId);
        List<String> commentWriterUsernames = getChildWriterUsernames(commentId);
        if(!parentWriterUsername.equals(childCommentWriter)) {
            sendNotification(parentWriterUsername, commentId, "내가 댓글을 남긴 댓글에 새로운 답글이 달렸습니다.");
        }
        if(commentWriterUsernames == null) {
            return;
        }
        for (String commentWriterUsername : commentWriterUsernames) {
            if(commentWriterUsername.equals(childCommentWriter)) {
                continue;
            }
            sendNotification(commentWriterUsername, commentId, "내가 댓글을 남긴 댓글에 새로운 답글이 달렸습니다.");
        }
    }
}
