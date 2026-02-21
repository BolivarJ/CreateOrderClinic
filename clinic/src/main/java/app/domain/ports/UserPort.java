package app.domain.ports;
import app.domain.models.User;
public interface UserPort {

    public boolean existsByDocument(String cedula);
    public boolean existsByUsername(String username);
    public User findByDocument(String document);
    public void save(User user);
    
}
