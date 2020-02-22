package acambieri.walleter.model.VO

import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest

class VOShareRequest {

    Long id
    RequestStatus status
    String owner
    String receiver
    VOWallet wallet

    public VOShareRequest(){
    }

    public VOShareRequest(ShareWalletRequest it){
        id = it.id
        status = it.status
        owner = it.owner.username
        receiver = it.receiver.username
        wallet = new VOWallet(it.wallet)
    }
}
