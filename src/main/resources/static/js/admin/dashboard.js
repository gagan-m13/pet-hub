/**
 * PET HUB — Admin Analytics Dashboard
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }
    await loadDashboardData();
});

async function loadDashboardData() {
    try {
        const res = await API.get('/admin/dashboard');
        const stats = res?.data;
        if (!stats) return;

        document.getElementById('stat-total-users').innerText    = stats.totalUsers      || 0;
        document.getElementById('stat-total-products').innerText = stats.totalProducts   || 0;
        document.getElementById('stat-total-orders').innerText   = stats.totalOrders     || 0;
        document.getElementById('stat-total-revenue').innerText  = API.formatCurrency(stats.totalRevenue);
        document.getElementById('stat-pending-orders').innerText = stats.pendingOrders   || 0;
        document.getElementById('stat-low-stock').innerText      = stats.lowStockProducts || 0;

        // Recent Orders Table
        const ordersTbody = document.getElementById('recent-orders-tbody');
        if (ordersTbody && stats.recentOrders) {
            ordersTbody.innerHTML = stats.recentOrders.map(o => `
                <tr>
                    <td><strong>${o.orderNumber}</strong></td>
                    <td>${o.userName || o.userEmail}</td>
                    <td>${API.formatCurrency(o.totalAmount)}</td>
                    <td><span class="status-badge status-${(o.status || '').toLowerCase()}">${o.status}</span></td>
                    <td>${new Date(o.createdAt).toLocaleDateString()}</td>
                </tr>
            `).join('');
        }

        // Low stock list
        const lowStockTbody = document.getElementById('low-stock-tbody');
        if (lowStockTbody && stats.lowStockProductList) {
            lowStockTbody.innerHTML = stats.lowStockProductList.map(p => `
                <tr>
                    <td><img src="${p.primaryImageUrl}" class="table-thumb" alt="${p.name}"></td>
                    <td><strong>${p.name}</strong></td>
                    <td>${p.sku}</td>
                    <td><span style="color: var(--danger); font-weight: 700;">${p.stockQuantity}</span></td>
                </tr>
            `).join('');
        }
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            API.showToast('Failed to load admin stats. Please refresh.', 'error');
        }
    }
}
