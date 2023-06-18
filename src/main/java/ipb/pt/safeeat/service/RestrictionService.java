package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.repository.RestrictionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RestrictionService {
    @Autowired
    private RestrictionRepository restrictionRepository;

    public List<Restriction> getAll() {
        return restrictionRepository.findAll();
    }

    public Restriction findById(String id) {
        return restrictionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));
    }

    public Restriction create(Restriction restriction) {
        return restrictionRepository.save(restriction);
    }

    public List<Restriction> createMany(List<Restriction> restrictions) {
        return restrictionRepository.saveAll(restrictions);
    }

    public Restriction update(Restriction restriction) {
        Restriction old = restrictionRepository.findById(restriction.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        BeanUtils.copyProperties(restriction, old);
        return restrictionRepository.save(restriction);
    }

    public void delete(String id) {
        restrictionRepository.deleteById(id);
    }
}
