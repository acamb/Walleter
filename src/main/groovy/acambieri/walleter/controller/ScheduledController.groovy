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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    List<VOScheduledEvent> getEvents(@RequestParam Long walletId, Principal principal){
        User user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(walletId)
        if(!wallet || (wallet.owner.id != user.id && !user.sharedWallets.find({it.id == wallet.id}))){
            return ResponseEntity.badRequest().build()
        }
        wallet.scheduledEvents
                .collect{new VOScheduledEvent(it as ScheduledEvent)}
                .sort{a,b -> (b.date<=>a.date) }
    }

    @PostMapping
    List<VOScheduledEvent> create(@RequestBody CreateScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(request.walletId)
        if(!wallet || (wallet.owner.id != user.id && !user.sharedWallets.find({it.id == wallet.id}))){
            return ResponseEntity.badRequest().build()
        }
        if( wallet != null){
            request.event.with {
                walletService.addScheduledEventToWallet(
                        wallet,
                        new ScheduledEvent(
                                frequency: it.frequency,
                                amount: it.amount,
                                description: it.description,
                                wallet: wallet,
                                enabled: it.enabled ?: true,
                                nextFire: it.nextFire,
                                units:it.units,
                                dateCreated: new Date()
                                )
                ).scheduledEvents
                        .toList()
                        .collect{new VOScheduledEvent(it as ScheduledEvent)}
            } as List<VOScheduledEvent>
        }
    }

    @PutMapping
    VOScheduledEvent update(@RequestBody UpdateScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(request.walletId)
        if(!wallet || (wallet.owner.id != user.id && !user.sharedWallets.find({it.id == wallet.id}))){
            return ResponseEntity.badRequest().build()
        }
        if(wallet != null && wallet.scheduledEvents.find({it.id == request.event.id}) != null){
            new VOScheduledEvent(walletService.updateScheduledEventToWallet(wallet,request.event))
        }
    }

    @DeleteMapping
    VOWallet delete(@RequestBody DeleteScheduledEventRequest request, Principal principal){
        User user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(request.wallet.id)
        if(!wallet || (wallet.owner.id != user.id && !user.sharedWallets.find({it.id == wallet.id}))){
            return ResponseEntity.badRequest().build()
        }
        if(wallet != null){
            new VOWallet(walletService.deleteScheduledEvent(wallet,request.event.id))
        }
    }
}