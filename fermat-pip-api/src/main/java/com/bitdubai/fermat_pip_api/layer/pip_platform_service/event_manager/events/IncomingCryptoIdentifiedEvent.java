package com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.events;

import com.bitdubai.fermat_api.layer.all_definition.event.PlatformEvent;
import com.bitdubai.fermat_api.layer.all_definition.event.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.event.EventType;

/**
 * Created by loui on 20/02/15.
 */
public class IncomingCryptoIdentifiedEvent implements PlatformEvent {
    private EventType eventType;
    private EventSource eventSource;

    public IncomingCryptoIdentifiedEvent(EventType eventType){
        this.eventType = eventType;
    }

    @Override
    public EventType getEventType() {
        return this.eventType;
    }

    @Override
    public void setSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }

    @Override
    public EventSource getSource() {
        return this.eventSource;
    }
}