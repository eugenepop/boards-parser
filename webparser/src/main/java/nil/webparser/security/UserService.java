package nil.webparser.security;

import nil.webparser.dao.UserRepository;
import nil.webparser.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFindByGoogleUsername = userRepository.findByEmail(username);

        if (userFindByGoogleUsername != null) {
            return userFindByGoogleUsername;
        }

        return null;
    }
}
