/**
 * PET HUB — User Profile & Address Book Manager
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    await loadUserProfile();
    await loadProfileAddresses();
    initProfileForms();
});

async function loadUserProfile() {
    try {
        const res = await API.get('/auth/me');
        const user = res?.data;
        if (!user) throw new Error('No user data returned');

        document.getElementById('profile-first-name').value = user.firstName || '';
        document.getElementById('profile-last-name').value  = user.lastName  || '';
        document.getElementById('profile-email').value      = user.email     || '';
        document.getElementById('profile-phone').value      = user.phone     || '';
        document.getElementById('user-display-name').innerText  = `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'My Account';
        document.getElementById('user-display-email').innerText = user.email || '';
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            API.showToast('Could not load your profile. Please try refreshing.', 'error');
        }
    }
}

async function loadProfileAddresses() {
    const list = document.getElementById('profile-addresses-list');
    if (!list) return;

    list.innerHTML = `<div style="display: flex; align-items: center; gap: 8px; color: var(--dark-muted); font-size: 0.9rem;">
        <span class="loading-spinner" style="width: 18px; height: 18px; border-width: 2px;"></span> Loading addresses...
    </div>`;

    try {
        const res = await API.get('/users/addresses');
        const addresses = res?.data;

        if (!addresses || addresses.length === 0) {
            list.innerHTML = `
                <div class="alert alert-info">
                    📍 No saved addresses yet. Add one during checkout or use the form on this page.
                </div>
            `;
            return;
        }

        list.innerHTML = addresses.map(a => {
            // Jackson serializes isDefault() boolean getter as "default" in JSON
            const isDefault = a['default'] || a.isDefault;
            return `
            <div style="background: var(--light-bg); border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 16px; margin-bottom: 12px; position: relative;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; gap: 8px;">
                    <div>
                        <strong style="color: var(--secondary);">${a.fullName}</strong>
                        ${isDefault ? `<span class="status-badge status-delivered" style="font-size: 0.7rem; margin-left: 8px; vertical-align: middle;">Default</span>` : ''}
                        <div style="color: var(--dark-muted); font-size: 0.85rem; margin-top: 4px;">
                            ${a.streetAddress}, ${a.city}, ${a.state} — ${a.postalCode}
                        </div>
                        <div style="font-size: 0.82rem; color: var(--dark-muted); margin-top: 4px;">📞 ${a.phone}</div>
                    </div>
                    <div style="display: flex; flex-direction: column; gap: 6px; flex-shrink: 0;">
                        ${!isDefault ? `<button onclick="setDefaultAddr(${a.id})" class="btn btn-outline btn-sm">Set Default</button>` : ''}
                        <button onclick="deleteAddr(${a.id})" class="btn btn-sm btn-danger">Delete</button>
                    </div>
                </div>
            </div>
        `}).join('');
    } catch (e) {
        if (e.message !== 'Unauthorized') {
            list.innerHTML = `<div class="alert alert-danger">⚠️ Could not load addresses. Please try refreshing.</div>`;
        }
    }
}

async function setDefaultAddr(id) {
    try {
        await API.put(`/users/addresses/${id}/default`);
        API.showToast('Default address updated', 'success');
        await loadProfileAddresses();
    } catch (e) {
        API.showToast('Error setting default address', 'error');
    }
}

async function deleteAddr(id) {
    if (!confirm('Are you sure you want to delete this address?')) return;
    try {
        await API.delete(`/users/addresses/${id}`);
        API.showToast('Address deleted', 'info');
        await loadProfileAddresses();
    } catch (e) {
        API.showToast('Could not delete address', 'error');
    }
}

function initProfileForms() {
    // ── Profile Update ────────────────────────────────────────
    const profileForm = document.getElementById('profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = profileForm.querySelector('button[type="submit"]');
            btn.disabled = true;
            btn.textContent = 'Saving...';

            const firstName = document.getElementById('profile-first-name').value.trim();
            const lastName  = document.getElementById('profile-last-name').value.trim();
            const phone     = document.getElementById('profile-phone').value.trim();

            try {
                const res = await API.put('/users/profile', { firstName, lastName, phone });
                // Update cached user
                const cached = API.getUser() || {};
                API.setUser({ ...cached, firstName, lastName, phone });
                API.initNavbar();
                API.showToast('Profile updated successfully!', 'success');
                document.getElementById('user-display-name').innerText = `${firstName} ${lastName}`;
            } catch (err) {
                API.showToast(err.message || 'Failed to update profile', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Save Changes';
            }
        });
    }

    // ── Change Password ────────────────────────────────────────
    const passwordForm = document.getElementById('password-form');
    if (passwordForm) {
        passwordForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const btn = passwordForm.querySelector('button[type="submit"]');
            const currentPassword = document.getElementById('current-password').value;
            const newPassword     = document.getElementById('new-password').value;
            const confirmPassword = document.getElementById('confirm-password').value;

            if (newPassword.length < 6) {
                API.showToast('New password must be at least 6 characters', 'error');
                return;
            }

            if (newPassword !== confirmPassword) {
                API.showToast('New passwords do not match', 'error');
                return;
            }

            btn.disabled = true;
            btn.textContent = 'Updating...';

            try {
                await API.put('/users/change-password', { currentPassword, newPassword });
                API.showToast('Password changed successfully! 🔐', 'success');
                passwordForm.reset();
            } catch (err) {
                API.showToast(err.message || 'Incorrect current password or server error', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Update Password';
            }
        });
    }
}
