package nil.webparser.security.oauth.extractor;

import nil.webparser.dao.UserRepository;
import nil.webparser.entity.Role;
import nil.webparser.entity.SocialMedia;
import nil.webparser.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
public class GooglePrincipalExtractor implements PrincipalExtractor {

    private final UserRepository userRepository;

    @Autowired
    public GooglePrincipalExtractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        User user;

        if (userRepository.existsByEmail((String) map.get("email"))) {
            user = userRepository.findFirstByEmail((String) map.get("email"));
        } else {
            user = new User();
            user.setEmail((String) map.get("email"));
            user.setUsername((String) map.get("email"));
            user.setName((String) map.get("given_name"));
            user.setRoles(Collections.singleton(Role.ROLE_USER));
            user.setPassword(UUID.randomUUID().toString());
            user.setActive(true);

            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setGoogleId((String) map.get("sub"));
            socialMedia.setGooglePhoto((String) map.get("picture"));
            //user.setSocialMedia(socialMedia);

            userRepository.save(user);
        }

        return user;
    }
}
