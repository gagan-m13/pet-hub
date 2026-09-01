package com.pethub.service.impl;

import com.pethub.entity.*;
import com.pethub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class DatabaseInitializerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PetCategoryRepository petCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseInitializerService(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      AddressRepository addressRepository,
                                      PetCategoryRepository petCategoryRepository,
                                      ProductCategoryRepository productCategoryRepository,
                                      ProductRepository productRepository,
                                      ProductImageRepository productImageRepository,
                                      ReviewRepository reviewRepository,
                                      CartRepository cartRepository,
                                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.petCategoryRepository = petCategoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.reviewRepository = reviewRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            initRolesAndUsers();
            initCategories();
            initSampleProducts();
            logger.info("PET HUB database initialization completed successfully.");
        } catch (Exception ex) {
            logger.error("Database initialization error: {}", ex.getMessage(), ex);
        }
    }

    private void initRolesAndUsers() {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        if (!userRepository.existsByEmail("admin@pethub.com")) {
            User admin = new User("System", "Admin", "admin@pethub.com", passwordEncoder.encode("Admin@123"), "9876543210");
            admin.setRoles(new HashSet<>(Arrays.asList(userRole, adminRole)));
            User savedAdmin = userRepository.save(admin);
            cartRepository.save(new Cart(savedAdmin));
            logger.info("Created Default Admin account: admin@pethub.com / Admin@123");
        }

        if (!userRepository.existsByEmail("customer@pethub.com")) {
            User customer = new User("Rahul", "Sharma", "customer@pethub.com", passwordEncoder.encode("User@123"), "9876543211");
            customer.setRoles(Collections.singleton(userRole));
            User savedCustomer = userRepository.save(customer);
            cartRepository.save(new Cart(savedCustomer));

            Address address = new Address(
                    savedCustomer,
                    "Rahul Sharma",
                    "9876543211",
                    "Flat 402, Green Valley Apartments, MG Road",
                    "Bengaluru",
                    "Karnataka",
                    "560001",
                    "India",
                    true
            );
            addressRepository.save(address);
            logger.info("Created Demo Customer account: customer@pethub.com / User@123");
        }
    }

    private void initCategories() {
        if (petCategoryRepository.count() == 0) {
            petCategoryRepository.saveAll(Arrays.asList(
                    new PetCategory("Dogs", "dogs", "Premium products and nutrition tailored for dogs of all breeds and ages.", "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=500&auto=format&fit=crop&q=60"),
                    new PetCategory("Cats", "cats", "Wholesome food, playful toys, and cozy beds for your feline friends.", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=500&auto=format&fit=crop&q=60"),
                    new PetCategory("Birds", "birds", "Healthy seed blends, cages, perches, and toys for pet birds.", "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=500&auto=format&fit=crop&q=60"),
                    new PetCategory("Fish & Aquatics", "fish-aquatics", "High quality fish food, aquariums, filters, and aquatic care essentials.", "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=500&auto=format&fit=crop&q=60"),
                    new PetCategory("Small Animals", "small-animals", "Food, bedding, and accessories for rabbits, hamsters, and guinea pigs.", "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=500&auto=format&fit=crop&q=60")
            ));
        }

        if (productCategoryRepository.count() == 0) {
            productCategoryRepository.saveAll(Arrays.asList(
                    new ProductCategory("Food & Nutrition", "food-nutrition", "Nutritious kibble, wet food, and specialized diets for pets.", "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Toys & Play", "toys-play", "Engaging, durable toys for physical exercise and mental stimulation.", "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Accessories & Leashes", "accessories-leashes", "Collars, leashes, harnesses, and travel accessories.", "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Grooming & Hygiene", "grooming-hygiene", "Shampoos, brushes, nail clippers, and dental care.", "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Beds & Furniture", "beds-furniture", "Orthopedic pet beds, scratching posts, and cozy sleeping mats.", "https://images.unsplash.com/photo-1541599540903-216a46ca1dc0?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Health & Wellness", "health-wellness", "Supplements, vitamins, flea & tick prevention, and first aid.", "https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Treats & Chews", "treats-chews", "Delicious, healthy treats and long-lasting dental chews.", "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?w=500&auto=format&fit=crop&q=60"),
                    new ProductCategory("Training & Behavior", "training-behavior", "Pee pads, clickers, crates, and behavior aids.", "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=500&auto=format&fit=crop&q=60")
            ));
        }
    }

    private void initSampleProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        PetCategory dogs = petCategoryRepository.findBySlug("dogs").orElse(null);
        PetCategory cats = petCategoryRepository.findBySlug("cats").orElse(null);
        PetCategory birds = petCategoryRepository.findBySlug("birds").orElse(null);
        PetCategory fish = petCategoryRepository.findBySlug("fish-aquatics").orElse(null);
        PetCategory smallAnimals = petCategoryRepository.findBySlug("small-animals").orElse(null);

        ProductCategory food = productCategoryRepository.findBySlug("food-nutrition").orElse(null);
        ProductCategory toys = productCategoryRepository.findBySlug("toys-play").orElse(null);
        ProductCategory acc = productCategoryRepository.findBySlug("accessories-leashes").orElse(null);
        ProductCategory grooming = productCategoryRepository.findBySlug("grooming-hygiene").orElse(null);
        ProductCategory beds = productCategoryRepository.findBySlug("beds-furniture").orElse(null);
        ProductCategory treats = productCategoryRepository.findBySlug("treats-chews").orElse(null);

        User customer = userRepository.findByEmail("customer@pethub.com").orElse(null);

        if (dogs != null && food != null) {
            Product p1 = new Product(
                    "Royal Canin Adult Maxi Dry Dog Food 4kg",
                    "royal-canin-adult-maxi-dry-dog-food-4kg",
                    "Specially formulated for adult large-breed dogs (26-44kg) from 15 months to 5 years old. Promotes optimal digestive security and supports high bone & joint health.",
                    new BigDecimal("2899.00"),
                    new BigDecimal("2599.00"),
                    "Royal Canin",
                    "RC-DOG-MAXI-4KG",
                    45,
                    dogs,
                    food,
                    true,
                    true
            );
            Product savedP1 = productRepository.save(p1);
            productImageRepository.save(new ProductImage(savedP1, "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=700&auto=format&fit=crop&q=80", true, 0));
            productImageRepository.save(new ProductImage(savedP1, "https://images.unsplash.com/photo-1568640347023-a616a30bc3bd?w=700&auto=format&fit=crop&q=80", false, 1));

            if (customer != null) {
                reviewRepository.save(new Review(savedP1, customer, 5, "Excellent food quality! My German Shepherd's coat is visibly shinier and healthier. Highly recommended."));
            }
        }

        if (dogs != null && toys != null) {
            Product p2 = new Product(
                    "KONG Classic Durable Rubber Dog Chew Toy",
                    "kong-classic-durable-rubber-dog-chew-toy",
                    "The gold standard of dog toys for over 40 years. Ultra-durable natural rubber formula provides mental stimulation and satisfies natural chew instincts.",
                    new BigDecimal("899.00"),
                    new BigDecimal("749.00"),
                    "KONG",
                    "KONG-CL-MED",
                    80,
                    dogs,
                    toys,
                    true,
                    true
            );
            Product savedP2 = productRepository.save(p2);
            productImageRepository.save(new ProductImage(savedP2, "https://images.unsplash.com/photo-1576201836106-db1758fd1c97?w=700&auto=format&fit=crop&q=80", true, 0));

            if (customer != null) {
                reviewRepository.save(new Review(savedP2, customer, 5, "Virtually indestructible! Keeps our Golden Retriever busy for hours when stuffed with peanut butter."));
            }
        }

        if (cats != null && food != null) {
            Product p3 = new Product(
                    "Whiskas Ocean Fish Adult Wet Cat Food (Pack of 12)",
                    "whiskas-ocean-fish-adult-wet-cat-food-12-pack",
                    "Delicious real fish pieces in savory gravy. Balanced calcium-phosphorus ratio for bone health, zinc and omega-6 fatty acids for vibrant skin and coat.",
                    new BigDecimal("660.00"),
                    new BigDecimal("579.00"),
                    "Whiskas",
                    "WHISK-OF-12PK",
                    60,
                    cats,
                    food,
                    true,
                    true
            );
            Product savedP3 = productRepository.save(p3);
            productImageRepository.save(new ProductImage(savedP3, "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (cats != null && beds != null) {
            Product p4 = new Product(
                    "Cozy Velvet Donut Cat & Small Dog Bed",
                    "cozy-velvet-donut-cat-small-dog-bed",
                    "Ultra-soft plush donut calming bed with raised rim creates a sense of security and provides head and neck support. Machine washable.",
                    new BigDecimal("1499.00"),
                    new BigDecimal("1199.00"),
                    "Pet Comforts",
                    "BED-DONUT-PLUSH-M",
                    35,
                    cats,
                    beds,
                    true,
                    true
            );
            Product savedP4 = productRepository.save(p4);
            productImageRepository.save(new ProductImage(savedP4, "https://images.unsplash.com/photo-1541599540903-216a46ca1dc0?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (dogs != null && acc != null) {
            Product p5 = new Product(
                    "Reflective Heavy-Duty Padded Dog Harness",
                    "reflective-heavy-duty-padded-dog-harness",
                    "No-pull ergonomic dog harness with 3M reflective straps for safe night walks. Breathable air-mesh lining keeps pets cool and comfortable.",
                    new BigDecimal("1299.00"),
                    new BigDecimal("999.00"),
                    "PawSafe",
                    "HARNESS-REF-L",
                    25,
                    dogs,
                    acc,
                    true,
                    true
            );
            Product savedP5 = productRepository.save(p5);
            productImageRepository.save(new ProductImage(savedP5, "https://images.unsplash.com/photo-1601758228041-f3b2795255f1?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (birds != null && food != null) {
            Product p6 = new Product(
                    "Nutri-Berries Tropical Fruit Blend for Parrots & Conures 1kg",
                    "nutri-berries-tropical-fruit-blend-1kg",
                    "Rich in natural seeds, pellets, real papaya, pineapple, and mango. Fortified with vitamins, minerals, and antioxidants.",
                    new BigDecimal("850.00"),
                    new BigDecimal("725.00"),
                    "Lafeber",
                    "BIRD-LAFEB-1KG",
                    40,
                    birds,
                    food,
                    true,
                    false
            );
            Product savedP6 = productRepository.save(p6);
            productImageRepository.save(new ProductImage(savedP6, "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (fish != null && food != null) {
            Product p7 = new Product(
                    "TetraMin Tropical Flakes Complete Fish Food 200g",
                    "tetramin-tropical-flakes-complete-fish-food-200g",
                    "Clean & Clear Water formula promotes long life and vibrant natural coloring for all tropical freshwater fish.",
                    new BigDecimal("499.00"),
                    new BigDecimal("420.00"),
                    "Tetra",
                    "FISH-TETRA-200G",
                    90,
                    fish,
                    food,
                    true,
                    false
            );
            Product savedP7 = productRepository.save(p7);
            productImageRepository.save(new ProductImage(savedP7, "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (dogs != null && grooming != null) {
            Product p8 = new Product(
                    "Organic Oatmeal & Aloe Vera Soothing Dog Shampoo 500ml",
                    "organic-oatmeal-aloe-vera-soothing-dog-shampoo-500ml",
                    "Hypoallergenic formula specially crafted for sensitive, dry, or itchy skin. Free of parabens, sulfates, and artificial fragrances.",
                    new BigDecimal("699.00"),
                    new BigDecimal("549.00"),
                    "NaturePaws",
                    "SHAMP-OATM-500",
                    50,
                    dogs,
                    grooming,
                    true,
                    false
            );
            Product savedP8 = productRepository.save(p8);
            productImageRepository.save(new ProductImage(savedP8, "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7?w=700&auto=format&fit=crop&q=80", true, 0));
        }

        if (smallAnimals != null && treats != null) {
            Product p9 = new Product(
                    "Crunchy Timothy Hay Meadow Sticks for Rabbits & Guinea Pigs",
                    "crunchy-timothy-hay-meadow-sticks",
                    "High-fiber natural hay chews essential for maintaining dental hygiene and proper digestive motility in small herbivores.",
                    new BigDecimal("399.00"),
                    new BigDecimal("329.00"),
                    "Oxbow",
                    "OXB-TIM-STICKS",
                    65,
                    smallAnimals,
                    treats,
                    true,
                    false
            );
            Product savedP9 = productRepository.save(p9);
            productImageRepository.save(new ProductImage(savedP9, "https://images.unsplash.com/photo-1548767797-d8c844163c4c?w=700&auto=format&fit=crop&q=80", true, 0));
        }
    }
}
