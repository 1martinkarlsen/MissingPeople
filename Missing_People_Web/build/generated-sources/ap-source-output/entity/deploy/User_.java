package entity.deploy;

import entity.Missing;
import entity.Role;
import entity.SearchNews;
import entity.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-06-02T21:05:38")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Long> id;
    public static volatile ListAttribute<User, Missing> following;
    public static volatile SingularAttribute<User, Date> lastLogin;
    public static volatile SingularAttribute<User, Date> created;
    public static volatile SingularAttribute<User, String> email;
    public static volatile ListAttribute<User, SearchNews> news;
    public static volatile ListAttribute<User, Role> roles;
    public static volatile SingularAttribute<User, String> lastname;
    public static volatile ListAttribute<User, Missing> volunteering;
    public static volatile SingularAttribute<User, String> firstname;
    public static volatile SingularAttribute<User, Boolean> isBanned;
    public static volatile SingularAttribute<User, String> password;

}