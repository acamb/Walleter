package acambieri.walleter.controller.requests

import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.VO.VOShareRequest

class UpdateShareRequest {
    VOShareRequest shareRequest
    RequestStatus status
}
