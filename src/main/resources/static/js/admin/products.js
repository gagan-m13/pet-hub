/**
 * PET HUB — Admin Product Management & Image Uploads
 */

let editingProductId = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }

    await loadCategorySelects();
    await loadAdminProducts();
    initProductForm();
});

async function loadCategorySelects() {
    try {
        const [petRes, prodRes] = await Promise.all([
            API.get('/pet-categories'),
            API.get('/categories')
        ]);

        const petSelect = document.getElementById('prod-pet-category');
        if (petSelect && petRes.data) {
            petSelect.innerHTML = petRes.data.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        }

        const prodSelect = document.getElementById('prod-product-category');
        if (prodSelect && prodRes.data) {
            prodSelect.innerHTML = prodRes.data.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        }
    } catch (e) {}
}

async function loadAdminProducts() {
    const tbody = document.getElementById('admin-products-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/products', { size: 50, sortBy: 'createdAt', sortDir: 'DESC' });
        const products = res.data.content;

        tbody.innerHTML = products.map(p => `
            <tr>
                <td><img src="${p.primaryImageUrl}" style="width: 44px; height: 44px; border-radius: 6px; object-fit: cover;"></td>
                <td>
                    <strong>${p.name}</strong>
                    <div style="font-size: 0.8rem; color: var(--dark-muted);">${p.brand} • SKU: ${p.sku}</div>
                </td>
                <td>${p.petCategory?.name} / ${p.productCategory?.name}</td>
                <td>${API.formatCurrency(p.effectivePrice)}</td>
                <td>
                    <span style="font-weight: 700; color: ${p.stockQuantity <= 5 ? 'var(--danger)' : 'var(--success)'};">
                        ${p.stockQuantity}
                    </span>
                </td>
                <td>
                    <span class="badge" style="position: static; display: inline-block; background: ${p.active ? 'var(--success)' : 'var(--danger)'};">
                        ${p.active ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <div style="display: flex; gap: 8px;">
                        <button onclick="editProduct(${p.id})" class="btn btn-outline btn-sm">Edit</button>
                        <button onclick="deleteProduct(${p.id})" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);">Delete</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load products', 'error');
    }
}

function openAddProductModal() {
    editingProductId = null;
    document.getElementById('product-modal-title').innerText = 'Add New Product';
    document.getElementById('product-form').reset();
    document.getElementById('product-modal').classList.add('open');
}

function closeProductModal() {
    document.getElementById('product-modal').classList.remove('open');
}

async function editProduct(id) {
    try {
        const res = await API.get(`/products/${id}`);
        const p = res.data;
        editingProductId = p.id;

        document.getElementById('product-modal-title').innerText = 'Edit Product';
        document.getElementById('prod-name').value = p.name;
        document.getElementById('prod-brand').value = p.brand || '';
        document.getElementById('prod-sku').value = p.sku;
        document.getElementById('prod-price').value = p.price;
        document.getElementById('prod-discount-price').value = p.discountPrice || '';
        document.getElementById('prod-stock').value = p.stockQuantity;
        document.getElementById('prod-description').value = p.description;
        document.getElementById('prod-pet-category').value = p.petCategory?.id;
        document.getElementById('prod-product-category').value = p.productCategory?.id;
        document.getElementById('prod-active').checked = p.active;
        document.getElementById('prod-featured').checked = p.featured;

        document.getElementById('product-modal').classList.add('open');
    } catch (e) {
        API.showToast('Could not load product details', 'error');
    }
}

function initProductForm() {
    const form = document.getElementById('product-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            name: document.getElementById('prod-name').value.trim(),
            brand: document.getElementById('prod-brand').value.trim(),
            sku: document.getElementById('prod-sku').value.trim(),
            price: parseFloat(document.getElementById('prod-price').value),
            discountPrice: document.getElementById('prod-discount-price').value ? parseFloat(document.getElementById('prod-discount-price').value) : null,
            stockQuantity: parseInt(document.getElementById('prod-stock').value),
            description: document.getElementById('prod-description').value.trim(),
            petCategoryId: parseInt(document.getElementById('prod-pet-category').value),
            productCategoryId: parseInt(document.getElementById('prod-product-category').value),
            active: document.getElementById('prod-active').checked,
            featured: document.getElementById('prod-featured').checked,
            imageUrls: []
        };

        const imageInput = document.getElementById('prod-image-file');
        const imageUrlInput = document.getElementById('prod-image-url');

        try {
            // Upload file if selected
            if (imageInput && imageInput.files && imageInput.files[0]) {
                const formData = new FormData();
                formData.append('file', imageInput.files[0]);
                const uploadRes = await API.post('/admin/products/upload-image', formData);
                payload.imageUrls.push(uploadRes.data.imageUrl);
            } else if (imageUrlInput && imageUrlInput.value.trim()) {
                payload.imageUrls.push(imageUrlInput.value.trim());
            }

            if (editingProductId) {
                await API.put(`/admin/products/${editingProductId}`, payload);
                API.showToast('Product updated successfully!', 'success');
            } else {
                await API.post('/admin/products', payload);
                API.showToast('Product created successfully!', 'success');
            }

            closeProductModal();
            loadAdminProducts();
        } catch (err) {
            API.showToast(err.message || 'Operation failed', 'error');
        }
    });
}

async function deleteProduct(id) {
    if (!confirm('Are you sure you want to deactivate/delete this product?')) return;
    try {
        await API.delete(`/admin/products/${id}`);
        API.showToast('Product deactivated successfully', 'info');
        loadAdminProducts();
    } catch (err) {
        API.showToast(err.message || 'Cannot delete product', 'error');
    }
}
