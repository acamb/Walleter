package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
@Entity
@EqualsAndHashCode
class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String description
    BigDecimal balance
    @ManyToOne
    User owner
    Date dateCreated
    @OneToMany(fetch = FetchType.EAGER)
    List<WalletEvent> events = new ArrayList<>()
    @OneToMany
    List<RecurringEvent> recurringEvents = new ArrayList<>()
    @OneToMany
    List<ShareWalletRequest> shareRequests = new ArrayList<>()
    @OneToMany
    List<User> sharers = new ArrayList<>()

}
