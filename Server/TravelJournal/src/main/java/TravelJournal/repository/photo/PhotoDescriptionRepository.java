package TravelJournal.repository.photo;

import TravelJournal.payload.response.PhotoDescriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PhotoDescriptionRepository extends MongoRepository<TravelJournal.model.photo.PhotoDescription, String> {
    Page<PhotoDescriptionResponse> findByOwner(String owner, Pageable pageable);
    TravelJournal.model.photo.PhotoDescription findByPhotoId(String photoId);
    @Query(value = "{'owner': ?0, 'country': ?1}")
    List<TravelJournal.model.photo.PhotoDescription> findByOwnerAndCountry(String owner, String country);

    List<PhotoDescriptionResponse> findByOwner(String owner);

    @Transactional
    void deleteByOwner(String owner);
}
