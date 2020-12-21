package labs.spring.hw7.services;

import labs.spring.hw7.entities.Roles;
import labs.spring.hw7.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    Users createUser(Users user);
    Users saveUser(Users user);
    List<Users> getAllUsers();
    List<Roles> getAllRoles();
    Roles getRole(Long id);
    Roles addRole(Roles role);
    Roles saveRole(Roles role);

}
