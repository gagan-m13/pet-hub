/**
 * PET HUB — Admin Order Fulfillment & Status Management
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }

    await loadAdminOrders();
    initFilterListeners();
});

async function loadAdminOrders(status = '', query = '') {
    const tbody = document.getElementById('admin-orders-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/admin/orders', { status, query, size: 50 });
        const orders = res.data.content;

        if (!orders || orders.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 24px;">No orders found.</td></tr>`;
            return;
        }

        tbody.innerHTML = orders.map(o => `
            <tr>
                <td><strong>${o.orderNumber}</strong></td>
                <td>
                    <div>${o.userName || 'Customer'}</div>
                    <small style="color: var(--dark-muted);">${o.userEmail}</small>
                </td>
                <td>
                    <div style="font-size: 0.85rem;">
                        ${o.shippingAddress?.fullName}, ${o.shippingAddress?.city} (📞 ${o.shippingAddress?.phone})
                    </div>
                </td>
                <td>${API.formatCurrency(o.totalAmount)}</td>
                <td>
                    <select onchange="updateStatus(${o.id}, this.value)" class="form-control" style="padding: 4px 8px; font-size: 0.85rem; font-weight: 700;">
                        <option value="PLACED" ${o.status === 'PLACED' ? 'selected' : ''}>PLACED</option>
                        <option value="CONFIRMED" ${o.status === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                        <option value="PACKED" ${o.status === 'PACKED' ? 'selected' : ''}>PACKED</option>
                        <option value="SHIPPED" ${o.status === 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                        <option value="DELIVERED" ${o.status === 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                        <option value="CANCELLED" ${o.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                    </select>
                </td>
                <td><small>${new Date(o.createdAt).toLocaleString()}</small></td>
                <td>
                    <button onclick="viewOrderItemsModal(${JSON.stringify(o.items).replace(/"/g, '&quot;')})" class="btn btn-outline btn-sm">
                        View Items (${o.itemCount})
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load orders', 'error');
    }
}

async function updateStatus(orderId, status) {
    try {
        await API.put(`/admin/orders/${orderId}/status`, { status });
        API.showToast(`Order status updated to ${status}!`, 'success');
    } catch (err) {
        API.showToast(err.message || 'Status update failed', 'error');
        loadAdminOrders();
    }
}

function viewOrderItemsModal(items) {
    const list = document.getElementById('order-modal-items-list');
    if (!list) return;

    list.innerHTML = items.map(i => `
        <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid var(--border);">
            <div style="display: flex; align-items: center; gap: 12px;">
                <img src="${i.productImageUrl}" style="width: 40px; height: 40px; border-radius: 4px; object-fit: cover;">
                <div>
                    <strong>${i.productName}</strong>
                    <div style="font-size: 0.8rem; color: var(--dark-muted);">SKU: ${i.productSku}</div>
                </div>
            </div>
            <div>${i.quantity} × ${API.formatCurrency(i.unitPrice)} = <strong>${API.formatCurrency(i.totalPrice)}</strong></div>
        </div>
    `).join('');

    document.getElementById('order-details-modal').classList.add('open');
}

function closeOrderModal() {
    document.getElementById('order-details-modal').classList.remove('open');
}

function initFilterListeners() {
    document.getElementById('order-status-filter')?.addEventListener('change', (e) => {
        loadAdminOrders(e.target.value, document.getElementById('order-search-input')?.value || '');
    });

    document.getElementById('order-search-btn')?.addEventListener('click', () => {
        loadAdminOrders(document.getElementById('order-status-filter')?.value || '', document.getElementById('order-search-input')?.value || '');
    });
}
