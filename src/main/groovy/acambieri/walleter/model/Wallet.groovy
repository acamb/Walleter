package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
@Entity
class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String description
    BigDecimal balance
    @ManyToOne
    User owner
    Date dateCreated
    @OneToMany(mappedBy = "wallet")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Fetch(FetchMode.JOIN)
    Set<WalletEvent> events = new ArrayList<>()
    @OneToMany(mappedBy = "wallet")
    Set<RecurringEvent> recurringEvents = new ArrayList<>()
    @OneToMany(mappedBy = "wallet")
    Set<ShareWalletRequest> shareRequests = new ArrayList<>()
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    Set<User> sharers = new ArrayList<>()

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Wallet wallet = (Wallet) o

        if (balance != wallet.balance) return false
        if (description != wallet.description) return false
        if (id != wallet.id) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (balance != null ? balance.hashCode() : 0)
        return result
    }
}
