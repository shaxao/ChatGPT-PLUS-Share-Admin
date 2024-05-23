const base_url = "http://localhost:9090"
//const base_url = "https://ttzi.top"

// 添加点击事件监听器
document.getElementById('googleLogin').addEventListener('click', function() {
    const authorizationUrl = `/oauth2/authorization/google`;
    window.location.href = authorizationUrl;
});

document.getElementById('githubLogin').addEventListener('click', function() {
    const authorizationUrl = `/oauth2/authorization/github`;
    window.location.href = authorizationUrl;
});

