package lv.superchef.app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppUser {

    private long id;
    private String username;
    private String email;
    private String password; // this will be encrypted
    private String role;

    public AppUser(){

    }

}
