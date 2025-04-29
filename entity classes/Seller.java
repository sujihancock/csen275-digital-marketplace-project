import jakarta.persistence.*;

@Entity
@Table(name = "seller")
public class Seller extends User {
    public Seller() {
    }

    public Seller(String username, String password, String email) {
        super(username, password, email);
    }
}
