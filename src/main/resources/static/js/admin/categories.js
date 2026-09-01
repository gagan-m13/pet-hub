/**
 * PET HUB — Admin Category Management (Pet & Product Categories)
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }

    await loadPetCategories();
    await loadProductCategories();
    initCategoryForms();
});

async function loadPetCategories() {
    const tbody = document.getElementById('pet-categories-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/pet-categories');
        const categories = res.data;

        tbody.innerHTML = categories.map(c => `
            <tr>
                <td><img src="${c.imageUrl || 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=200'}" style="width: 40px; height: 40px; border-radius: 6px; object-fit: cover;"></td>
                <td><strong>${c.name}</strong></td>
                <td><code>${c.slug}</code></td>
                <td><small style="color: var(--dark-muted);">${c.description || '—'}</small></td>
                <td>
                    <button onclick="deletePetCategory(${c.id})" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load pet categories', 'error');
    }
}

async function loadProductCategories() {
    const tbody = document.getElementById('product-categories-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/categories');
        const categories = res.data;

        tbody.innerHTML = categories.map(c => `
            <tr>
                <td><img src="${c.imageUrl || 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=200'}" style="width: 40px; height: 40px; border-radius: 6px; object-fit: cover;"></td>
                <td><strong>${c.name}</strong></td>
                <td><code>${c.slug}</code></td>
                <td><small style="color: var(--dark-muted);">${c.description || '—'}</small></td>
                <td>
                    <button onclick="deleteProductCategory(${c.id})" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load product categories', 'error');
    }
}

function initCategoryForms() {
    document.getElementById('add-pet-cat-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('pet-cat-name').value.trim();
        const description = document.getElementById('pet-cat-desc').value.trim();
        const imageUrl = document.getElementById('pet-cat-img').value.trim();

        try {
            await API.post('/admin/pet-categories', { name, description, imageUrl });
            API.showToast('Pet category created successfully!', 'success');
            e.target.reset();
            loadPetCategories();
        } catch (err) {
            API.showToast(err.message || 'Failed to create category', 'error');
        }
    });

    document.getElementById('add-prod-cat-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('prod-cat-name').value.trim();
        const description = document.getElementById('prod-cat-desc').value.trim();
        const imageUrl = document.getElementById('prod-cat-img').value.trim();

        try {
            await API.post('/admin/categories', { name, description, imageUrl });
            API.showToast('Product category created successfully!', 'success');
            e.target.reset();
            loadProductCategories();
        } catch (err) {
            API.showToast(err.message || 'Failed to create category', 'error');
        }
    });
}

async function deletePetCategory(id) {
    if (!confirm('Are you sure you want to delete this pet category?')) return;
    try {
        await API.delete(`/admin/pet-categories/${id}`);
        API.showToast('Pet category deleted', 'info');
        loadPetCategories();
    } catch (err) {
        API.showToast(err.message || 'Cannot delete category in use', 'error');
    }
}

async function deleteProductCategory(id) {
    if (!confirm('Are you sure you want to delete this product category?')) return;
    try {
        await API.delete(`/admin/categories/${id}`);
        API.showToast('Product category deleted', 'info');
        loadProductCategories();
    } catch (err) {
        API.showToast(err.message || 'Cannot delete category in use', 'error');
    }
}
