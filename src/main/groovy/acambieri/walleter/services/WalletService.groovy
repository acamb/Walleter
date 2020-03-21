package acambieri.walleter.services


import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ScheduledEvent
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.User
import acambieri.walleter.model.VO.VOScheduledEvent
import acambieri.walleter.model.Wallet
import acambieri.walleter.repository.ScheduledEventRepository
import acambieri.walleter.repository.ShareRequestRepository
import acambieri.walleter.repository.UserRepository
import acambieri.walleter.repository.WalletEventRepository
import acambieri.walleter.repository.WalletRepository
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import acambieri.walleter.model.WalletEvent

@Transactional
@Service
class WalletService {

    @Autowired
    UserRepository userRepository
    @Autowired
    WalletRepository walletRepository
    @Autowired
    WalletEventRepository eventRepository
    @Autowired
    ScheduledEventRepository scheduledEventRepository
    @Autowired
    ShareRequestRepository shareRequestRepository

    Wallet createNewWallet(Long userId, String description) {
        return createNewWallet(userId,description,0.0)
    }

    Wallet createNewWallet(Long userId,String description,BigDecimal balance) {
        def user=userRepository.findById(userId).get()
        def wallet = walletRepository.save(new Wallet(description:description,balance: balance,owner: user,dateCreated: new Date()))
        user.wallets << wallet
        userRepository.save(user)
        wallet
    }

    Wallet findWallet(Long walletId){
        walletRepository.findById(walletId).get()
    }

    Wallet addEventToWallet(Wallet wallet, WalletEvent event){
        if(wallet.balance+event.amount < 0) throw new NegativeBalanceException((wallet.balance-event.amount).toString())
        event.wallet=wallet
        wallet.events << eventRepository.save(event)
        wallet.balance += event.amount
        walletRepository.save(wallet)
    }

    Wallet addScheduledEventToWallet(Wallet wallet, ScheduledEvent event){
        event.wallet = wallet
        wallet.scheduledEvents << scheduledEventRepository.save(event)
        walletRepository.save(wallet)
    }

    ScheduledEvent updateScheduledEventToWallet(Wallet wallet, VOScheduledEvent event){
        ScheduledEvent dbEvent = scheduledEventRepository.findById(event.id).get()
        event.with {
            dbEvent.units=it.units
            dbEvent.enabled=it.enabled
            dbEvent.frequency=it.frequency
            dbEvent.amount=it.amount
            dbEvent.description=it.description
            dbEvent.nextFire=it.nextFire
        }
        scheduledEventRepository.save(dbEvent)
    }

    Wallet deleteScheduledEvent(Wallet wallet,Long eventId){
        ScheduledEvent dbEvent = scheduledEventRepository.findById(eventId).get()
        if(wallet.scheduledEvents.remove(dbEvent)) {
            scheduledEventRepository.delete(dbEvent)
            walletRepository.save(wallet)
        }
        else{
            return wallet
        }
    }

    Wallet applyRecurringEvent(ScheduledEvent event) {
        if(!event.enabled) return
        def wallet = walletRepository.findById(event.wallet.id).get()
        wallet.balance += event.amount
        event.lastFire=new Date()
        if(event.units > 0) {
            use(TimeCategory) {
                event.nextFire += event.units."${event.frequency.toString().toLowerCase()}s"
            }
        }
        else{
            event.enabled=false
        }
        eventRepository.save(event)
        walletRepository.save(wallet)
    }

    List<ScheduledEvent> getRecurringEventsToFire(){
        scheduledEventRepository.listEventsToFire()
    }

    ShareWalletRequest createShareRequest(User owner, Wallet wallet, User receiver){
        def shareRequest = shareRequestRepository.save(
                new ShareWalletRequest(status: RequestStatus.PENDING,wallet: wallet,receiver: receiver,owner:owner)
        )
        owner.createdShareRequests << shareRequest
        receiver.shareRequests << shareRequest
        wallet.shareRequests << shareRequest
        userRepository.save(owner)
        userRepository.save(receiver)
        walletRepository.save(wallet)
        shareRequest
    }

    ShareWalletRequest acceptShareRequest(Long requestId,String receiverUsername){
        def request = shareRequestRepository.findById(requestId).get()
        if(!shareRequestsChecks(request,receiverUsername)){
            return null
        }
        if(!request.receiver.sharedWallets?.contains(request.wallet)){
            Wallet wallet = findWallet(request.wallet.id)
            User user = userRepository.findByUsername(receiverUsername)
            wallet.sharers << user
            wallet = walletRepository.save wallet
            user.sharedWallets << wallet
            user = userRepository.save user
            request.status=RequestStatus.ACCEPTED
            shareRequestRepository.save request
        }
        else{
            request.status = RequestStatus.DUPLICATE
            shareRequestRepository.save request
        }
    }

    ShareWalletRequest rejectShareRequest(Long requestId,String username){
        def request = shareRequestRepository.findById(requestId).get()
        if(!shareRequestsChecks(request,username)){
            return null
        }
        request.status = RequestStatus.REJECTED
        shareRequestRepository.save request
    }



    void deleteShareGrant(User user, Long walletId){
        def wallet = walletRepository.findById(walletId).get()
        if(user.sharedWallets.contains(wallet)){
            user.sharedWallets.remove(wallet)
            wallet.sharers.remove(user)
            walletRepository.save(wallet)
        }
    }

    boolean shareRequestsChecks(ShareWalletRequest shareWalletRequest, String receiverUsername) {
        def dbRequest = shareRequestRepository.findById(shareWalletRequest.id).get()
        def user = userRepository.findByUsername(receiverUsername)
        if(shareWalletRequest != dbRequest) return false
        if(!user.getShareRequests().contains(dbRequest)) return false
        return true
    }

    Wallet removeEventFromWallet(Wallet wallet, long eventId) {
        WalletEvent event = eventRepository.findById(eventId).get()
        def dbWallet = walletRepository.findById(wallet.id).get()
        dbWallet.balance -= event.amount
        dbWallet.events.remove(event)
        eventRepository.delete(event)
        walletRepository.save(dbWallet)
    }
}