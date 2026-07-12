package com.assetflow.controller;

import com.assetflow.dto.response.NotificationResponse;
import com.assetflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> getMyNotifications(Principal principal) {
        return notificationService.getMyNotifications(principal.getName());
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public void markRead(@PathVariable Long id, Principal principal) {
        notificationService.markAsRead(id, principal.getName());
    }

    @PostMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public void markAllRead(Principal principal) {
        notificationService.markAllAsRead(principal.getName());
    }
}
