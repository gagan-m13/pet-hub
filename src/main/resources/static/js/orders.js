/**
 * PET HUB — Orders & Invoice History
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }
    await loadOrders();
});

async function loadOrders() {
    const listContainer = document.getElementById('orders-list');
    if (!listContainer) return;

    listContainer.innerHTML = `
        <div style="text-align: center; padding: 40px; color: var(--dark-muted);">
            <div class="loading-spinner" style="margin: 0 auto 12px;"></div>
            <div>Loading your orders...</div>
        </div>
    `;

    try {
        const res = await API.get('/orders');
        const orders = res?.data?.content;

        if (!orders || orders.length === 0) {
            listContainer.innerHTML = `
                <div style="text-align: center; padding: 64px 24px; background: #fff; border-radius: var(--radius-md); border: 1px solid var(--border);">
                    <div style="font-size: 3.5rem; margin-bottom: 16px;">📦</div>
                    <h2 style="color: var(--secondary); margin-bottom: 8px;">No Orders Yet</h2>
                    <p style="color: var(--dark-muted); margin-bottom: 24px;">You haven't placed any orders. Start shopping and get premium pet supplies delivered!</p>
                    <a href="/products.html" class="btn btn-primary">Start Shopping 🐾</a>
                </div>
            `;
            return;
        }

        listContainer.innerHTML = orders.map(order => `
            <div class="card" style="padding: 0; margin-bottom: 20px; overflow: hidden;">
                <!-- Order Header -->
                <div style="background: var(--light-bg); padding: 18px 24px; border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                    <div>
                        <div style="font-size: 0.75rem; font-weight: 700; color: var(--dark-muted); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 3px;">Order ID</div>
                        <div style="font-size: 1.05rem; font-weight: 800; color: var(--secondary);">${order.orderNumber}</div>
                        <div style="font-size: 0.82rem; color: var(--dark-muted); margin-top: 2px;">
                            Placed on ${new Date(order.createdAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <span class="status-badge status-${order.status.toLowerCase()}">${order.status}</span>
                        <div style="font-size: 1.3rem; font-weight: 800; color: var(--dark); margin-top: 8px;">
                            ${API.formatCurrency(order.totalAmount)}
                        </div>
                    </div>
                </div>

                <!-- Order Items -->
                <div style="padding: 20px 24px;">
                    ${(order.items || []).map(item => `
                        <div style="display: flex; align-items: center; gap: 14px; margin-bottom: 14px;">
                            <img src="${item.productImageUrl || 'https://images.unsplash.com/photo-1589924691995-400dc9eee119?w=100&auto=format&fit=crop&q=60'}"
                                 style="width: 52px; height: 52px; border-radius: var(--radius-xs); object-fit: cover; background: var(--light-bg); border: 1px solid var(--border); flex-shrink: 0;"
                                 alt="${item.productName}"
                                 onerror="this.src='https://images.unsplash.com/photo-1589924691995-400dc9eee119?w=100&auto=format&fit=crop&q=60'">
                            <div style="flex: 1; min-width: 0;">
                                <div style="font-weight: 700; font-size: 0.9rem; color: var(--dark); white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${item.productName}</div>
                                <div style="font-size: 0.8rem; color: var(--dark-muted); margin-top: 2px;">
                                    Qty: ${item.quantity} × ${API.formatCurrency(item.unitPrice)}
                                </div>
                            </div>
                            <div style="font-weight: 800; font-size: 0.95rem; color: var(--secondary); flex-shrink: 0;">
                                ${API.formatCurrency(item.totalPrice)}
                            </div>
                        </div>
                    `).join('')}
                </div>

                <!-- Order Footer -->
                <div style="border-top: 1px solid var(--border); padding: 14px 24px; background: var(--light-bg); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px;">
                    <div style="font-size: 0.85rem; color: var(--dark-muted);">
                        📍 ${order.shippingAddress ? `${order.shippingAddress.fullName}, ${order.shippingAddress.city}, ${order.shippingAddress.state}` : 'Address on file'}
                        &nbsp;&bull;&nbsp; 💳 ${order.paymentMethod?.replace(/_/g, ' ') || 'N/A'}
                    </div>
                    <div>
                        ${(order.status === 'PLACED' || order.status === 'CONFIRMED') ? `
                            <button onclick="cancelUserOrder(${order.id})" class="btn btn-sm btn-danger">
                                Cancel Order
                            </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        `).join('');
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            listContainer.innerHTML = `
                <div class="alert alert-danger">
                    ⚠️ Could not load your orders. Please try refreshing the page.
                </div>
            `;
        }
    }
}

async function cancelUserOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order? This cannot be undone.')) return;
    try {
        await API.put(`/orders/${orderId}/cancel`);
        API.showToast('Order cancelled successfully. Inventory has been restocked.', 'info');
        await loadOrders();
    } catch (err) {
        API.showToast(err.message || 'Cannot cancel this order at this stage', 'error');
    }
}
