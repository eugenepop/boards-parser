package nil.webparser.comparator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsBrierlyRepository extends JpaRepository<PointsBrierlyRow, Long> {
}
