/**
 * PET HUB — Admin Review Moderation
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }
    await loadAdminReviews();
});

async function loadAdminReviews() {
    const tbody = document.getElementById('admin-reviews-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/admin/reviews', { size: 50 });
        const reviews = res.data.content;

        if (!reviews || reviews.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 24px;">No reviews found.</td></tr>`;
            return;
        }

        tbody.innerHTML = reviews.map(r => `
            <tr>
                <td><strong>${r.productName || 'Product #' + r.productId}</strong></td>
                <td>${r.userName || 'Customer'}</td>
                <td><span style="color: #f59e0b; font-weight: 700;">★ ${r.rating}</span></td>
                <td><small>${r.comment}</small></td>
                <td><small>${new Date(r.createdAt).toLocaleDateString()}</small></td>
                <td>
                    <button onclick="deleteAdminReview(${r.id})" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);">
                        Remove
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load reviews', 'error');
    }
}

async function deleteAdminReview(id) {
    if (!confirm('Are you sure you want to remove this review?')) return;
    try {
        await API.delete(`/admin/reviews/${id}`);
        API.showToast('Review removed', 'info');
        loadAdminReviews();
    } catch (err) {
        API.showToast(err.message || 'Cannot delete review', 'error');
    }
}
