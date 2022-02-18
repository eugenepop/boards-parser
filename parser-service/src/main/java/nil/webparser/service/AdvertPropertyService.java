package nil.webparser.service;

import org.springframework.stereotype.Service;
import nil.webparser.dao.AdvertPropertyRepository;
import nil.webparser.entity.AdvertProperty;

import java.util.List;

@Service
public class AdvertPropertyService {
    private final AdvertPropertyRepository advertPropertyRepository;

    public AdvertPropertyService(AdvertPropertyRepository advertPropertyRepository) {
        this.advertPropertyRepository = advertPropertyRepository;
    }

    public List<AdvertProperty> getAll() {
        return advertPropertyRepository.findAll();
    }

    public AdvertProperty save(AdvertProperty advertProperty) {
        return advertPropertyRepository.save(advertProperty);
    }

    public void deleteAll() {
        advertPropertyRepository.deleteAll();
    }
}
