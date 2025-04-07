package org.metrowheel.user.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.user.model.User;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
    
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
    
    public Optional<User> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}
