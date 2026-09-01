/**
 * PET HUB — Product Catalog, Filtering, Search & Sorting
 */

let currentFilters = {
    keyword:           '',
    petCategoryId:     '',
    productCategoryId: '',
    brand:             '',
    minPrice:          '',
    maxPrice:          '',
    inStockOnly:       false,
    sortBy:            'createdAt',
    sortDir:           'DESC',
    page:              0,
    size:              12
};

document.addEventListener('DOMContentLoaded', async () => {
    // Pick up URL query params
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('keyword'))         currentFilters.keyword           = urlParams.get('keyword');
    if (urlParams.has('petCategory'))     currentFilters.petCategoryId     = urlParams.get('petCategory');
    if (urlParams.has('productCategory')) currentFilters.productCategoryId = urlParams.get('productCategory');

    const searchInput = document.getElementById('catalog-search-input');
    if (searchInput && currentFilters.keyword) searchInput.value = currentFilters.keyword;

    await loadFilterOptions();
    await loadProducts();
    initFilterEventListeners();
});

async function loadFilterOptions() {
    try {
        const [petCatsRes, prodCatsRes, brandsRes] = await Promise.all([
            API.get('/pet-categories'),
            API.get('/categories'),
            API.get('/products/brands')
        ]);

        // Pet categories
        const petCatContainer = document.getElementById('pet-category-filters');
        if (petCatContainer && petCatsRes?.data) {
            petCatContainer.innerHTML = `
                <label class="checkbox-label">
                    <input type="radio" name="petCategory" value="" ${!currentFilters.petCategoryId ? 'checked' : ''}>
                    <span>All Pets</span>
                </label>
                ${petCatsRes.data.map(cat => `
                    <label class="checkbox-label">
                        <input type="radio" name="petCategory" value="${cat.id}" ${currentFilters.petCategoryId == cat.id ? 'checked' : ''}>
                        <span>${cat.name}</span>
                    </label>
                `).join('')}
            `;
        }

        // Product categories
        const prodCatContainer = document.getElementById('product-category-filters');
        if (prodCatContainer && prodCatsRes?.data) {
            prodCatContainer.innerHTML = `
                <label class="checkbox-label">
                    <input type="radio" name="productCategory" value="" ${!currentFilters.productCategoryId ? 'checked' : ''}>
                    <span>All Categories</span>
                </label>
                ${prodCatsRes.data.map(cat => `
                    <label class="checkbox-label">
                        <input type="radio" name="productCategory" value="${cat.id}" ${currentFilters.productCategoryId == cat.id ? 'checked' : ''}>
                        <span>${cat.name}</span>
                    </label>
                `).join('')}
            `;
        }

        // Brands
        const brandContainer = document.getElementById('brand-filters');
        if (brandContainer && brandsRes?.data) {
            brandContainer.innerHTML = `
                <label class="checkbox-label">
                    <input type="radio" name="brand" value="" checked>
                    <span>All Brands</span>
                </label>
                ${brandsRes.data.map(b => `
                    <label class="checkbox-label">
                        <input type="radio" name="brand" value="${b}">
                        <span>${b}</span>
                    </label>
                `).join('')}
            `;
        }
    } catch (e) {
        console.warn('Filter options load failed:', e.message);
    }
}

