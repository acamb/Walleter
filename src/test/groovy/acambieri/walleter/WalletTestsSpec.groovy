package acambieri.walleter

import acambieri.walleter.model.User
import acambieri.walleter.model.Wallet
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import acambieri.walleter.model.WalletEvent
@DataJpaTest
class WalletTestsSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "create a user, attach a wallet and a event"() {
        given: "a user"
            def user = new User(username: 'test',password: 'testme123')
            user.save(failOnError:true)
        when: "a wallet with an event is attached"
            def wallet = new Wallet(balance: 0)
            user.addToWallets(wallet)
            def event = new WalletEvent(description: 'grocery',amount: 12)
            wallet.addToEvents(event)
        then: "the user has a wallet with an event inside"
            User.get(user.id).wallets.size() == 1
            User.get(user.id).wallets[0].events.size() == 1
    }


}
