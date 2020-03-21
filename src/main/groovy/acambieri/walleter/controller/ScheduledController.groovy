package acambieri.walleter.controller

import acambieri.walleter.controller.requests.CreateScheduledEventRequest
import acambieri.walleter.controller.requests.DeleteScheduledEventRequest
import acambieri.walleter.controller.requests.UpdateScheduledEventRequest
import acambieri.walleter.model.ScheduledEvent
import acambieri.walleter.model.User
import acambieri.walleter.model.VO.VOScheduledEvent
import acambieri.walleter.model.VO.VOWallet
import acambieri.walleter.model.Wallet
import acambieri.walleter.services.UserService
import acambieri.walleter.services.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@RestController
@RequestMapping("/scheduled")
class ScheduledController {

    @Autowired
    WalletService walletService
    @Autowired
    UserService userService

    @GetMapping
    List<ScheduledEvent> getEvents(Long walletId,Principal principal){
        User user = userService.getUser(principal.name)
        [ *user.wallets.toList(),*user.sharedWallets.toList()].find({it.id == walletId})?.scheduledEvents
    }

    @PostMapping
    List<ScheduledEvent> create(CreateScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        Wallet wallet= [*user.wallets.toList(), *user.sharedWallets.toList()].find({it.id == request.walletId})
        if( wallet != null){
            request.event.with {
                walletService.addScheduledEventToWallet(
                        wallet,
                        new ScheduledEvent(
                                frequency: it.frequency,
                                wallet: wallet,
                                enabled: it.enabled,
                                nextFire: it.nextFire,
                                units:it.units,
                                dateCreated: new Date()
                                )
                ).scheduledEvents
            }
        }
    }

    @PutMapping
    VOScheduledEvent update(UpdateScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        Wallet wallet= [*user.wallets.toList(), *user.sharedWallets.toList()].find({it.id == request.walletId})
        if(wallet != null && wallet.scheduledEvents.find({it.id == request.event.id}) != null){
            new VOScheduledEvent(walletService.updateScheduledEventToWallet(wallet,request.event))
        }
    }

    @DeleteMapping
    VOWallet delete(DeleteScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        Wallet wallet= [*user.wallets.toList(), *user.sharedWallets.toList()].find({it.id == request.walletId})
        if(wallet != null){
            new VOWallet(walletService.deleteScheduledEvent(wallet,request.eventId))
        }
    }
}