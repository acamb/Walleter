package acambieri.walleter.controller.requests

import acambieri.walleter.model.VO.VOWallet
import acambieri.walleter.model.VO.VOWalletEvent

class DeleteEventRequest {
    VOWalletEvent event
    VOWallet wallet
}
