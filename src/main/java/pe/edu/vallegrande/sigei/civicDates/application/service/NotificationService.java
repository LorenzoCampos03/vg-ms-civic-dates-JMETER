package pe.edu.vallegrande.sigei.civicDates.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.EventNotificationMessage;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventTypeImageRepository;
import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final R2dbcEventTypeImageRepository eventTypeImageRepository;

    public Mono<Void> notify(pe.edu.vallegrande.sigei.civicDates.domain.model.Event event) {
        if (event.getNotificationChannels() == null || event.getNotificationChannels().isEmpty()) {
            log.info("No notification channels selected for event: {}", event.getTitle());
            return Mono.empty();
        }

        Mono<String> imageUrlMono;
        String resolvedImageUrl = event.getImageUrl();

        if (resolvedImageUrl == null || resolvedImageUrl.isBlank()) {
            imageUrlMono = eventTypeImageRepository.findById(event.getEventType())
                    .map(entity -> entity.getImageUrl())
                    .defaultIfEmpty("");
        } else {
            imageUrlMono = Mono.just(resolvedImageUrl);
        }

        return Mono.zip(imageUrlMono, ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                        return jwtAuth.getToken().getTokenValue();
                    }
                    return "";
                })
                .defaultIfEmpty(""))
                .flatMap(tuple -> {
                    String finalImageUrl = tuple.getT1();
                    String token = tuple.getT2();

                    EventNotificationMessage message = EventNotificationMessage.builder()
                            .eventId(event.getId())
                            .eventType(event.getEventType())
                            .institutionId(event.getInstitutionId())
                            .title(event.getTitle())
                            .description(event.getDescription())
                            .eventDate(event.getStartDate())
                            .channels(event.getNotificationChannels())
                            .customMessage(event.getCustomMessage())
                            .targetRoles(event.getTargetRoles())
                            .imageUrl(finalImageUrl.isBlank() ? null : finalImageUrl)
                            .token(token.isBlank() ? null : token)
                            .build();

                    String routingKey = "event." + event.getEventType();

                    log.info("Publishing notification message to RabbitMQ with routing key: {} | imageUrl: {}", routingKey, finalImageUrl);

                    try {
                        rabbitTemplate.convertAndSend("notifications.exchange", routingKey, message);
                    } catch (Exception e) {
                        log.error("Failed to publish notification message for event: {}", event.getTitle(), e);
                    }
                    return Mono.empty();
                }).then();
    }
}
