package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.AddEventsToCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarResponse;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarWithEventsResponse;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.CalendarMapper;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.EventMapper;
import pe.edu.vallegrande.sigei.civicDates.application.service.CalendarService;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.common.ApiResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;
    private final CalendarMapper calendarMapper;
    private final EventMapper eventMapper;

    @GetMapping
    public Flux<ApiResponse<CalendarResponse>> getAllCalendars() {
        return calendarService.getAllCalendars()
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar retrieved successfully")
                        .data(calendar)
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<CalendarResponse>> getCalendarById(@PathVariable Integer id) {
        return calendarService.getCalendarById(id)
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar retrieved successfully")
                        .data(calendar)
                        .build());
    }

    @GetMapping("/institution/{institutionId}")
    public Flux<ApiResponse<CalendarResponse>> getCalendarsByInstitution(@PathVariable String institutionId) {
        return calendarService.getCalendarsByInstitution(institutionId)
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendars retrieved successfully")
                        .data(calendar)
                        .build());
    }

    @GetMapping("/{id}/events")
    public Mono<ApiResponse<CalendarWithEventsResponse>> getCalendarWithEvents(@PathVariable Integer id) {
        return calendarService.getCalendarById(id)
                .flatMap(calendar -> 
                    calendarService.getEventsByCalendar(id)
                            .map(eventMapper::toResponse)
                            .collectList()
                            .map(events -> calendarMapper.toResponseWithEvents(calendar, events))
                )
                .map(calendar -> ApiResponse.<CalendarWithEventsResponse>builder()
                        .success(true)
                        .message("Calendar with events retrieved successfully")
                        .data(calendar)
                        .build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<CalendarResponse>> createCalendar(@RequestBody CreateCalendarRequest request) {
        return calendarService.createCalendar(calendarMapper.toCalendar(request))
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar created successfully")
                        .data(calendar)
                        .build());
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<CalendarResponse>> importCalendar(
            @RequestBody CreateCalendarRequest request,
            @RequestParam(required = false) AddEventsToCalendarRequest eventsRequest) {
        return calendarService.importCalendar(
                        calendarMapper.toCalendar(request),
                        eventsRequest != null ? eventsRequest.getEventIds() : null)
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar imported successfully")
                        .data(calendar)
                        .build());
    }

    @PostMapping("/{id}/events")
    public Mono<ApiResponse<Void>> addEventsToCalendar(
            @PathVariable Integer id,
            @RequestBody AddEventsToCalendarRequest request) {
        return calendarService.addEventsToCalendar(id, request.getEventIds())
                .then(Mono.just(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Events added to calendar successfully")
                        .build()));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<CalendarResponse>> updateCalendar(
            @PathVariable Integer id,
            @RequestBody UpdateCalendarRequest request) {
        AcademicCalendar calendarUpdate = AcademicCalendar.builder()
                .academicYear(request.getAcademicYear())
                .academicYearName(request.getAcademicYearName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return calendarService.updateCalendar(id, calendarUpdate)
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar updated successfully")
                        .data(calendar)
                        .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCalendar(@PathVariable Integer id) {
        return calendarService.deleteCalendarLogically(id);
    }

    @PostMapping("/{id}/reactivate")
    public Mono<ApiResponse<CalendarResponse>> reactivateCalendar(@PathVariable Integer id) {
        return calendarService.reactivateCalendar(id)
                .map(calendarMapper::toResponse)
                .map(calendar -> ApiResponse.<CalendarResponse>builder()
                        .success(true)
                        .message("Calendar reactivated successfully")
                        .data(calendar)
                        .build());
    }
}
