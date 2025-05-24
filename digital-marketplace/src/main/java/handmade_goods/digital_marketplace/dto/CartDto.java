package handmade_goods.digital_marketplace.dto;

import java.util.List;

public record CartDto(List<CartItemDto> cartItems, Double totalAmount) { }
