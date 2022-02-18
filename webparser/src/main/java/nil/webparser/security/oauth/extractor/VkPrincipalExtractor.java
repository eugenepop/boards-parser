package nil.webparser.security.oauth.extractor;

import nil.webparser.dao.UserRepository;
import nil.webparser.entity.Role;
import nil.webparser.entity.SocialMedia;
import nil.webparser.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class VkPrincipalExtractor implements PrincipalExtractor {

    private final UserRepository userRepository;

    @Autowired
    public VkPrincipalExtractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object extractPrincipal(Map<String, Object> map) {
        User user;

        List<LinkedHashMap> users = (List<LinkedHashMap>) map.get("response");
        LinkedHashMap<String, Object> vkUserDetails = users.get(0);

        if (userRepository.existsByEmail((String) map.get("email"))) {
            user = userRepository.findFirstByEmail((String) map.get("email"));
        } else {
            user = new User();
            user.setEmail((String) map.get("email"));
            user.setName((String) vkUserDetails.get("first_name"));
            user.setRoles(Collections.singleton(Role.ROLE_USER));
            user.setUsername((String) map.get("email"));
            user.setPassword(UUID.randomUUID().toString());
            user.setActive(true);

            SocialMedia socialMedia = new SocialMedia();
            socialMedia.setVkId((Integer) vkUserDetails.get("uid"));
            socialMedia.setVkBirthDate((String) vkUserDetails.get("bdate"));
            socialMedia.setVkSex((Integer) vkUserDetails.get("sex"));
            socialMedia.setVkPhoto((String) vkUserDetails.get("photo_max_orig"));
            socialMedia.setVkFriendStatus((Integer) vkUserDetails.get("friend_status"));
            socialMedia.setVkRelation((Integer) vkUserDetails.get("relation"));
            socialMedia.setVkRelationPartner((Integer) vkUserDetails.get("relation_partner"));

            userRepository.save(user);
        }

        return user;
    }
}
