package acambieri.walleter.repository

import acambieri.walleter.model.User
import org.springframework.data.repository.CrudRepository

import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends CrudRepository<User,Long> {
    User findByUsernameAndEnabledIsTrue(String username);

    User findByUsername(String username);
}
