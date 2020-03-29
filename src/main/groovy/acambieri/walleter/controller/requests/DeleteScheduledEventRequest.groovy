package acambieri.walleter.controller.requests

import acambieri.walleter.model.VO.VOWallet
import acambieri.walleter.model.VO.VOWalletEvent

class DeleteScheduledEventRequest {
    VOWalletEvent event
    VOWallet wallet
}
