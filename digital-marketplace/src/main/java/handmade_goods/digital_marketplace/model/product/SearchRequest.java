package handmade_goods.digital_marketplace.model.product;

import java.util.List;

public record SearchRequest(List<String> keywords, List<Product.Category> categories) {}