package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
@Entity
@EqualsAndHashCode
@ToString
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    String username
    String password
    Boolean enabled = true
    @OneToMany(mappedBy="owner")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Wallet> wallets  = new ArrayList<>()
    @OneToMany(mappedBy="receiver")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<ShareWalletRequest> shareRequests = new ArrayList<>()
    @OneToMany(mappedBy="owner")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<ShareWalletRequest> createdShareRequests = new ArrayList<>()
    @OneToMany(mappedBy="sharers")
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Wallet> sharedWallets = new ArrayList<>()
    @ManyToMany
    List<Role> roles = new ArrayList<>()

}
