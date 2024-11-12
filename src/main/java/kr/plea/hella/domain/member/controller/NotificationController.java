package kr.plea.hella.domain.member.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.plea.hella.domain.member.service.notification.LocalNotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final LocalNotificationService notificationService;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public SseEmitter subscribe() {
        String username = getUsername();
        return notificationService.connectNotification(username);
    }

    private static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails)auth.getPrincipal();
        return userDetails.getUsername();
    }
}
