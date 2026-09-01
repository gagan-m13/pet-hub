/**
 * PET HUB — Checkout Process
 */

let selectedAddressId = null;

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    await loadOrderSummary();
    await loadUserAddresses();
    initNewAddressForm();
    initPlaceOrderBtn();
});

async function loadOrderSummary() {
    try {
        const res = await API.get('/cart');
        const cart = res?.data;

        if (!cart || !cart.items || cart.items.length === 0) {
            window.location.href = '/cart.html';
            return;
        }

        const itemsList = document.getElementById('checkout-items-list');
        if (itemsList) {
            itemsList.innerHTML = cart.items.map(item => `
                <div class="checkout-item-row">
                    <div>
                        <div class="checkout-item-name">${item.productName}</div>
                        <div class="checkout-item-qty">Qty: ${item.quantity} × ${API.formatCurrency(item.unitPrice)}</div>
                    </div>
                    <span class="checkout-item-price">${API.formatCurrency(item.subtotal)}</span>
                </div>
            `).join('');
        }

        document.getElementById('checkout-subtotal').innerText = API.formatCurrency(cart.subtotal);
        document.getElementById('checkout-shipping').innerText =
            (!cart.shippingFee || cart.shippingFee == 0) ? 'FREE 🎉' : API.formatCurrency(cart.shippingFee);
        document.getElementById('checkout-tax').innerText = API.formatCurrency(cart.estimatedTax);
        document.getElementById('checkout-total').innerText = API.formatCurrency(cart.totalAmount);
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            API.showToast('Could not load checkout data. Please go back to cart.', 'error');
        }
    }
}

async function loadUserAddresses() {
    const grid = document.getElementById('addresses-grid');
    if (!grid) return;

    try {
        const res = await API.get('/users/addresses');
        const addresses = res?.data;

        if (!addresses || addresses.length === 0) {
            grid.innerHTML = `
                <div class="alert alert-info" style="grid-column: 1/-1;">
                    ℹ️ No saved addresses yet. Please add your delivery address below.
                </div>
            `;
            return;
        }

        grid.innerHTML = addresses.map((addr, idx) => {
            // Jackson serializes isDefault() boolean getter as "default" (strips "is" prefix)
            const isDefault = addr['default'] || addr.isDefault;
            const isSelected = isDefault || (idx === 0 && !selectedAddressId);
            if (isSelected && !selectedAddressId) selectedAddressId = addr.id;

            return `
                <div class="address-card ${selectedAddressId === addr.id ? 'selected' : ''}"
                     onclick="selectAddress(${addr.id}, this)">
                    ${isDefault ? `<span class="status-badge status-delivered" style="position: absolute; top: 10px; right: 10px; font-size: 0.7rem;">Default</span>` : ''}
                    <h4>${addr.fullName}</h4>
                    <p>${addr.streetAddress}, ${addr.city}</p>
                    <p>${addr.state} — ${addr.postalCode}</p>
                    <p style="margin-top: 6px; color: var(--dark-muted); font-size: 0.82rem;">📞 ${addr.phone}</p>
                </div>
            `;
        }).join('');
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            grid.innerHTML = `<div class="alert alert-danger" style="grid-column: 1/-1;">Failed to load addresses. Please add one below.</div>`;
        }
    }
}

function selectAddress(id, el) {
    selectedAddressId = id;
    document.querySelectorAll('.address-card').forEach(c => c.classList.remove('selected'));
    el.classList.add('selected');
}

function initNewAddressForm() {
    const form = document.getElementById('new-address-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = form.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Saving...';

        const payload = {
            fullName:      document.getElementById('addr-name').value.trim(),
            phone:         document.getElementById('addr-phone').value.trim(),
            streetAddress: document.getElementById('addr-street').value.trim(),
            city:          document.getElementById('addr-city').value.trim(),
            state:         document.getElementById('addr-state').value.trim(),
            postalCode:    document.getElementById('addr-postal').value.trim(),
            country:       'India',
            isDefault:     document.getElementById('addr-default').checked
        };

        try {
            const res = await API.post('/users/addresses', payload);
            API.showToast('Address saved successfully!', 'success');
            selectedAddressId = res.data.id;
            form.reset();
            await loadUserAddresses();
        } catch (err) {
            API.showToast(err.message || 'Failed to save address', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Save & Select Address';
        }
    });
}

function initPlaceOrderBtn() {
    const btn = document.getElementById('place-order-btn');
    if (!btn) return;

    btn.addEventListener('click', async () => {
        if (!selectedAddressId) {
            API.showToast('Please select or add a shipping address first', 'error');
            return;
        }

        const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value || 'CASH_ON_DELIVERY';

        btn.disabled = true;
        btn.innerHTML = '<span class="loading-spinner"></span>&nbsp; Processing...';

        try {
            const response = await API.post('/orders', {
                shippingAddressId: selectedAddressId,
                paymentMethod
            });

            API.showToast('🎉 Order placed successfully!', 'success');
            API.updateCartBadge();

            setTimeout(() => {
                window.location.href = `/orders.html`;
            }, 1200);
        } catch (err) {
            API.showToast(err.message || 'Failed to place order. Check item availability.', 'error');
            btn.disabled = false;
            btn.innerHTML = 'Place Order 🐾';
        }
    });
}
