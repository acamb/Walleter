package acambieri.walleter.controller

import acambieri.walleter.controller.requests.CreateEventRequest
import acambieri.walleter.controller.requests.DeleteEventRequest
import acambieri.walleter.model.VO.VOWallet
import acambieri.walleter.model.VO.VOWalletEvent
import acambieri.walleter.model.WalletEvent
import acambieri.walleter.services.UserService
import acambieri.walleter.services.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

@RestController
@RequestMapping("/event")
class EventController {

    @Autowired
    WalletService walletService
    @Autowired
    UserService userService
    @GetMapping
    def list(Long walletId,Principal principal){
        def user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(walletId)
        if(!wallet || wallet.owner.id != user.id || !user.sharedWallets.find({it.id == wallet.id})){
            return ResponseEntity.badRequest().build()
        }
        wallet.events.collect{new VOWalletEvent(it)}
    }

    @PostMapping
    def create(@RequestBody CreateEventRequest request, Principal principal){
        def user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(request.walletId)
        if(wallet in user.wallets || wallet in user.sharedWallets){
            new VOWallet(walletService.addEventToWallet(wallet,request.event))
        }
        else{
            ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    def delete(@RequestBody DeleteEventRequest request, Principal principal){
        def user = userService.getUser(principal.name)
        def wallet = walletService.findWallet(request.wallet.id)
        if(wallet in user.wallets || wallet in user.sharedWallets){
            new VOWallet(walletService.removeEventFromWallet(wallet,request.event.id))
        }
    }
}