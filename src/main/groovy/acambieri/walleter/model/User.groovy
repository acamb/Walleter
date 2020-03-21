package acambieri.walleter.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
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
import javax.persistence.OneToMany
@Entity
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
    Set<Wallet> wallets  = new ArrayList<>()
    @OneToMany(mappedBy="receiver")
    @LazyCollection(LazyCollectionOption.FALSE)
    Set<ShareWalletRequest> shareRequests = new ArrayList<>()
    @OneToMany(mappedBy="owner")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Fetch(FetchMode.JOIN)
    Set<ShareWalletRequest> createdShareRequests = new ArrayList<>()
    @ManyToMany(mappedBy = "sharers")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Fetch(FetchMode.JOIN)
    Set<Wallet> sharedWallets = new ArrayList<>()
    @ManyToMany
    Set<Role> roles = new ArrayList<>()

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        User user = (User) o

        if (enabled != user.enabled) return false
        if (id != user.id) return false
        if (password != user.password) return false
        if (username != user.username) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0)
        return result
    }
}
