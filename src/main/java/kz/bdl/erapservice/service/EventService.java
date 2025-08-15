package kz.bdl.erapservice.service;

import kz.bdl.erapservice.dto.HikvisionEventRequest;
import kz.bdl.erapservice.entity.Event;

public interface EventService {
    Event saveHikvisionEvent(HikvisionEventRequest request);
}
