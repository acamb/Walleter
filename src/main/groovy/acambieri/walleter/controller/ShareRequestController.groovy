package acambieri.walleter.controller

import acambieri.walleter.controller.requests.CreateShareRequest
import acambieri.walleter.controller.requests.DeleteShareRequest
import acambieri.walleter.controller.requests.UpdateShareRequest
import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.VO.VOShareRequest
import acambieri.walleter.model.Wallet
import acambieri.walleter.services.UserService
import acambieri.walleter.services.WalletService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
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
@CompileStatic
@RequestMapping("/share")
class ShareRequestController {

    @Autowired
    WalletService walletService
    @Autowired
    UserService userService

    @GetMapping()
    def listSharedRequests(Principal principal){
        def user = userService.getUser(principal.name)
        return [ owned: user.createdShareRequests.collect{new VOShareRequest(it)},
                 received: user.shareRequests.findAll(
                         {!(it.status in [RequestStatus.REJECTED,RequestStatus.DUPLICATE,RequestStatus.DELETED])}
                 ).collect{new VOShareRequest(it)}
        ]
    }

    @PostMapping()
    def share(@RequestBody CreateShareRequest request, Principal principal){
        def wallet = walletService.findWallet(request.walletId)
        def user = userService.getUser(principal.name)
        if(wallet.owner != user){
            return ResponseEntity.badRequest().build()
        }
        def userToShare = userService.getUser(request.username)
        if(!userToShare){
            return ResponseEntity.badRequest().build()
        }
        new VOShareRequest(walletService.createShareRequest(user,wallet,userToShare))
    }

    @PutMapping()
    def shareRequest(@RequestBody UpdateShareRequest request, Principal principal) {
        def createdShareRequest
        if (request.status == RequestStatus.ACCEPTED) {
            createdShareRequest = walletService.acceptShareRequest(request.shareRequest.id,principal.name)
        }
        else{
            createdShareRequest = walletService.rejectShareRequest(request.shareRequest.id,principal.name)
        }
        if(!createdShareRequest){
            return ResponseEntity.badRequest().build()
        }
        new VOShareRequest(createdShareRequest)
    }

    @DeleteMapping()
    def deleteShareGrant(@RequestBody DeleteShareRequest request, Principal principal){
        def userWithShare = userService.getUser(request.username)
        def owner = userService.getUser(principal.name)
        def dbWallet = walletService.findWallet(request.wallet.id)
        if(userWithShare && dbWallet.owner == owner){
            walletService.deleteShareGrant(userWithShare,request.wallet.id)
        }
        listSharedRequests(principal)
    }

}
