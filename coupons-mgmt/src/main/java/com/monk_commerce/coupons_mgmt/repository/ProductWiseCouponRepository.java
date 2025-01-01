package com.monk_commerce.coupons_mgmt.repository;

import com.monk_commerce.coupons_mgmt.entity.ProductWiseCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductWiseCouponRepository extends JpaRepository<ProductWiseCouponEntity, Long> {
    List<ProductWiseCouponEntity> findByIsDeletedFalse();
}
