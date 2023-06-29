package ipb.pt.safeeat.service;

import ipb.pt.safeeat.utility.NotFoundConstants;
import ipb.pt.safeeat.dto.CategoryDto;
import ipb.pt.safeeat.model.Category;
import ipb.pt.safeeat.model.Product;
import ipb.pt.safeeat.repository.CategoryRepository;
import ipb.pt.safeeat.repository.ProductRepository;
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

    @Autowired
    private ProductRepository productRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(String id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CATEGORY_NOT_FOUND));
    }

    public Category create(CategoryDto categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        return categoryRepository.save(category);
    }

    @Transactional
    public List<Category> createMany(List<CategoryDto> categoryDtos) {
        List<Category> created = new ArrayList<>();
        for (CategoryDto categoryDto : categoryDtos) {
            created.add(create(categoryDto));
        }

        return created;
    }

    public Category update(CategoryDto categoryDto) {
        Category old = categoryRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CATEGORY_NOT_FOUND));

        BeanUtils.copyProperties(categoryDto, old);
        return categoryRepository.save(old);
    }

    public void delete(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstants.CATEGORY_NOT_FOUND));

        List<Product> products = productRepository.findAllByCategory(category);

        if (products.size() > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category with products");

        categoryRepository.deleteById(id);
    }
}
