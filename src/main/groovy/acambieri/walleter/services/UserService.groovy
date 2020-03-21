package acambieri.walleter.services

import acambieri.walleter.model.User
import acambieri.walleter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Transactional
@Service
class UserService {

    @Autowired
    UserRepository userRepository;

    User getUser(String username){
        userRepository.findByUsername(username)
    }
}
