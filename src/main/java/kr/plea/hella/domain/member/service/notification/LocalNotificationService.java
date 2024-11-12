package kr.plea.hella.domain.member.service.notification;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.plea.hella.domain.comment.repository.CommentRepository;
import kr.plea.hella.domain.member.component.Notification;
import kr.plea.hella.domain.member.component.NotificationBaseComponent;
import kr.plea.hella.domain.member.repository.EmitterRepository;
import kr.plea.hella.domain.post.repository.PostRepository;
import kr.plea.hella.global.exception.ExceptionCode;
import kr.plea.hella.global.exception.RootException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocalNotificationService extends NotificationBaseComponent implements Notification {

    public LocalNotificationService(PostRepository postRepository, CommentRepository commentRepository,
        EmitterRepository emitterRepository) {
        super(postRepository, commentRepository, emitterRepository);
    }

    @Override
    public void sendNotification(String username, Long notificationId, String message) {
        emitterRepository.get(username).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(
                    SseEmitter.event()
                        .id(notificationId.toString())
                        .name(NOTIFICATION_NAME)
                        .data(message));
            } catch (IOException e) {
                emitterRepository.delete(username);
                throw new RootException(ExceptionCode.NOTIFICATION_CONNECTION_ERROR);
            }
        }, () -> log.info("No  emitter found"));
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
