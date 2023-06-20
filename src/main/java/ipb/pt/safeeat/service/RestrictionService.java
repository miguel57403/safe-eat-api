package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.RestaurantConstants;
import ipb.pt.safeeat.dto.RestrictionDto;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.repository.RestrictionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    public Restriction create(RestrictionDto restrictionDto) {
        Restriction restriction = new Restriction();
        BeanUtils.copyProperties(restrictionDto, restriction);
        return restrictionRepository.save(restriction);
    }

    @Transactional
    public List<Restriction> createMany(List<RestrictionDto> restrictionDtos) {
        List<Restriction> created = new ArrayList<>();
        for (RestrictionDto restrictionDto : restrictionDtos) {
            created.add(create(restrictionDto));
        }

        return created;
    }

    public Restriction update(RestrictionDto restrictionDto) {
        Restriction old = restrictionRepository.findById(restrictionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, RestaurantConstants.NOT_FOUND));

        BeanUtils.copyProperties(restrictionDto, old);
        return restrictionRepository.save(old);
    }

    public void delete(String id) {
        restrictionRepository.deleteById(id);
    }
}
