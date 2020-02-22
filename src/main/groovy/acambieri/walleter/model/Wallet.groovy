package acambieri.walleter.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
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
    @OneToMany
    List<WalletEvent> events
    @OneToMany
    List<RecurringEvent> recurringEvents
    @OneToMany
    List<ShareWalletRequest> shareRequests
    @OneToMany
    List<User> sharers

}
