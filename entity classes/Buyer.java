import jakarta.persistence.*;

@Entity
@Table(name = "buyer")
public class Buyer extends User {
    public Buyer() {
    }

    public Buyer(String username, String password, String email) {
        super(username, password, email);
    }
}
