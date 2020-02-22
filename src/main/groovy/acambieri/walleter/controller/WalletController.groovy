package acambieri.walleter.controller

import acambieri.walleter.model.RequestStatus
import acambieri.walleter.model.ShareWalletRequest
import acambieri.walleter.model.User
import acambieri.walleter.model.VO.VOWallet
import acambieri.walleter.model.Wallet
import acambieri.walleter.services.UserService
import acambieri.walleter.services.WalletService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

import java.security.Principal

@RestController
@RequestMapping("/wallet")
class WalletController {

    @Autowired
    UserService userService;
    @Autowired
    WalletService walletService;

    @PostMapping("/create")
    VOWallet save(String description, BigDecimal amount, Principal principal){
        if(amount == null) amount = 0
        def user = userService.getUser(principal.name)
        def wallet=walletService.createNewWallet(user.id,description,amount)
        new VOWallet(wallet)
    }

    @GetMapping("/list")
    List<VOWallet> list(Principal principal){
        def user = userService.getUser(principal.name)
        def wallets = [*user.wallets.toList(),*user.sharedWallets.toList()]
        wallets.collect {new VOWallet(it)}
    }

}
