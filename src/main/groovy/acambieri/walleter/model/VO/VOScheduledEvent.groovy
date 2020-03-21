package acambieri.walleter.model.VO

import acambieri.walleter.model.Frequency
import acambieri.walleter.model.ScheduledEvent
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class VOScheduledEvent extends VOWalletEvent {
    Frequency frequency
    Integer units
    Date dateCreated
    Date lastFire
    Date nextFire
    Boolean enabled

    public VOScheduledEvent(ScheduledEvent it){
        this.id = it.id
        this.description = it.description
        this.amount = it.amount
        this.date = it.date
        this.frequency=it.frequency
        this.units=it.units
        this.dateCreated = it.dateCreated
        this.lastFire= it.lastFire
        this.nextFire = it.nextFire
        this.enabled = it.enabled
    }
}
