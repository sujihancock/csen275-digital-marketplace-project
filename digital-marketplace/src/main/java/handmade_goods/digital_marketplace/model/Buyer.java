package handmade_goods.digital_marketplace.model;

import jakarta.persistence.*;

@Entity
@Table(name = "buyers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Buyer extends User {

    public Buyer() {
    }

    public Buyer(String username, String password, String email) {
        super(username, password, email);
    }
}
