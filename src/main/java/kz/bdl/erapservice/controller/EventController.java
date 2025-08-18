package kz.bdl.erapservice.controller;

import kz.bdl.erapservice.dto.HikvisionEventRequest;
import kz.bdl.erapservice.dto.HikvisionEventResponse;
import kz.bdl.erapservice.entity.Event;
import kz.bdl.erapservice.exception.ResourceBadRequestException;
import kz.bdl.erapservice.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event/v1")
@AllArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping("/hikvision")
    public ResponseEntity<HikvisionEventResponse> hikvisionEvent(@RequestBody HikvisionEventRequest request) {
        log.info("Received Hikvision event request with ID: {}", request.getId());
        
        try {
            Event savedEvent = eventService.saveHikvisionEvent(request);
            log.info("Successfully processed Hikvision event with ID: {}", savedEvent.getId());
            return ResponseEntity.ok(new HikvisionEventResponse("0", "success"));
        } catch (ResourceBadRequestException e) {
            log.error("Failed to process Hikvision event: {}", e.getMessage());
            return ResponseEntity.ok(new HikvisionEventResponse("400", e.getMessage()));
        }
    }
}
