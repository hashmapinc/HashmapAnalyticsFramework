package com.hashmapinc.haf.repository;

import com.hashmapinc.haf.page.PaginatedRequest;
import org.hibernate.jpa.criteria.path.PluralAttributePath;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DefaultBaseRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID>{

    private EntityManager entityManager;

    public DefaultBaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<T> findPaginated(PaginatedRequest request, PageRequest page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getDomainClass());
        Root<T> root = criteriaQuery.from(getDomainClass());
        Predicate finalPredicate;
        finalPredicate = builder.equal(root.<String>get("tenantId"), request.getTenantId().toString());
        if(request.getCustomerId() != null){
            finalPredicate = builder.and(finalPredicate, builder.equal(root.<String>get("customerId"), request.getCustomerId().toString()));
        }
        finalPredicate = builder.and(finalPredicate, builder.greaterThan(root.get("id"), request.idOffset()));

        if(request.hasCriteria()){
            for (Map.Entry<String, Object> criterion: request.getCriteria().entrySet()){
                if(root.get(criterion.getKey()) instanceof PluralAttributePath){
                    Join<Object, Object> join = root.join(criterion.getKey());
                    join.on(join.in(criterion.getValue()));
                }else {
                    finalPredicate = builder.and(finalPredicate, builder.equal(root.get(criterion.getKey()), criterion.getValue()));
                }
            }
        }

        criteriaQuery.select(root).where(finalPredicate).orderBy(builder.desc(root.get("id")));
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(page.getPageNumber() * page.getPageSize());
        query.setMaxResults(page.getPageSize());
        return query.getResultList();
    }
}
