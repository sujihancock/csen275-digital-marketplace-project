package handmade_goods.digital_marketplace.model.product;

public record AddRequest(String name, String description, Double price, String imageUrl, Product.Category category) {
}
