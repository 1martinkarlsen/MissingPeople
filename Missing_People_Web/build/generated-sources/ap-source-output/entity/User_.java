package entity;

import entity.Role;
import entity.Search;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-04-21T11:53:09")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, Date> lastLogin;
    public static volatile ListAttribute<User, Search> following;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, Date> created;
    public static volatile ListAttribute<User, Role> roles;
    public static volatile SingularAttribute<User, String> lastname;
    public static volatile SingularAttribute<User, String> firstname;
    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, Boolean> isBanned;

}