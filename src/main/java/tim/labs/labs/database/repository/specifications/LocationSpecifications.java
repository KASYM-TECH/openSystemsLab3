package tim.labs.labs.database.repository.specifications;

import tim.labs.labs.database.entity.Location;
import org.springframework.data.jpa.domain.Specification;

public class LocationSpecifications {
    public static Specification<Location> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
}
