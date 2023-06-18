package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constants.CategoryConstants;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category findById(String id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CategoryConstants.NOT_FOUND));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public List<Category> createMany(List<Category> categories) {
        List<Category> created = new ArrayList<>();
        for(Category category : categories) {
            created.add(create(category));
        }

        return created;
    }

    public Category update(Category category) {
        Category old = categoryRepository.findById(category.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, CategoryConstants.NOT_FOUND));

        BeanUtils.copyProperties(category, old);
        return categoryRepository.save(category);
    }

    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}
