package com.monk_commerce.coupons_mgmt.repository;

import com.monk_commerce.coupons_mgmt.entity.BxGyCouponEntity;
import com.monk_commerce.coupons_mgmt.entity.CartWiseCouponEntity;
import com.monk_commerce.coupons_mgmt.entity.ProductWiseCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BxGyCouponRepository extends JpaRepository<BxGyCouponEntity, Long> {
    List<BxGyCouponEntity> findByIsDeletedFalse();
}
