package acambieri.walleter.config

import acambieri.walleter.model.User
import acambieri.walleter.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
class TestEventListener {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder

    @EventListener
    @Transactional
    @Profile("development")
    void onApplicationEvent(ContextRefreshedEvent event){
        userRepository.save(new User(username: "test",password:passwordEncoder.encode("testme")))

    }
}
