package hr.tvz.diplomski.webshop.repository;

import hr.tvz.diplomski.webshop.domain.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findBrandFacet(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = spec.toPredicate(root, query, cb);

        Join<Product, Brand> brandJoin = root.join(Product_.brand);
        query.multiselect(brandJoin.get(Brand_.name), cb.count(root));
        query.where(predicate);
        query.groupBy(brandJoin.get(Brand_.name));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Object[]> findCategoryFacet(Specification<Product> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = spec.toPredicate(root, query, cb);

        Join<Product, Category> categoryJoin = root.join(Product_.category);
        query.multiselect(categoryJoin.get(Category_.name), cb.count(root));
        query.where(predicate);
        query.groupBy(categoryJoin.get(Category_.name));

        return entityManager.createQuery(query).getResultList();
    }
}
