package handmade_goods.digital_marketplace.model;
import jakarta.persistence.*;


@Entity
@Table(name = "sellers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Seller extends User {

    public Seller() {
    }

    public Seller(String username, String password, String email) {
        super(username, password, email);
    }
}
