package acambieri.walleter.model.VO

import acambieri.walleter.model.WalletEvent
class VOWalletEvent {
    Long id
    String description
    BigDecimal amount
    Date date;

    public VOWalletEvent(){

    }

    public VOWalletEvent(WalletEvent it){
        id = it.id
        description = it.description
        amount = it.amount
        this.date = it.date
    }
}
