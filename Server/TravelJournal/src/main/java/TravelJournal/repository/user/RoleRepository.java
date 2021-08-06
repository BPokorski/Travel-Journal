package TravelJournal.repository.user;

import TravelJournal.model.user.EnumRole;
import TravelJournal.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(EnumRole role);
}
