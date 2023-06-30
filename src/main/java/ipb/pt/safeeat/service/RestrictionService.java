package ipb.pt.safeeat.service;

import ipb.pt.safeeat.component.RestrictionCheckerComponent;
import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.RestrictionDto;
import ipb.pt.safeeat.model.Ingredient;
import ipb.pt.safeeat.model.Restriction;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.IngredientRepository;
import ipb.pt.safeeat.repository.RestrictionRepository;
import ipb.pt.safeeat.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private RestrictionCheckerComponent restrictionCheckerComponent;

    public List<Restriction> findAll() {
        List<Restriction> restrictions = restrictionRepository.findAll();
        restrictionCheckerComponent.checkRestrictionList(restrictions);
        return restrictions;
    }

    public Restriction findById(String id) {
        Restriction restriction = restrictionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        restrictionCheckerComponent.checkRestriction(restriction);
        return restriction;
    }

    public List<Restriction> findAllByUser(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.USER_NOT_FOUND));

        User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!current.isAdmin() && !current.equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTRICTION);

        List<Restriction> restrictions = user.getRestrictions();
        restrictionCheckerComponent.checkRestrictionList(restrictions);
        return restrictions;
    }

    public Restriction create(RestrictionDto restrictionDto) {
        Restriction restriction = new Restriction();
        BeanUtils.copyProperties(restrictionDto, restriction);
        Restriction created = restrictionRepository.save(restriction);

        restrictionCheckerComponent.checkRestriction(created);
        return created;
    }

    public Restriction update(RestrictionDto restrictionDto) {
        Restriction old = restrictionRepository.findById(restrictionDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        BeanUtils.copyProperties(restrictionDto, old);
        Restriction updated = restrictionRepository.save(old);

        restrictionCheckerComponent.checkRestriction(updated);
        return updated;
    }

    public void delete(String id) {
        Restriction restriction = restrictionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        List<User> users = userRepository.findByRestrictions(restriction);

        for (User user : users) {
            user.getRestrictions().remove(restriction);
            userRepository.save(user);
        }

        List<Ingredient> ingredients = ingredientRepository.findByRestrictions(restriction);
        for (Ingredient ingredient : ingredients) {
            ingredient.getRestrictions().remove(restriction);
            ingredientRepository.save(ingredient);
        }

        restrictionRepository.deleteById(id);
    }
}
