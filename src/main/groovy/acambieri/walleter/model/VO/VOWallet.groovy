package acambieri.walleter.model.VO

import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.Wallet

class VOWallet {
    Long id
    String description
    BigDecimal balance
    Date dateCreated
    List<ShareWalletRequest> shareRequest

    public VOWallet(){

    }

    public VOWallet(Wallet it){
        id=it.id
        description=it.description
        balance = it.balance
        dateCreated = it.dateCreated
        shareRequest = it.shareRequests
    }
}
