package kz.bdl.erapservice.service.impl;

import kz.bdl.erapservice.dto.HikvisionEventRequest;
import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.Event;
import kz.bdl.erapservice.entity.Violation;
import kz.bdl.erapservice.exception.ResourceBadRequestException;
import kz.bdl.erapservice.repository.CameraRepository;
import kz.bdl.erapservice.repository.EventRepository;
import kz.bdl.erapservice.repository.ViolationRepository;
import kz.bdl.erapservice.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CameraRepository cameraRepository;
    private final ViolationRepository violationRepository;

    @Override
    public Event saveHikvisionEvent(HikvisionEventRequest request) {
        log.info("Saving Hikvision event with external ID: {}", request.getId());

        List<Camera> cameras = cameraRepository.findByIp(request.getCameraIp());
        if (cameras.isEmpty()) {
            throw new ResourceBadRequestException("Camera not found with IP: " + request.getCameraIp());
        }
        Camera camera = cameras.get(0);

        String imageUrl = request.getImage();
        String base64Image = "";
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URL url = new URL(imageUrl);
                InputStream inputStream = url.openStream();
                byte[] imageBytes = inputStream.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
                inputStream.close();
                log.info("Successfully downloaded and converted image to base64 from URL: {}", imageUrl);
            } catch (Exception e) {
                log.error("Failed to download image from URL: {}", imageUrl, e);
                throw new ResourceBadRequestException("Failed to download image from URL: " + imageUrl);
            }
        } else {
            throw new ResourceBadRequestException("Image URL is empty");
        }

        Event event = new Event();
        event.setExternalId(request.getId());
        event.setCamera(camera);
        event.setPlateNumber(request.getPlateNumber());
        event.setImage(base64Image);
        event.setCreatedAt(parseDateTime(request.getDate()));
        event.setSpeed(request.getSpeed());
        event.setSpeedLimit(request.getSpeedLimit());
        event.setRoadLane(request.getRoadLane());
        event.setCameraDirection(request.getCameraDirection());

        if (request.getViolationType() != null) {
            Violation violation = violationRepository.getViolationByHikCode(request.getViolationType());
            event.setViolation(violation);
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Successfully saved event with ID: {}", savedEvent.getId());
        
        return savedEvent;
    }

    private LocalDateTime parseDateTime(String dateString) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            log.warn("Failed to parse date: {}, using current time", dateString);
            return LocalDateTime.now();
        }
    }
}
