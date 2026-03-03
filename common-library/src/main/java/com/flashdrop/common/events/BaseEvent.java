package com.flashdrop.common.events;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent {

    private String eventId;
    private Instant timestamp;

    public BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
