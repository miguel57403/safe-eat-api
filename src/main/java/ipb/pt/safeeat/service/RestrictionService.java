package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.RestrictionDto;
import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import ipb.pt.safeeat.repository.UserRepository;
import ipb.pt.safeeat.utility.RestrictionChecker;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RestrictionChecker restrictionChecker;

    public List<Restriction> findAll() {
        List<Restriction> restrictions = restrictionRepository.findAll();
        restrictionChecker.checkRestrictionList(restrictions);
        return restrictions;
    }

    public Restriction findById(String id) {
        return restrictionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));
    }

    public List<Restriction> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.USER_NOT_FOUND));

        return user.getRestrictions();
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        BeanUtils.copyProperties(restrictionDto, old);
        return restrictionRepository.save(old);
    }

    public void delete(String id) {
        Restriction restriction = restrictionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.RESTAURANT_NOT_FOUND));

        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.getRestrictions().remove(restriction);
            userRepository.save(user);
        }

        List<Ingredient> ingredients = ingredientRepository.findAll();
        for (Ingredient ingredient : ingredients) {
            ingredient.getRestrictions().remove(restriction);
            ingredientRepository.save(ingredient);
        }

        restrictionRepository.deleteById(id);
    }
}
