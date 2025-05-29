package handmade_goods.digital_marketplace.model.product;

public record UpdateRequest(
    String name,
    String description, 
    Double price,
    String imageUrl,
    Product.Category category,
    Integer quantity
) {} 