package acambieri.walleter

import acambieri.walleter.model.Frequency
import acambieri.walleter.model.RecurringEvent
import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.Wallet
import acambieri.walleter.model.WalletEvent
import acambieri.walleter.model.User
import acambieri.walleter.repository.UserRepository
import acambieri.walleter.services.WalletService
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import spock.lang.AutoCleanup
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
//@CompileStatic
class WalletServiceSpec extends Specification {

    @Autowired
    WalletService service
    @Autowired
    UserRepository userRepository
    @Autowired
    EntityManager entityManager

    def setup() {
    }

    def cleanup() {
    }

    void "create a wallet and add an event"() {
        given: "a user with a wallet"
        def user = createUserIfDoesntExists("pippo")
        def wallet = service.createNewWallet(user.id, "test wallet",new BigDecimal(100))
        user = service.findUser(user.username)
        when: "an event is added to a wallet"
        def walletEvent = new WalletEvent(amount: amount, description: "withdraw")
        wallet = service.addEventToWallet(user.wallets[0], walletEvent)
        then: "the balance is changed and the event is attached to the wallet"
        wallet.events.size() == walletsize
        wallet.balance == expected
        where:
        amount | expected | walletsize
        10   | 110 | 1
        -20  | 90  | 2
    }

    void "create a wallet and add an event that is greater than the balance"() {
        given: "a user with a wallet"
        def user = createUserIfDoesntExists("pippo")
        def wallet = service.createNewWallet(user.id, "test wallet")
        wallet.balance = 100
        when: "an event is added to a wallet"
        def walletEvent = new WalletEvent(amount: -110, description: "withdraw")
        wallet = service.addEventToWallet(user.wallets[0], walletEvent)
        then: "an exception is thrown"
        thrown(Exception)
    }

    void "apply a recurring event to a wallet"(){
        given: "a user, with a wallet and a recurring event"
        def user = createUserIfDoesntExists("pippo")
        def wallet = service.createNewWallet(user.id,"test wallet",100.0)
        def fireDate=new Date()
        RecurringEvent recurringEvent = new RecurringEvent(frequency: Frequency.DAY,
                units: 2,
                amount: -20,
                enabled: true,
                nextFire: fireDate,
                description: "test recurring event"
        )
        wallet = service.addRecurringEventToWallet(wallet,recurringEvent)
        when: "the recurring event is processed"
        service.applyRecurringEvent(recurringEvent)
        wallet = service.findWallet(wallet.id)
        recurringEvent = service.recurringEventRepository.findById(recurringEvent.id).get()
        then: "the balance is changed and the nextFire property of the event is updated"
        wallet.balance == 80
        recurringEvent.nextFire == TimeCategory.getDays(2) + fireDate
    }

    void "get recurring events to fire"(){
        given: "a wallet with some recurring events"
        def wallet = service.createNewWallet(createUserIfDoesntExists("pippo").id,"test wallet",100.0)
        use(TimeCategory){
            def recurringEventInThePast = new RecurringEvent(frequency: Frequency.DAY,units: 2,amount: -20,enabled: true,nextFire: new Date() - 1.days,description: "test recurring event")
            def recurringEventInTheFuture = new RecurringEvent(frequency: Frequency.DAY,units: 2,amount: -20,enabled: true,nextFire: new Date() + 1.days,description: "test recurring event")
            def recurringEventInThePastDisabled = new RecurringEvent(frequency: Frequency.DAY,units: 2,amount: -20,enabled: false,nextFire: new Date() - 1.hours,description: "test recurring event")
            wallet=service.addRecurringEventToWallet(wallet,recurringEventInThePast)
            wallet=service.addRecurringEventToWallet(wallet,recurringEventInThePastDisabled)
            wallet=service.addRecurringEventToWallet(wallet,recurringEventInTheFuture)
        }
        when: "get the events to fire"
        List<RecurringEvent> events = service.getRecurringEventsToFire()
        then: "only enabled events in the past are retrieved"
        events.size() == 1
    }

    User createUserIfDoesntExists(String username){
        User user = userRepository.findByUsername(username);
        if(user == null){
            user = userRepository.save(new User(username:  username,password:  "12345678910"))
        }
        return user;
    }

    void "create a share request"(){
        given: "two users and a wallet to share"
        def user1 = createUserIfDoesntExists("pippo")
        def user2 = createUserIfDoesntExists("pippo2")
        def wallet = service.createNewWallet(user1.id,"a wallet",100.0)
        when: "a share request is created"
        service.createShareRequest(user1,wallet,user2)
        user1 = service.findUser(user1.username)
        user2 = service.findUser(user2.username)
        then: "the share request is attached to the wallet"
        user1.createdShareRequests.size() == 1
        user1.createdShareRequests[0].owner.username == user1.username
        user1.createdShareRequests[0].receiver.username == user2.username
        user1.createdShareRequests[0].wallet.id == wallet.id
        user1.createdShareRequests[0].status == RequestStatus.PENDING
        user2.shareRequests.size() == 1
    }

    void "accept share request and delete the share"(){
        given: "two users and a share request"
        def user1 = createUserIfDoesntExists("pippo")
        def user2 = createUserIfDoesntExists("pippo2")
        def wallet = service.createNewWallet(user1.id,"a wallet",100.0)
        service.createShareRequest(user1,wallet,user2)
        when: "the share request is accepted"
        user2 = service.findUser(user2.username)
        ShareWalletRequest shareRequest = user2.shareRequests[0]
        service.acceptShareRequest(shareRequest,user2.username)
        entityManager.clear()
        user2 = service.findUser(user2.username)
        shareRequest = service.shareRequestRepository.findById(shareRequest.id).get()
        wallet = service.findWallet(wallet.id)
        then: "user2 has the wallet shared and the request is accepted"
        user2.sharedWallets.size() == 1
        user2.sharedWallets[0].owner.id == user1.id
        shareRequest.status == RequestStatus.ACCEPTED
        wallet.sharers.size() == 1
        wallet.sharers[0].id == user2.id
        when: "another request is made"
        service.acceptShareRequest(shareRequest,user2.username)
        shareRequest = service.shareRequestRepository.findById(shareRequest.id).get()
        wallet = service.findWallet(wallet.id)
        then: " is marked as duplicate"
        shareRequest.status == RequestStatus.DUPLICATE
        wallet.sharers.size() == 1
        when: "a share is deleted"
        service.deleteShareGrant(user2,wallet)
        user2 = service.findUser(user2.username)
        wallet = service.findWallet(wallet.id)
        then: "the share is removed from the user"
        user2.sharedWallets.size() == 0
        wallet.sharers.size() == 0
    }
}
