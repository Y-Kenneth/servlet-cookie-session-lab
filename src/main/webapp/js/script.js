// Show/hide the days dropdown based on checkbox state
function toggleDays(checkbox) {
    document.getElementById('daysGroup').classList.toggle('visible', checkbox.checked);
}

// Read ?error= param from URL and show appropriate message
(function showError() {
    const params = new URLSearchParams(window.location.search);
    const error  = params.get('error');
    const box    = document.getElementById('errorBox');
    const msgs   = {
        name:    '⚠️ Please enter a valid name.',
        age:     '⚠️ Please enter a valid age (1–150).',
        session: '⚠️ Session expired. Please log in again.'
    };
    if (msgs[error]) {
        box.textContent = msgs[error];
        box.style.display = 'block';
    }
})();

// Pre-fill from cookies if they exist (set by Remember Me)
(function prefillFromCookies() {
    const getCookie = name => {
        const match = document.cookie.split('; ').find(r => r.startsWith(name + '='));
        return match ? decodeURIComponent(match.split('=')[1]) : null;
    };
    const savedName = getCookie('savedName');
    const savedAge  = getCookie('savedAge');
    if (savedName) document.getElementById('name').value = savedName;
    if (savedAge)  document.getElementById('age').value  = savedAge;
    if (savedName || savedAge) {
        // Auto-check the remember me box since cookies are present
        const cb = document.getElementById('rememberMe');
        cb.checked = true;
        toggleDays(cb);
    }
})();