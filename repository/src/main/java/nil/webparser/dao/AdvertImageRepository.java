package nil.webparser.dao;

import nil.webparser.entity.AdvertImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertImageRepository extends JpaRepository<AdvertImage, Long> {

    List<AdvertImage> findAllByProcessedIsFalse();

}
