package acambieri.walleter.model

import acambieri.walleter.model.Frequency
import acambieri.walleter.model.Wallet

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
class ScheduledEvent extends WalletEvent{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    Frequency frequency
    Integer units
    Date dateCreated
    Date lastFire
    @Temporal(TemporalType.DATE)
    Date nextFire
    Boolean enabled
    @ManyToOne
    Wallet wallet

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ScheduledEvent that = (ScheduledEvent) o
        if(id != that.id) return false
        if (dateCreated != that.dateCreated) return false
        if (enabled != that.enabled) return false
        if (frequency != that.frequency) return false
        if (units != that.units) return false

        return true
    }

    int hashCode() {
        int result
        result = (frequency != null ? frequency.hashCode() : 0)
        result = 31 * result + (units != null ? units.hashCode() : 0)
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0)
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0)
        return result
    }
}
