package nil.webparser.dao;

import nil.webparser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String email);

    User findByName(String name);

    User findByEmail(String googleUsername);

    @Query("select count(u)>0 from User u where email = ?1")
    boolean existsByEmail(String email);

    User findFirstByEmail(String email);
}