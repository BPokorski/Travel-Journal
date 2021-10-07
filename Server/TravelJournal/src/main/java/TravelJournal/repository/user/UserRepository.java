package TravelJournal.repository.user;

import TravelJournal.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends MongoRepository<User, String> {
    User findByLogin(String login);
    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);

    @Transactional
    void deleteByLogin(String login);
}
