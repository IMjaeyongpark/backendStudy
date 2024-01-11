package jaeyong.Test2.Domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {

    @Id
    private String ID;
    private String User_Name;
    private String User_Password;

}
