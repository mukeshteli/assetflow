package com.assetflow.specification;

import com.assetflow.entity.Asset;
import com.assetflow.entity.AssetStatus;
import org.springframework.data.jpa.domain.Specification;

public class AssetSpecification {

    public static Specification<Asset> search(String keyword, Long categoryId, AssetStatus status) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("assetName")), like),
                        cb.like(cb.lower(root.get("assetTag")), like),
                        cb.like(cb.lower(root.get("serialNumber")), like)
                ));
            }

            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            return predicate;
        };
    }
}