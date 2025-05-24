package handmade_goods.digital_marketplace.model.review;

import java.time.LocalDateTime;

public record ReviewRequest(String comment, Double rating, LocalDateTime date, Long reviewerId) { }
