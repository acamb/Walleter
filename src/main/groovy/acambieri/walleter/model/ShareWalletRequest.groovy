package acambieri.walleter.model

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

}
