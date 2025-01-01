# Coupons Management API

## Description  
This project is a Spring Boot application designed for managing coupons in an e-commerce system. It supports cart-wise coupons, integration with product categories, and an intuitive API for easy management. Developed as part of the **Monk Commerce 2024 task**.

---

## Features  
- **Cart-wise coupon support** for personalized discounts.  
- **API-based management** of coupons: Create, update, delete, and validate coupons.  
- **Spring Boot integration** with JPA, Hibernate, and RESTful APIs.  
- Secure and scalable architecture.  

---

## Technologies Used  
- **Spring Boot** (3.4.x)  
- **Java** (21)  
- **Maven**  
- **JPA**  
- **Postgres**  
- **Swagger** for API documentation  

---

## Prerequisites  
- **Java JDK 11+**  
- **Maven 3.8+**  
- **Postgres Server** or any relational database you use  
- An IDE like IntelliJ IDEA or Eclipse (optional)

---


## Database Design  

### Overview  
The database design utilizes a **single-table inheritance strategy** (`@Inheritance(strategy = InheritanceType.JOINED)`) to manage different coupon types under a parent entity (`CouponEntity`). Each subclass represents a specific type of coupon, with its data stored in separate tables. This design is both flexible and scalable for managing various coupon types.

### Entity Breakdown  

1. **`CouponEntity`**  
   - Acts as the base class for all coupon types.  
   - Contains common attributes like `id`, `type`, and `isDeleted`.  
   - Mapped to the `coupons` table.

2. **`ProductWiseCouponEntity`**  
   - Represents product-specific coupons.  
   - Inherits from `CouponEntity`.  
   - Stores the product ID and the discount amount.  
   - Mapped to the `product_wise_coupons` table.

3. **`BxGyCouponEntity`** (Buy X Get Y Coupons)  
   - Represents "Buy X, Get Y" type coupons.  
   - Contains two lists of products:  
     - `buyProducts`: List of products that must be purchased.  
     - `getProducts`: List of products that will be offered as a reward.  
   - Uses two join tables, `buy_x_get_y_buy_products` and `buy_x_get_y_get_products`, to manage the relationships with `CouponProductEntity`.

4. **`CartWiseCouponEntity`**  
   - Represents cart-level coupons applied when a cart total exceeds a threshold.  
   - Stores the threshold and discount values.  
   - Mapped to the `cart_wise_coupons` table.

5. **`CouponProductEntity`**  
   - Represents individual products associated with "Buy X Get Y" coupons.  
   - Stores the product ID and quantity.  
   - Mapped to the `coupon_product` table.

### Relationships  
- **Inheritance Mapping**  
  - All subclasses of `CouponEntity` are mapped using the `JOINED` strategy, creating separate tables for each subclass while sharing the `id` from the parent class.
  
- **One-to-Many Relationships**  
  - `BxGyCouponEntity` uses one-to-many relationships for both `buyProducts` and `getProducts`.  
  - These relationships are maintained through join tables (`buy_x_get_y_buy_products` and `buy_x_get_y_get_products`).

---

# API Endpoints

- `GET /api/coupons`: Fetch paginated list of coupons.
- `GET /api/coupons/{id}`: Fetch a particular coupon by ID.
- `POST /api/coupons`: Create a new coupon based on the details provided.
- `PUT /api/coupons/{id}`: Update an existing coupon.
- `DELETE /api/coupons/{id}`: Soft delete a coupon.
- `POST /api/coupons/apply-coupons`: Get applicable coupons for a cart.
- `POST /api/coupons/apply-coupons/{id}`: Apply a coupon to a cart with a particular coupon ID.

---

## Business Logic  

### Coupon Management  
1. **Creation of Coupons**  
   - Coupons are created based on their type (`CouponType`).  
   - Each coupon type (e.g., cart-wise, product-wise, BxGy) includes specific business rules for validation during creation.

2. **Validation of Coupons**  
   - Each coupon type is validated based on its criteria:  
     - **Cart-wise coupons**: Ensure the cart total meets the threshold.  
     - **Product-wise coupons**: Verify the product exists and matches the criteria.  
     - **BxGy coupons**: Check that the required products are present in the cart with the correct quantities.

3. **Deletion of Coupons**  
   - Coupons can be soft-deleted by setting the `isDeleted` flag to `true`.  
   - This approach ensures historical data is retained for reporting or auditing purposes.

### Coupon Application  
1. **Cart-wise Coupons**  
   - Check if the cart total exceeds the `threshold`.  
   - Apply the `discount` to the cart total.

2. **Product-wise Coupons**  
   - Verify if the specified product is in the cart.  
   - Apply the `discount` to the product's price.

3. **BxGy Coupons**  
   - Check if all `buyProducts` are present in the cart with sufficient quantities.  
   - Add the `getProducts` as free or discounted items.  
   - Enforce the `repitionLimit` to cap the number of times the coupon can be applied.

### Key Design Considerations  
1. **Scalability**  
   - The use of inheritance allows for easy addition of new coupon types.  
   - Relationships and join tables support complex coupon structures like BxGy.

2. **Flexibility**  
   - Each coupon type encapsulates its own rules, making it easy to modify or extend specific business logic.  

3. **Performance**  
   - Use appropriate indexes on foreign keys and commonly queried fields (e.g., `product_id`, `type`, `isDeleted`).  
   - Optimize join queries for inheritance and relationships.

4. **Auditing and Reporting**  
   - Soft deletion (`isDeleted`) retains data for future audits.  
   - Historical data can be used to analyze coupon usage patterns.

---

## Unimplemented Cases

- **Coupon Expiration**: No auto-expiration mechanism for coupons.
- **Usage Limit**: Coupons don’t have a usage limit or tracking.
- **Coupon Stacking**: No support for applying multiple coupons on a single cart.
- **Coupon Conditions**: Missing conditions like minimum cart value or product categories for applying coupons.

## Limitations

- **Pagination**: Coupons are fetched without pagination or filtering, which may cause performance issues with large datasets.
- **Validation**: Coupons are applied without thorough validation (e.g., expiry check, valid code).
- **Cart Integration**: Basic cart structure assumed, not scalable for complex cart systems.
- **User-Specific Coupons**: Coupons are applied without considering user-specific restrictions or usage history.
