package com.assetflow.service;

import com.assetflow.dto.response.NotificationResponse;
import com.assetflow.entity.Employee;
import com.assetflow.entity.Notification;
import com.assetflow.repository.EmployeeRepository;
import com.assetflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    public void sendNotification(Employee recipient, String message, String type) {
        if (recipient == null) return;
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String email) {
        Employee current = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(current.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void markAsRead(Long id, String email) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getRecipient().getEmail().equals(email)) {
            throw new SecurityException("Unauthorized access to notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String email) {
        Employee current = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Notification> unread = notificationRepository.findByRecipientIdAndReadOrderByCreatedAtDesc(current.getId(), false);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .recipientId(n.getRecipient().getId())
                .recipientName(n.getRecipient().getFullName())
                .message(n.getMessage())
                .read(n.getRead())
                .type(n.getType())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
