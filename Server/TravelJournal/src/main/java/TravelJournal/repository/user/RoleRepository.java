package TravelJournal.repository.user;

import TravelJournal.model.user.EnumRole;
import TravelJournal.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(EnumRole role);
}
