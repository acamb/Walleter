package acambieri.walleter.controller

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

import java.security.Principal

@Controller
@CompileStatic
class ShareRequestController {

    @Autowired
    WalletService walletService
    @Autowired
    UserService userService

    @GetMapping("/share")
    def listSharedRequests(Principal principal){
        def user = userService.getUser(principal.name)
        return [ owned: user.createdShareRequests.collect{new VOShareRequest(it)},
                 received: user.shareRequests.findAll(
                         {!(it.status in [RequestStatus.REJECTED,RequestStatus.DUPLICATE,RequestStatus.DELETED])}
                 ).collect{new VOShareRequest(it)}
        ]
    }

    @PostMapping("/share")
    def share(Long walletId, String username, Principal principal){
        def wallet = walletService.findWallet(walletId)
        def user = userService.getUser(principal.name)
        if(wallet.owner != user){
            return ResponseEntity.badRequest().build()
        }
        def userToShare = walletService.findUser(username)
        if(!userToShare){
            return ResponseEntity.badRequest().build()
        }
        new VOShareRequest(walletService.createShareRequest(user,wallet,userToShare))
    }

    @PutMapping("/share")
    def shareRequest(ShareWalletRequest shareRequest, RequestStatus status,Principal principal) {
        def createdShareRequest
        if (status == RequestStatus.ACCEPTED) {
            createdShareRequest = walletService.acceptShareRequest(shareRequest,principal.name)
        }
        else{
            createdShareRequest = walletService.rejectShareRequest(shareRequest,principal.name)
        }
        if(!createdShareRequest){
            return ResponseEntity.badRequest().build()
        }
        new VOShareRequest(createdShareRequest)
    }

    @DeleteMapping("/share")
    def deleteShareGrant(Wallet wallet, String username, Principal principal){
        def userWithShare = walletService.findUser(username)
        def owner = walletService.findUser(principal.name)
        def dbWallet = walletService.findWallet(wallet.id)
        if(userWithShare && dbWallet.owner == owner){
            walletService.deleteShareGrant(userWithShare,wallet)
        }
        listSharedRequests(principal)
    }

}
