package kr.plea.hella.domain.member.component;

public interface Notification {
    void sendNotification(String username, Long notificationId, String message);
    void sendCommentNotification(Long postId, Long commentId, String CommentWriter);
    void sendChildCommentNotification(Long commentId, String childCommentWriter);
}
