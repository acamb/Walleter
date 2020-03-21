package acambieri.walleter.controller.requests

import acambieri.walleter.model.VO.VOScheduledEvent

class CreateScheduledEventRequest {
    Long walletId;
    VOScheduledEvent event;
}
