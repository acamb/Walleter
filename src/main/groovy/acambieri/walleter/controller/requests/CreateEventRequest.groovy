package acambieri.walleter.controller.requests

import acambieri.walleter.model.VO.VOWalletEvent
import acambieri.walleter.model.WalletEvent
class CreateEventRequest {

    VOWalletEvent event
    Long walletId

}
