package nil.webparser.dao;

import nil.webparser.entity.AdvertProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertPropertyRepository extends JpaRepository<AdvertProperty, Long>, CrudRepository<AdvertProperty, Long> {
}
