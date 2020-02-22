package acambieri.walleter.repository

import acambieri.walleter.model.Wallet
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository extends CrudRepository<Wallet,Long> {

}