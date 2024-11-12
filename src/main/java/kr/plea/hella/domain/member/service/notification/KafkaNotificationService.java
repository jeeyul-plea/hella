package kr.plea.hella.domain.member.service.notification;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.component.Notification;
import kr.plea.hella.domain.member.component.NotificationBaseComponent;
import kr.plea.hella.domain.member.component.NotificationMessage;
import kr.plea.hella.domain.member.repository.EmitterRepository;
import kr.plea.hella.domain.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaNotificationService extends NotificationBaseComponent implements Notification {
    private static final String NOTIFICATION_TOPIC = "notification-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaNotificationService(PostRepository postRepository,
        CommentRepository commentRepository,
        EmitterRepository emitterRepository, KafkaTemplate<String, String> kafkaTemplate) {
        super(postRepository, commentRepository, emitterRepository);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(String username, Long notificationId, String message) {
        NotificationMessage notiMessage = new NotificationMessage(username, notificationId, "게시글에 새로운 댓글이 달렸습니다.");
        kafkaTemplate.send(NOTIFICATION_TOPIC, notiMessage.toJson());
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
