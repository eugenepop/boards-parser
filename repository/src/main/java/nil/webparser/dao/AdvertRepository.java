package nil.webparser.dao;

import nil.webparser.entity.Advert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertRepository extends JpaRepository<Advert, Long>, CrudRepository<Advert, Long> {
    Advert findFirstByLink(String link);

    @Query("SELECT DISTINCT adv FROM Advert adv ORDER BY adv.price ASC")
    Page<Advert> findWithPagination(Pageable pageableRequest);
}
