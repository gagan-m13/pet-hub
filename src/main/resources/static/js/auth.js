/**
 * PET HUB — Authentication Logic (Login & Registration)
 */

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitBtn = loginForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerText = 'Signing in...';

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;

            try {
                const response = await API.post('/auth/login', { email, password });
                API.setToken(response.data.token);
                API.setUser(response.data);
                API.showToast('Login successful! Welcome back.', 'success');

                setTimeout(() => {
                    if (response.data.roles && response.data.roles.includes('ROLE_ADMIN')) {
                        window.location.href = '/admin/dashboard.html';
                    } else {
                        window.location.href = '/index.html';
                    }
                }, 800);
            } catch (err) {
                API.showToast(err.message || 'Invalid credentials. Please try again.', 'error');
                submitBtn.disabled = false;
                submitBtn.innerText = 'Sign In';
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitBtn = registerForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerText = 'Creating account...';

            const firstName = document.getElementById('first-name').value.trim();
            const lastName = document.getElementById('last-name').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const phone = document.getElementById('phone').value.trim();

            try {
                const response = await API.post('/auth/register', {
                    firstName,
                    lastName,
                    email,
                    password,
                    phone
                });

                API.setToken(response.data.token);
                API.setUser(response.data);
                API.showToast('Account created successfully! Welcome to PET HUB.', 'success');

                setTimeout(() => {
                    window.location.href = '/index.html';
                }, 800);
            } catch (err) {
                API.showToast(err.message || 'Registration failed. Please check your inputs.', 'error');
                submitBtn.disabled = false;
                submitBtn.innerText = 'Create Account';
            }
        });
    }
});
