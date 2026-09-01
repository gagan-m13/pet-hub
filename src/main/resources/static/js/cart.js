/**
 * PET HUB — Shopping Cart Manager
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }
    await loadCart();
});

async function loadCart() {
    const tableBody = document.getElementById('cart-items-tbody');
    if (!tableBody) return;

    try {
        const res = await API.get('/cart');
        const cart = res?.data;

        if (!cart || !cart.items || cart.items.length === 0) {
            document.getElementById('cart-content-wrapper').innerHTML = `
                <div class="cart-empty-state">
                    <div class="empty-icon">🛒</div>
                    <h2>Your Cart is Empty</h2>
                    <p>Explore our catalog and find the perfect products for your furry friends!</p>
                    <a href="/products.html" class="btn btn-primary" style="margin-top: 8px;">Start Shopping 🐾</a>
                </div>
            `;
            API.updateCartBadge();
            return;
        }

        tableBody.innerHTML = cart.items.map(item => `
            <tr>
                <td>
                    <div class="cart-item-info">
                        <img src="${item.productImageUrl || 'https://images.unsplash.com/photo-1589924691995-400dc9eee119?w=200&auto=format&fit=crop&q=60'}"
                             class="cart-item-img" alt="${item.productName}"
                             onerror="this.src='https://images.unsplash.com/photo-1589924691995-400dc9eee119?w=200&auto=format&fit=crop&q=60'">
                        <div>
                            <a href="/product-details.html?id=${item.productId}" class="cart-item-name">
                                ${item.productName}
                            </a>
                            <div class="cart-item-meta">${item.productBrand || 'PET HUB'} &bull; SKU: ${item.productSku || '—'}</div>
                        </div>
                    </div>
                </td>
                <td style="font-weight: 600; color: var(--dark-muted); font-size: 0.9rem;">
                    ${API.formatCurrency(item.unitPrice)}
                </td>
                <td>
                    <div class="quantity-control">
                        <button class="qty-btn" onclick="updateQty(${item.id}, ${item.quantity - 1})">−</button>
                        <input type="text" class="qty-input" value="${item.quantity}" readonly>
                        <button class="qty-btn" onclick="updateQty(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                </td>
                <td style="font-weight: 800; color: var(--secondary); font-size: 1rem;">
                    ${API.formatCurrency(item.subtotal)}
                </td>
                <td>
                    <button onclick="removeCartItem(${item.id})"
                            class="btn btn-sm"
                            title="Remove item"
                            style="background: #fee2e2; color: var(--danger); border: none; padding: 7px 10px;">
                        🗑
                    </button>
                </td>
            </tr>
        `).join('');

        // Update summary
        document.getElementById('cart-subtotal').innerText = API.formatCurrency(cart.subtotal);
        document.getElementById('cart-shipping').innerText =
            (!cart.shippingFee || cart.shippingFee == 0) ? 'FREE' : API.formatCurrency(cart.shippingFee);
        document.getElementById('cart-tax').innerText = API.formatCurrency(cart.estimatedTax);
        document.getElementById('cart-total').innerText = API.formatCurrency(cart.totalAmount);

        API.updateCartBadge();
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            API.showToast('Could not load cart. Please refresh.', 'error');
        }
    }
}

async function updateQty(itemId, newQty) {
    if (newQty < 1) {
        removeCartItem(itemId);
        return;
    }
    try {
        // Use request directly to pass quantity as query param (as backend expects @RequestParam)
        await API.request(`/api/cart/items/${itemId}?quantity=${newQty}`, { method: 'PUT' });
        await loadCart();
    } catch (err) {
        API.showToast(err.message || 'Cannot update quantity', 'error');
    }
}

async function removeCartItem(itemId) {
    try {
        await API.delete(`/cart/items/${itemId}`);
        API.showToast('Item removed from cart', 'info');
        await loadCart();
    } catch (err) {
        API.showToast('Failed to remove item', 'error');
    }
}
