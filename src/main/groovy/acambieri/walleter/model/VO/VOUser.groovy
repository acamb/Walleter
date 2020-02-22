package acambieri.walleter.model.VO

import acambieri.walleter.model.User

class VOUser {

    Long id
    String username


    public VOUser(User user){
        id = user.id
        username = user.username
    }
}
