package handmade_goods.digital_marketplace.repository.product;

import handmade_goods.digital_marketplace.model.product.SearchRequest;
import handmade_goods.digital_marketplace.model.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findByCategoryAndKeywords(SearchRequest searchRequest) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        List<Product.Category> categories = searchRequest.categories();
        if (!(categories == null || categories.isEmpty())) {
            predicates.add(product.get("category").in(categories));
        }

        List<String> keywords = searchRequest.keywords();
        if (!(keywords == null || keywords.isEmpty())) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String keyword : keywords) {
                keywordPredicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(product.get("name")), "%" + keyword.toLowerCase() + "%"
                        )
                );
            }
            predicates.add(criteriaBuilder.or(keywordPredicates.toArray(new Predicate[0])));
        }

        query.select(product).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }
}
