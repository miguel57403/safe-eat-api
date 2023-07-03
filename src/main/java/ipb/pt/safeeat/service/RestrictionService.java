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
        // TODO: Split this method?
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Restriction> restrictions = restrictionRepository.findAll();
        if (user != "anonymousUser") restrictionCheckerComponent.checkRestrictionList(restrictions);
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

        if (!getAuthenticatedUser().isAdmin() && !getAuthenticatedUser().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_RESTRICTION);

        List<Restriction> restrictions = restrictionRepository.findAllById(user.getRestrictionIds());
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

        List<User> users = userRepository.findByRestrictionIds(restriction.getId());

        for (User user : users) {
            user.getRestrictionIds().remove(restriction.getId());
            userRepository.save(user);
        }

        List<Ingredient> ingredients = ingredientRepository.findAllByRestrictionIds(restriction.getId());
        for (Ingredient ingredient : ingredients) {
            ingredient.getRestrictionIds().remove(restriction.getId());
            ingredientRepository.save(ingredient);
        }

        restrictionRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
