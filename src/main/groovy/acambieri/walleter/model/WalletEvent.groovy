package acambieri.walleter.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.ManyToOne
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
class WalletEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String description
    BigDecimal amount
    @ManyToOne
    Wallet wallet
    Date date

}
