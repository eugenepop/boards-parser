package nil.webparser.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import nil.webparser.dao.AdvertRepository;
import nil.webparser.entity.Advert;
import nil.webparser.entity.AdvertProperty;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class AdvertService {
    @PersistenceContext
    private EntityManager entityManager;

    private AdvertRepository advertRepository;

    public AdvertService(AdvertRepository advertRepository) {
        this.advertRepository = advertRepository;
    }

    public List<Advert> getAll() {
        return advertRepository.findAll();
    }

    public void deleteAll() {
        advertRepository.deleteAll();
    }

    public boolean existByLink(String link) {
        return advertRepository.findFirstByLink(link) != null;
    }

    public void save(Advert advert) {
        try {
            advertRepository.save(advert);
        } catch (JpaSystemException jse) {
            if (jse.getCause() != null && jse.getCause() instanceof GenericJDBCException) {
                GenericJDBCException ge = (GenericJDBCException) jse.getCause();
                if (ge.getCause() != null && ge.getCause() instanceof SQLException) {
                    advert.setDescription(EmojiParser.removeAllEmojis(advert.getDescription()));
                    advertRepository.save(advert);
                } else {
                    throw ge;
                }
            } else {
                throw jse;
            }
        }
    }

    public List<Advert> getAdverts(int page, int size) {
        Pageable pageableRequest = PageRequest.of(page, size);
        log.info("{} is done", Thread.currentThread());
        return advertRepository.findWithPagination(pageableRequest).getContent();
    }

    Function<Map.Entry<Long, List<Advert>>, Advert> collapseAdverts = e -> {
        List<AdvertProperty> advertProperties = e.getValue().stream()
                .map(adv -> adv.getProperties().iterator().next())
                .collect(Collectors.toList());
        Advert advertWithProps = e.getValue().iterator().next();
        advertWithProps.setProperties(advertProperties);
        return advertWithProps;
    };

    //@Scheduled(fixedDelayString = "1000000")
    public void getAdvertLightVersion() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Advert> query = builder.createQuery(Advert.class);

        Root<Advert> advert = query.from(Advert.class);
        Join<Advert, AdvertProperty> advertProperty = advert.join("properties", JoinType.INNER);
        query.multiselect(advert.get("id"), advert.get("title"), advertProperty.get("value"));

        TypedQuery<Advert> typedQuery = entityManager.createQuery(query);
        List<Advert> resultList = typedQuery.getResultList().stream()
                .collect(Collectors.groupingBy(Advert::getId))
                .entrySet().stream()
                .map(e -> e.getValue().stream()
                        .reduce((initAdv, nextAdv) -> {
                            initAdv.getProperties().addAll(nextAdv.getProperties());
                            return initAdv;
                        }).get())
                .collect(Collectors.toList());

        log.info(resultList.toString());

        /*Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(Advert.class, "advert");
        criteria.setFetchMode("advert.properties", FetchMode.JOIN);
        criteria.createAlias("advert.properties", "properties"); // inner join by default

        ProjectionList columns = Projections.projectionList()
                .add(Projections.property("title"))
                .add(Projections.property("properties.value"));
        criteria.setProjection(columns);

        List<Object[]> list = criteria.list();
        for(Object[] arr : list){
            System.out.println(Arrays.toString(arr));
        }*/
    }
}
