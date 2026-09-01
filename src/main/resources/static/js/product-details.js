/**
 * PET HUB — Product Details & Review Interactions
 */

let currentProduct = null;

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (!productId) {
        window.location.href = '/products.html';
        return;
    }

    await loadProductDetails(productId);
    await loadProductReviews(productId);
    initQuantityControls();
    initReviewForm(productId);
});

async function loadProductDetails(id) {
    try {
        const res = await API.get(`/products/${id}`);
        currentProduct = res.data;
        renderProductDetails(currentProduct);
    } catch (e) {
        API.showToast('Product not found', 'error');
        setTimeout(() => window.location.href = '/products.html', 1500);
    }
}

function renderProductDetails(p) {
    document.title = `${p.name} — PET HUB`;
    document.getElementById('product-title').innerText = p.name;
    document.getElementById('product-brand').innerText = `Brand: ${p.brand || 'PET HUB'}`;
    document.getElementById('product-sku').innerText = `SKU: ${p.sku}`;
    document.getElementById('product-description').innerText = p.description;
    document.getElementById('product-effective-price').innerText = API.formatCurrency(p.effectivePrice);

    if (p.discountPrice) {
        const oldPriceEl = document.getElementById('product-old-price');
        if (oldPriceEl) {
            oldPriceEl.innerText = API.formatCurrency(p.price);
            oldPriceEl.style.display = 'inline';
        }
    }

    const stockBadge = document.getElementById('product-stock-badge');
    const addToCartBtn = document.getElementById('add-to-cart-btn');

    if (p.stockQuantity <= 0) {
        stockBadge.innerText = 'Out of Stock';
        stockBadge.style.color = 'var(--danger)';
        if (addToCartBtn) {
            addToCartBtn.disabled = true;
            addToCartBtn.innerText = 'Out of Stock';
        }
    } else if (p.stockQuantity <= 5) {
        stockBadge.innerText = `Low Stock (${p.stockQuantity} remaining)`;
        stockBadge.style.color = 'var(--warning)';
    } else {
        stockBadge.innerText = `In Stock (${p.stockQuantity} available)`;
        stockBadge.style.color = 'var(--success)';
    }

    // Gallery
    const mainImg = document.getElementById('main-gallery-img');
    mainImg.src = p.primaryImageUrl;

    const thumbsContainer = document.getElementById('gallery-thumbs');
    if (thumbsContainer && p.images && p.images.length > 0) {
        thumbsContainer.innerHTML = p.images.map((img, idx) => `
            <img src="${img.imageUrl}" class="thumb-img ${img.primary ? 'active' : ''}" onclick="switchGalleryImage('${img.imageUrl}', this)" alt="Thumbnail ${idx + 1}">
        `).join('');
    }
}

function switchGalleryImage(url, el) {
    document.getElementById('main-gallery-img').src = url;
    document.querySelectorAll('.thumb-img').forEach(t => t.classList.remove('active'));
    el.classList.add('active');
}

function initQuantityControls() {
    const qtyInput = document.getElementById('product-qty-input');
    const decBtn = document.getElementById('qty-dec-btn');
    const incBtn = document.getElementById('qty-inc-btn');
    const addBtn = document.getElementById('add-to-cart-btn');

    if (decBtn && qtyInput) {
        decBtn.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            if (val > 1) qtyInput.value = val - 1;
        });
    }

    if (incBtn && qtyInput) {
        incBtn.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            if (currentProduct && val < currentProduct.stockQuantity) {
                qtyInput.value = val + 1;
            } else {
                API.showToast(`Only ${currentProduct.stockQuantity} units available`, 'info');
            }
        });
    }

    if (addBtn && qtyInput) {
        addBtn.addEventListener('click', async () => {
            if (!API.isAuthenticated()) {
                API.showToast('Please sign in to add items to your cart', 'info');
                setTimeout(() => window.location.href = '/login.html', 1000);
                return;
            }

            const quantity = parseInt(qtyInput.value) || 1;
            try {
                addBtn.disabled = true;
                addBtn.innerText = 'Adding...';
                await API.post('/cart/items', { productId: currentProduct.id, quantity });
                API.showToast(`Added ${quantity} item(s) to your cart! 🛍️`, 'success');
                API.updateCartBadge();
            } catch (err) {
                API.showToast(err.message || 'Could not add to cart', 'error');
            } finally {
                addBtn.disabled = false;
                addBtn.innerText = 'Add to Cart 🛒';
            }
        });
    }
}

async function loadProductReviews(productId) {
    const container = document.getElementById('reviews-list');
    if (!container) return;

    try {
        const res = await API.get(`/products/${productId}/reviews`);
        const reviews = res.data;

        if (!reviews || reviews.length === 0) {
            container.innerHTML = `<p style="color: var(--dark-muted); padding: 16px 0;">No reviews yet. Be the first to share your experience!</p>`;
            return;
        }

        container.innerHTML = reviews.map(r => `
            <div style="border-bottom: 1px solid var(--border); padding: 16px 0;">
                <div style="display: flex; justify-content: space-between; margin-bottom: 6px;">
                    <strong>${r.userName || 'Verified Buyer'}</strong>
                    <span style="color: #f59e0b;">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                </div>
                <p style="color: var(--dark-muted); font-size: 0.95rem;">${r.comment}</p>
                <small style="color: #94a3b8;">${new Date(r.createdAt).toLocaleDateString()}</small>
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = `<p style="color: var(--danger);">Failed to load reviews</p>`;
    }
}

function initReviewForm(productId) {
    const form = document.getElementById('review-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!API.isAuthenticated()) {
            API.showToast('Please sign in to write a review', 'info');
            setTimeout(() => window.location.href = '/login.html', 1000);
            return;
        }

        const rating = parseInt(document.getElementById('review-rating').value);
        const comment = document.getElementById('review-comment').value.trim();

        try {
            await API.post(`/products/${productId}/reviews`, { rating, comment });
            API.showToast('Thank you! Your review has been submitted.', 'success');
            form.reset();
            loadProductReviews(productId);
            loadProductDetails(productId); // Refresh rating stats
        } catch (err) {
            API.showToast(err.message || 'Failed to submit review', 'error');
        }
    });
}
