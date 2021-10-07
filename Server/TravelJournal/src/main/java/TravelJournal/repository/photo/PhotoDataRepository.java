package TravelJournal.repository.photo;

import TravelJournal.model.photo.PhotoData;
import TravelJournal.payload.response.PhotoDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PhotoDataRepository extends MongoRepository<PhotoData, String> {
    Page<PhotoDataResponse> findByOwner(String owner, Pageable pageable);
    PhotoData findByPhotoId(String photoId);
    @Query(value = "{'owner': ?0, 'country': ?1}")
    List<PhotoData> findByOwnerAndCountry(String owner, String country);

    List<PhotoDataResponse> findByOwner(String owner);

    @Transactional
    void deleteByOwner(String owner);

    @Transactional
    void deleteByPhotoId(String photoId);
}
