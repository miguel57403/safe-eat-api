package ipb.pt.safeeat.service;

import ipb.pt.safeeat.constant.ForbiddenConstant;
import ipb.pt.safeeat.constant.NotFoundConstant;
import ipb.pt.safeeat.dto.AdvertisementDto;
import ipb.pt.safeeat.model.Advertisement;
import ipb.pt.safeeat.model.Restaurant;
import ipb.pt.safeeat.model.User;
import ipb.pt.safeeat.repository.AdvertisementRepository;
import ipb.pt.safeeat.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private AzureBlobService azureBlobService;

    public List<Advertisement> findAll() {
        return advertisementRepository.findAll();
    }

    public Advertisement findById(String id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADVERTISEMENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByAdvertisements(advertisement).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        return advertisement;
    }

    public List<Advertisement> findAllByRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.isAdmin() && !restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        return restaurant.getAdvertisements();
    }

    public Advertisement create(AdvertisementDto advertisementDto) {
        Restaurant restaurant = restaurantRepository.findById(advertisementDto.getRestaurantId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        Advertisement advertisement = new Advertisement();
        BeanUtils.copyProperties(advertisementDto, advertisement);
        Advertisement created = advertisementRepository.save(advertisement);

        restaurant.getAdvertisements().add(created);
        restaurantRepository.save(restaurant);

        return created;
    }

    public Advertisement update(AdvertisementDto advertisementDto) {
        Advertisement old = advertisementRepository.findById(advertisementDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADVERTISEMENT_NOT_FOUND));

        Restaurant restaurant = restaurantRepository.findByAdvertisements(old).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        if (!restaurant.getAdvertisements().contains(old))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        BeanUtils.copyProperties(advertisementDto, old);
        old.setRestaurantId(restaurant.getId());

        return advertisementRepository.save(old);
    }

    public Advertisement updateImage(String id, MultipartFile imageFile) throws IOException {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.CATEGORY_NOT_FOUND));

        InputStream imageStream = imageFile.getInputStream();
        String blobName = imageFile.getOriginalFilename();

        if (blobName == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is null");

        if (advertisement.getImage() != null && !advertisement.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(advertisement.getImage().replace(containerUrl, ""));
        }

        String extension = blobName.substring(blobName.lastIndexOf(".") + 1);
        String partialBlobName = "advertisements/" + advertisement.getId() + "." + extension;
        azureBlobService.uploadBlob(partialBlobName, imageStream);

        String newBlobName = azureBlobService.getBlobUrl(partialBlobName);
        advertisement.setImage(newBlobName);
        return advertisementRepository.save(advertisement);
    }

    public void delete(String id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.ADVERTISEMENT_NOT_FOUND));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Restaurant restaurant = restaurantRepository.findByAdvertisements(advertisement).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NotFoundConstant.RESTAURANT_NOT_FOUND));

        if (!restaurant.getOwner().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        if (!restaurant.getAdvertisements().contains(advertisement))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ForbiddenConstant.FORBIDDEN_ADVERTISEMENT);

        if (advertisement.getImage() != null && !advertisement.getImage().isBlank()) {
            String containerUrl = azureBlobService.getContainerUrl() + "/";
            azureBlobService.deleteBlob(advertisement.getImage().replace(containerUrl, ""));
        }

        restaurant.getAdvertisements().remove(advertisement);
        restaurantRepository.save(restaurant);

        advertisementRepository.deleteById(id);
    }
}
