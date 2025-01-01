package com.monk_commerce.coupons_mgmt.entity;

import com.monk_commerce.coupons_mgmt.enums.CouponType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Each subclass will have its own table
@Table(name = "coupons")
@Getter
@Setter
@ToString
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType type;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
