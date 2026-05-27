package pe.edu.vallegrande.sigei.civicDates.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventNotificationScheduler {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    /**
     * Executes every day at 08:00 AM.
     * Finds active events occurring today with scheduled notifications enabled and sends them.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduleDailyNotifications() {
        log.info("Running daily scheduled notifications job at {}", java.time.LocalDateTime.now());
        LocalDate today = LocalDate.now();

        eventRepository.findAll()
                .filter(event -> EventStatus.ACTIVE.equals(event.getStatus()))
                .filter(event -> today.equals(event.getStartDate()))
                .filter(event -> Boolean.TRUE.equals(event.getIsNotificationScheduled()))
                .doOnNext(event -> {
                    log.info("Sending scheduled notification for event ID: {}", event.getId());
                    notificationService.notify(event);
                })
                .subscribe();
    }
}
