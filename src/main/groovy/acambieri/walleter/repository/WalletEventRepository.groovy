package acambieri.walleter.repository

import org.springframework.data.repository.CrudRepository
import acambieri.walleter.model.WalletEvent

interface WalletEventRepository extends CrudRepository<WalletEvent,Long> {

}