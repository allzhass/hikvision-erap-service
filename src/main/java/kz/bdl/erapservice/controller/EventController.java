package kz.bdl.erapservice.controller;

import kz.bdl.erapservice.dto.HikvisionEventRequest;
import kz.bdl.erapservice.entity.Event;
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
    public ResponseEntity<Event> hikvisionEvent(@RequestBody HikvisionEventRequest request) {
        log.info("Received Hikvision event request with ID: {}", request.getId());
        
        Event savedEvent = eventService.saveHikvisionEvent(request);
        
        log.info("Successfully processed Hikvision event with ID: {}", savedEvent.getId());
        return ResponseEntity.ok(savedEvent);
    }
}
