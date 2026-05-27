package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.EventResponse;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.EventMapper;
import pe.edu.vallegrande.sigei.civicDates.application.service.EventService;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.external.CloudinaryService;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.common.ApiResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public Flux<ApiResponse<EventResponse>> getAllEvents() {
        return eventService.getAllEvents()
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event retrieved successfully")
                        .data(event)
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<EventResponse>> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event retrieved successfully")
                        .data(event)
                        .build());
    }

    @GetMapping("/institution/{institutionId}")
    public Flux<ApiResponse<EventResponse>> getEventsByInstitution(@PathVariable String institutionId) {
        return eventService.getEventsByInstitution(institutionId)
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Events retrieved successfully")
                        .data(event)
                        .build());
    }

    @GetMapping("/inactive")
    public Flux<ApiResponse<EventResponse>> getInactiveEvents() {
        return eventService.getInactiveEvents()
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Inactive events retrieved successfully")
                        .data(event)
                        .build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<EventResponse>> createEvent(@RequestBody CreateEventRequest request) {
        return eventService.createEvent(eventMapper.toEvent(request))
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event created successfully")
                        .data(event)
                        .build());
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<EventResponse>> updateEvent(
            @PathVariable Long id,
            @RequestBody UpdateEventRequest request) {
        return eventService.getEventById(id)
                .map(existingEvent -> eventMapper.toEvent(request, existingEvent))
                .flatMap(event -> eventService.updateEvent(id, event))
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event updated successfully")
                        .data(event)
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id)
                .then(Mono.just(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Event deleted successfully")
                        .build()));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ApiResponse<EventResponse>> restoreEvent(@PathVariable Long id) {
        return eventService.restoreEvent(id)
                .map(eventMapper::toResponse)
                .map(event -> ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event restored successfully")
                        .data(event)
                        .build());
    }

    // ── Cloudinary Image Upload ───────────────────────────────────────────────

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ApiResponse<String>> uploadImage(@RequestPart("file") FilePart filePart) {
        return cloudinaryService.uploadImage(filePart)
                .map(url -> ApiResponse.<String>builder()
                        .success(true)
                        .message("Image uploaded successfully")
                        .data(url)
                        .build());
    }

    // ── Event Type Default Image Management ──────────────────────────────────

    @GetMapping("/types")
    public Flux<ApiResponse<Map<String, String>>> getAllDefaultImages() {
        return eventService.getAllDefaultImages()
                .map(entry -> ApiResponse.<Map<String, String>>builder()
                        .success(true)
                        .message("Event type images retrieved successfully")
                        .data(entry)
                        .build());
    }

    @PutMapping("/types/{eventType}")
    public Mono<ApiResponse<Map<String, String>>> updateDefaultImage(
            @PathVariable String eventType,
            @RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        return eventService.updateDefaultImage(eventType.toUpperCase(), imageUrl)
                .map(entity -> ApiResponse.<Map<String, String>>builder()
                        .success(true)
                        .message("Default image updated for type: " + entity.getEventType())
                        .data(Map.of("eventType", entity.getEventType(), "imageUrl", entity.getImageUrl()))
                        .build());
    }
}
