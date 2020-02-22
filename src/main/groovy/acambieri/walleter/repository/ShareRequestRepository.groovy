package acambieri.walleter.repository

import acambieri.walleter.model.ShareWalletRequest
import org.springframework.data.repository.CrudRepository

interface ShareRequestRepository extends CrudRepository<ShareWalletRequest,Long>{

}