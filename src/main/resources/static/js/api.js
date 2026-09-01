/**
 * PET HUB — Centralized API Client & Utility Module
 */

const API_BASE = '/api';

const API = {
    getToken() {
        return localStorage.getItem('pet_hub_token');
    },

    setToken(token) {
        localStorage.setItem('pet_hub_token', token);
    },

    getUser() {
        try {
            const user = localStorage.getItem('pet_hub_user');
            return user ? JSON.parse(user) : null;
        } catch { return null; }
    },

    setUser(user) {
        localStorage.setItem('pet_hub_user', JSON.stringify(user));
    },

    clearAuth() {
        localStorage.removeItem('pet_hub_token');
        localStorage.removeItem('pet_hub_user');
    },

    isAuthenticated() {
        return !!this.getToken();
    },

    isAdmin() {
        const user = this.getUser();
        return user && Array.isArray(user.roles) && user.roles.includes('ROLE_ADMIN');
    },

    async request(endpoint, options = {}) {
        // Build full URL
        let url;
        if (endpoint.startsWith('http')) {
            url = endpoint;
        } else {
            url = window.location.origin + (endpoint.startsWith('/api') ? endpoint : API_BASE + endpoint);
        }

        const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        // Let browser set multipart boundary
        if (options.body instanceof FormData) {
            delete headers['Content-Type'];
        }

        try {
            const response = await fetch(url, { ...options, headers });

            // Handle 401 — session expired
            if (response.status === 401 && !endpoint.includes('/auth/login')) {
                this.clearAuth();
                this.showToast('Session expired. Please login again.', 'error');
                setTimeout(() => {
                    if (!window.location.pathname.includes('login.html')) {
                        window.location.href = '/login.html';
                    }
                }, 1400);
                throw new Error('Unauthorized');
            }

            const data = await response.json().catch(() => null);

            if (!response.ok) {
                const msg = (data && data.message) ? data.message : `Error ${response.status}`;
                throw new Error(msg);
            }

            return data;
        } catch (error) {
            if (error.message !== 'Unauthorized') {
                console.error(`[API Error] ${endpoint}:`, error.message);
            }
            throw error;
        }
    },

    // GET — builds URL with query string params correctly
    get(endpoint, params = {}) {
        const base = window.location.origin + (endpoint.startsWith('/api') ? endpoint : API_BASE + endpoint);
        const url = new URL(base);
        Object.entries(params).forEach(([key, val]) => {
            if (val !== undefined && val !== null && val !== '') {
                url.searchParams.append(key, val);
            }
        });
        return this.request(url.pathname + url.search, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint.startsWith('/api') ? endpoint : API_BASE + endpoint, {
            method: 'POST',
            body: body instanceof FormData ? body : JSON.stringify(body)
        });
    },

    put(endpoint, body) {
        // Support endpoints that already carry query params (e.g. /cart/items/5?quantity=2)
        const fullEndpoint = endpoint.startsWith('/api') ? endpoint : API_BASE + endpoint;
        return this.request(fullEndpoint, {
            method: 'PUT',
            body: (body === undefined || body === null) ? undefined
                  : body instanceof FormData ? body
                  : JSON.stringify(body)
        });
    },

    delete(endpoint) {
        const fullEndpoint = endpoint.startsWith('/api') ? endpoint : API_BASE + endpoint;
        return this.request(fullEndpoint, { method: 'DELETE' });
    },

    // ─── Toast Notifications ────────────────────────────────────────────────
    showToast(message, type = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const icons = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
        const icon = icons[type] || 'ℹ️';

        toast.innerHTML = `<span class="toast-icon">${icon}</span><span class="toast-msg">${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.classList.add('toast-fade-out');
            setTimeout(() => toast.remove(), 350);
        }, 3500);
    },

    // ─── Cart Badge ────────────────────────────────────────────────────────
    updateCartBadge() {
        const badge = document.getElementById('cart-badge');
        if (!badge) return;

        if (!this.isAuthenticated()) {
            badge.textContent = '0';
            badge.style.display = 'none';
            return;
        }

        this.get('/cart')
            .then(res => {
                const qty = res?.data?.totalQuantity ?? 0;
                badge.textContent = qty;
                badge.style.display = qty > 0 ? 'flex' : 'none';
            })
            .catch(() => {
                badge.textContent = '0';
                badge.style.display = 'none';
            });
    },

    // ─── Currency ──────────────────────────────────────────────────────────
    formatCurrency(amount) {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 2
        }).format(amount || 0);
    },

    // ─── Navbar ────────────────────────────────────────────────────────────
    initNavbar() {
        const user = this.getUser();
        const authContainer = document.getElementById('nav-auth-section');
        if (!authContainer) return;

        if (this.isAuthenticated() && user) {
            let adminLink = '';
            if (this.isAdmin()) {
                adminLink = `<a href="/admin/dashboard.html" class="nav-link nav-admin">⚙️ Admin</a>`;
            }
            authContainer.innerHTML = `
                ${adminLink}
                <a href="/orders.html" class="nav-link">📦 Orders</a>
                <a href="/profile.html" class="nav-link">👤 ${user.firstName || 'Account'}</a>
                <button id="logout-btn" class="btn btn-outline btn-sm">Logout</button>
            `;
            document.getElementById('logout-btn')?.addEventListener('click', () => {
                this.clearAuth();
                this.showToast('Logged out successfully', 'info');
                setTimeout(() => window.location.href = '/index.html', 700);
            });
        } else {
            authContainer.innerHTML = `
                <a href="/login.html" class="btn btn-outline btn-sm">Login</a>
                <a href="/register.html" class="btn btn-primary btn-sm">Sign Up</a>
            `;
        }

        this.updateCartBadge();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    API.initNavbar();
});
