package handmade_goods.digital_marketplace.dto;

import handmade_goods.digital_marketplace.model.product.Product;

public class CartItemDto {

    private final Product.Summary productSummary;
    private final int quantity;
    private final double totalPrice;

    public CartItemDto(Product.Summary productSummary, int quantity) {
        this.productSummary = productSummary;
        this.quantity = quantity;
        this.totalPrice = quantity * productSummary.price();
    }

    public Product.Summary getProductSummary() {
        return productSummary;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
