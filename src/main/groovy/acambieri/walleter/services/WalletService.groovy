package acambieri.walleter.services

import acambieri.walleter.model.RecurringEvent
import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.User
import acambieri.walleter.model.Wallet
import acambieri.walleter.repository.RecurringEventRepository
import acambieri.walleter.repository.ShareRequestRepository
import acambieri.walleter.repository.UserRepository
import acambieri.walleter.repository.WalletEventRepository
import acambieri.walleter.repository.WalletRepository
import groovy.time.TimeCategory
import groovy.transform.CompileStatic
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
    RecurringEventRepository recurringEventRepository
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

    User findUser(String username){
        userRepository.findByUsername(username)
    }

    Wallet addEventToWallet(Wallet wallet,WalletEvent event){
        if(wallet.balance+event.amount < 0) throw new NegativeBalanceException((wallet.balance-event.amount).toString())
        wallet.events << eventRepository.save(event)
        wallet.balance += event.amount
        walletRepository.save(wallet)
    }

    Wallet addRecurringEventToWallet(Wallet wallet, RecurringEvent event){
        event.wallet = wallet
        wallet.recurringEvents << recurringEventRepository.save(event)
        walletRepository.save(wallet)
    }

    Wallet applyRecurringEvent(RecurringEvent event) {
        if(!event.enabled) return
        def wallet = walletRepository.findById(event.wallet.id).get()
        wallet.balance += event.amount
        use(TimeCategory) {
            event.nextFire += event.units."${event.frequency.toString().toLowerCase()}s"
        }
        eventRepository.save(event)
        walletRepository.save(wallet)
    }

    List<RecurringEvent> getRecurringEventsToFire(){
        recurringEventRepository.listEventsToFire()
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

    ShareWalletRequest acceptShareRequest(ShareWalletRequest request,String receiverUsername){
        if(!shareRequestsChecks(request,receiverUsername)){
            return null
        }
        if(!request.receiver.sharedWallets?.contains(request.wallet)){
            Wallet wallet = findWallet(request.wallet.id)
            User user = findUser(receiverUsername)
            wallet.sharers << user
            user.sharedWallets << wallet
            request.status=RequestStatus.ACCEPTED
            userRepository.save user
            walletRepository.save wallet
            request=shareRequestRepository.save request
            request
        }
        else{
            request.status = RequestStatus.DUPLICATE
            shareRequestRepository.save request
        }
    }

    ShareWalletRequest rejectShareRequest(ShareWalletRequest request,String username){
        if(!shareRequestsChecks(request,username)){
            return null
        }
        request.status = RequestStatus.REJECTED
        shareRequestRepository.save request
    }



    void deleteShareGrant(User user, Wallet wallet){
        if(user.sharedWallets.contains(wallet)){
            user.sharedWallets.remove(wallet)
            wallet.sharers.remove(user)
            userRepository.save(user)
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
}