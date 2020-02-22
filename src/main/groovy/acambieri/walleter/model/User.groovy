package acambieri.walleter.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String username
    String password
    Boolean enabled = true
    @OneToMany(mappedBy="owner")
    List<Wallet> wallets
    @OneToMany(mappedBy="receiver")
    List<ShareWalletRequest> shareRequests
    @OneToMany(mappedBy="owner")
    List<ShareWalletRequest> createdShareRequests
    @OneToMany(mappedBy="sharers")
    List<Wallet> sharedWallets
    @ManyToMany
    List<Role> roles

}
