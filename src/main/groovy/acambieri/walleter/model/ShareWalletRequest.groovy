package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
@Entity
class ShareWalletRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    RequestStatus status

    @ManyToOne
    User owner

    @ManyToOne
    User receiver

    @ManyToOne
    Wallet wallet

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ShareWalletRequest that = (ShareWalletRequest) o

        if (id != that.id) return false
        if (owner.id != that.owner.id) return false
        if (receiver.id != that.receiver.id) return false
        if (status != that.status) return false
        if (wallet.id != that.wallet.id) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + receiver.hashCode()
        result = 31 * result + wallet.hashCode()
        return result
    }
}
