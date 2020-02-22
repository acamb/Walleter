package acambieri.walleter.model

import acambieri.walleter.model.Frequency
import acambieri.walleter.model.Wallet

import javax.persistence.Entity
import javax.persistence.ManyToOne
@Entity
class RecurringEvent  extends WalletEvent{

    Long id
    Frequency frequency
    Integer units
    Date dateCreated
    Date nextFire
    Boolean enabled
    @ManyToOne
    Wallet wallet

}
