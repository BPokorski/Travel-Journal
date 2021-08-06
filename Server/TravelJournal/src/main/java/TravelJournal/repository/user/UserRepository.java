package TravelJournal.repository.user;

import TravelJournal.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByLogin(String login);
    Boolean existsByLogin(String login);
    Boolean existsByEmail(String email);
}
