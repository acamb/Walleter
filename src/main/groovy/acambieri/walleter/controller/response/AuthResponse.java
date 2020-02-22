package acambieri.walleter.controller.response;

import acambieri.walleter.model.VO.VOUser;

import java.io.Serializable;

public class AuthResponse implements Serializable {

    private String token;

    private VOUser user;

    public VOUser getUser() {
        return user;
    }

    public void setUser(VOUser user) {
        this.user = user;
    }

    public AuthResponse(String token, VOUser user){
        this.token=token;
        this.user=user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
