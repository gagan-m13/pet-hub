/**
 * PET HUB — Admin User Management
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!API.isAdmin()) {
        window.location.href = '/login.html';
        return;
    }

    await loadAdminUsers();
    initUserSearch();
});

async function loadAdminUsers(query = '') {
    const tbody = document.getElementById('admin-users-tbody');
    if (!tbody) return;

    try {
        const res = await API.get('/admin/users', { query, size: 50 });
        const users = res.data.content;

        tbody.innerHTML = users.map(u => `
            <tr>
                <td><strong>#${u.id}</strong></td>
                <td>${u.firstName} ${u.lastName}</td>
                <td>${u.email}</td>
                <td>${u.phone || '—'}</td>
                <td>
                    ${u.roles.map(r => `<span class="badge" style="position: static; display: inline-block; background: ${r.includes('ADMIN') ? 'var(--primary)' : 'var(--secondary)'}; margin-right: 4px;">${r.replace('ROLE_', '')}</span>`).join('')}
                </td>
                <td>
                    <span class="badge" style="position: static; display: inline-block; background: ${u.enabled ? 'var(--success)' : 'var(--danger)'};">
                        ${u.enabled ? 'Active' : 'Disabled'}
                    </span>
                </td>
                <td>
                    <button onclick="toggleUser(${u.id})" class="btn btn-outline btn-sm">
                        ${u.enabled ? 'Disable' : 'Enable'}
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        API.showToast('Failed to load users', 'error');
    }
}

async function toggleUser(userId) {
    try {
        await API.put(`/admin/users/${userId}/toggle-status`);
        API.showToast('User status updated', 'success');
        loadAdminUsers(document.getElementById('user-search-input')?.value || '');
    } catch (err) {
        API.showToast(err.message || 'Action failed', 'error');
    }
}

function initUserSearch() {
    document.getElementById('user-search-btn')?.addEventListener('click', () => {
        loadAdminUsers(document.getElementById('user-search-input')?.value || '');
    });
}