async function loadProducts() {
    const grid = document.getElementById('products-grid');
    const countLabel = document.getElementById('product-count-label');
    if (!grid) return;

    grid.innerHTML = `
        <div style="grid-column: 1/-1; text-align: center; padding: 60px 20px; color: var(--dark-muted);">
            <div class="loading-spinner" style="margin: 0 auto 16px; width: 36px; height: 36px; border-width: 3px;"></div>
            <div style="font-size: 1rem; font-weight: 600;">Loading products...</div>
        </div>
    `;

    try {
        const response = await API.get('/products', currentFilters);
        const pagedData = response?.data;

        if (!pagedData) throw new Error('No data returned from server');

        if (countLabel) {
            countLabel.innerHTML = `<strong>${pagedData.totalElements}</strong> products found`;
        }

        if (!pagedData.content || pagedData.content.length === 0) {
            grid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 64px 24px; background: #fff; border-radius: var(--radius-md); border: 1px solid var(--border);">
                    <div style="font-size: 3rem; margin-bottom: 16px;">🔍</div>
                    <h3 style="color: var(--secondary); margin-bottom: 8px;">No products found</h3>
                    <p style="color: var(--dark-muted);">Try adjusting your filters or search keywords.</p>
                    <button onclick="resetFilters()" class="btn btn-outline btn-sm" style="margin-top: 16px;">Clear All Filters</button>
                </div>
            `;
            renderPagination(0, 0);
            return;
        }

        grid.innerHTML = pagedData.content.map(p => `
            <div class="product-card">
                ${p.discountPrice ? `<span class="product-badge">SALE</span>` : ''}
                ${p.featured ? `<span class="product-badge" style="right: 12px; left: auto; background: linear-gradient(135deg,#25a18e,#1a7a6c);">⭐ Featured</span>` : ''}
                <a href="/product-details.html?id=${p.id}" class="product-img-wrapper">
                    <img src="${p.primaryImageUrl || ''}"
                         alt="${p.name}"
                         loading="lazy"
                         onerror="this.src='https://images.unsplash.com/photo-1551717743-49959800b1f6?w=400&auto=format&fit=crop&q=60'">
                </a>
                <div class="product-info">
                    <span class="product-category-tag">${p.petCategory?.name || 'Pet'} · ${p.productCategory?.name || 'Supply'}</span>
                    <a href="/product-details.html?id=${p.id}">
                        <h3 class="product-name" title="${p.name}">${p.name}</h3>
                    </a>
                    <div class="product-rating">
                        <span>★ ${p.averageRating > 0 ? Number(p.averageRating).toFixed(1) : 'New'}</span>
                        <span style="color: var(--dark-muted);">(${p.reviewCount || 0})</span>
                    </div>
                    <div class="product-pricing">
                        <span class="current-price">${API.formatCurrency(p.effectivePrice)}</span>
                        ${p.discountPrice ? `<span class="old-price">${API.formatCurrency(p.price)}</span>` : ''}
                    </div>
                    <button onclick="quickAddToCart(${p.id}, event)"
                            class="btn btn-primary btn-sm btn-block"
                            style="margin-top: auto;"
                            ${!p.inStock ? 'disabled' : ''}>
                        ${p.inStock ? '🛒 Add to Cart' : 'Out of Stock'}
                    </button>
                </div>
            </div>
        `).join('');

        renderPagination(pagedData.totalPages, pagedData.pageNumber);
    } catch (e) {
        grid.innerHTML = `
            <div style="grid-column: 1/-1; text-align: center; padding: 48px 20px;">
                <div class="alert alert-danger" style="max-width: 500px; margin: 0 auto; text-align: left;">
                    ⚠️ Failed to load products. Please check your connection and try again.<br>
                    <button onclick="loadProducts()" class="btn btn-sm btn-outline" style="margin-top: 12px;">Retry</button>
                </div>
            </div>
        `;
        if (countLabel) countLabel.textContent = 'Error loading products';
    }
}

function renderPagination(totalPages, currentPage) {
    const container = document.getElementById('pagination-container');
    if (!container || totalPages <= 1) {
        if (container) container.innerHTML = '';
        return;
    }

    let buttons = '';
    if (currentPage > 0) {
        buttons += `<button class="btn btn-outline btn-sm" onclick="goToPage(${currentPage - 1})">← Prev</button>`;
    }

    const start = Math.max(0, currentPage - 2);
    const end   = Math.min(totalPages - 1, currentPage + 2);

    for (let i = start; i <= end; i++) {
        buttons += `<button class="btn ${i === currentPage ? 'btn-primary' : 'btn-outline'} btn-sm" onclick="goToPage(${i})">${i + 1}</button>`;
    }

    if (currentPage < totalPages - 1) {
        buttons += `<button class="btn btn-outline btn-sm" onclick="goToPage(${currentPage + 1})">Next →</button>`;
    }

    container.innerHTML = `<div style="display: flex; justify-content: center; gap: 8px; margin-top: 36px; flex-wrap: wrap;">${buttons}</div>`;
}

function goToPage(page) {
    currentFilters.page = page;
    loadProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function initFilterEventListeners() {
    document.getElementById('pet-category-filters')?.addEventListener('change', e => {
        currentFilters.petCategoryId = e.target.value;
        currentFilters.page = 0;
        loadProducts();
    });

    document.getElementById('product-category-filters')?.addEventListener('change', e => {
        currentFilters.productCategoryId = e.target.value;
        currentFilters.page = 0;
        loadProducts();
    });

    document.getElementById('brand-filters')?.addEventListener('change', e => {
        currentFilters.brand = e.target.value;
        currentFilters.page = 0;
        loadProducts();
    });

    document.getElementById('in-stock-only')?.addEventListener('change', e => {
        currentFilters.inStockOnly = e.target.checked;
        currentFilters.page = 0;
        loadProducts();
    });

    document.getElementById('price-filter-btn')?.addEventListener('click', () => {
        currentFilters.minPrice = document.getElementById('min-price')?.value || '';
        currentFilters.maxPrice = document.getElementById('max-price')?.value || '';
        currentFilters.page = 0;
        loadProducts();
    });

    document.getElementById('sort-by-select')?.addEventListener('change', e => {
        const val = e.target.value;
        const sortMap = {
            price_asc:  { sortBy: 'price',     sortDir: 'ASC'  },
            price_desc: { sortBy: 'price',     sortDir: 'DESC' },
            name_asc:   { sortBy: 'name',      sortDir: 'ASC'  },
            newest:     { sortBy: 'createdAt', sortDir: 'DESC' }
        };
        const sort = sortMap[val] || { sortBy: 'createdAt', sortDir: 'DESC' };
        currentFilters.sortBy  = sort.sortBy;
        currentFilters.sortDir = sort.sortDir;
        currentFilters.page = 0;
        loadProducts();
    });

    const searchInput = document.getElementById('catalog-search-input');
    const searchBtn   = document.getElementById('catalog-search-btn');

    searchBtn?.addEventListener('click', () => {
        currentFilters.keyword = searchInput?.value.trim() || '';
        currentFilters.page = 0;
        loadProducts();
    });

    searchInput?.addEventListener('keydown', e => {
        if (e.key === 'Enter') {
            currentFilters.keyword = searchInput.value.trim();
            currentFilters.page = 0;
            loadProducts();
        }
    });
}

function resetFilters() {
    currentFilters = {
        keyword: '', petCategoryId: '', productCategoryId: '',
        brand: '', minPrice: '', maxPrice: '',
        inStockOnly: false, sortBy: 'createdAt', sortDir: 'DESC', page: 0, size: 12
    };
    const searchInput = document.getElementById('catalog-search-input');
    if (searchInput) searchInput.value = '';
    const minPrice = document.getElementById('min-price');
    const maxPrice = document.getElementById('max-price');
    if (minPrice) minPrice.value = '';
    if (maxPrice) maxPrice.value = '';
    loadFilterOptions();
    loadProducts();
}

async function quickAddToCart(productId, event) {
    if (!API.isAuthenticated()) {
        API.showToast('Please login to add items to your cart', 'info');
        setTimeout(() => window.location.href = '/login.html', 1000);
        return;
    }

    const btn = event?.target;
    if (btn) { btn.disabled = true; btn.textContent = 'Adding...'; }

    try {
        await API.post('/cart/items', { productId, quantity: 1 });
        API.showToast('Added to cart! 🛍️', 'success');
        API.updateCartBadge();
    } catch (e) {
        API.showToast(e.message || 'Failed to add item', 'error');
    } finally {
        if (btn) { btn.disabled = false; btn.textContent = '🛒 Add to Cart'; }
    }
}
