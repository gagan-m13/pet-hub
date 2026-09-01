package com.pethub;

import com.pethub.dto.request.ProductRequest;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.ProductResponse;
import com.pethub.entity.PetCategory;
import com.pethub.entity.ProductCategory;
import com.pethub.repository.PetCategoryRepository;
import com.pethub.repository.ProductCategoryRepository;
import com.pethub.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private PetCategoryRepository petCategoryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testCreateAndGetProduct() {
        PetCategory petCategory = petCategoryRepository.findBySlug("dogs").orElseThrow();
        ProductCategory prodCategory = productCategoryRepository.findBySlug("toys-play").orElseThrow();

        ProductRequest request = new ProductRequest();
        request.setName("Interactive Laser Dog Ball");
        request.setDescription("Smart self-rolling laser ball for dogs.");
        request.setPrice(new BigDecimal("1299.00"));
        request.setDiscountPrice(new BigDecimal("999.00"));
        request.setBrand("PetTech");
        request.setSku("PT-BALL-01");
        request.setStockQuantity(30);
        request.setPetCategoryId(petCategory.getId());
        request.setProductCategoryId(prodCategory.getId());
        request.setActive(true);
        request.setFeatured(true);

        ProductResponse created = productService.createProduct(request);
        assertNotNull(created.getId());
        assertEquals("Interactive Laser Dog Ball", created.getName());
        assertEquals(new BigDecimal("999.00"), created.getEffectivePrice());
        assertTrue(created.isInStock());

        ProductResponse fetched = productService.getProductById(created.getId());
        assertEquals("PT-BALL-01", fetched.getSku());
    }

    @Test
    public void testFilterAndSearchProducts() {
        PagedResponse<ProductResponse> result = productService.getProducts(
                null, null, null, null, null, false, "Royal Canin", 0, 10, "createdAt", "DESC"
        );
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
    }

    @Test
    public void testGetFeaturedProducts() {
        List<ProductResponse> featured = productService.getFeaturedProducts();
        assertNotNull(featured);
        assertFalse(featured.isEmpty());
    }
}
