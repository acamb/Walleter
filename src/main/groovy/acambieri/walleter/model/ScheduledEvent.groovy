package acambieri.walleter.model

import acambieri.walleter.model.Frequency
import acambieri.walleter.model.Wallet

import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
class ScheduledEvent extends WalletEvent{

    Frequency frequency
    Integer units
    Date dateCreated
    Date lastFire
    @Temporal(TemporalType.DATE)
    Date nextFire
    Boolean enabled
    @ManyToOne
    Wallet wallet

}
