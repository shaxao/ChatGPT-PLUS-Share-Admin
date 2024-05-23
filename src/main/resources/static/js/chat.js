function sendMessage() {
    // 获取用户输入的消息
    var messageInput = document.getElementById('message');
    var userMessage = messageInput.value;

    // 获取用户头像和用户名
    var userAvatar = "/static/images/muhuo-logo.jpg";  // 替换为实际路径
    var userName = "User 1";  // 替换为实际用户名

    // 构建用户消息的 HTML 结构
    var userMessageHTML = `
        <div class="message sent">
            <div class="user-info">
                <img src="${userAvatar}">
                <div class="username">${userName}</div>
            </div>
            <div class="user-content" onmouseover="showEditIcon(this)" onmouseout="hideEditIcon(this)">${userMessage}</div>
             <span class="edit-icon" onclick="editContent(this)"  onmouseover="showEditIcon(this)" onmouseout="hideEditIcon(this)">✎</span>
        </div>
    `;

    // 将用户消息添加到聊天显示区域
    var chatMessages = document.getElementById('chatMessages');
    chatMessages.innerHTML += userMessageHTML;

    // 模拟后端返回的回复消息（实际上需要通过网络请求获取）
    var replyMessage = "This is a reply from the server.";

    // 获取固定的头像和名字
    var fixedUserAvatar = "/static/images/fixed_avatar.jpg";  // 替换为实际路径
    var fixedUserName = "ChatGPT";  // 替换为实际用户名

    // 构建回复消息的 HTML 结构
    var replyMessageHTML = `
        <div class="message received">
            <div class="user-info">
                <img src="${fixedUserAvatar}">
                <div class="username">${fixedUserName}</div>
            </div>
            <div class="bot-content">${replyMessage}</div>
        </div>
    `;

    // 将回复消息添加到聊天显示区域
    chatMessages.innerHTML += replyMessageHTML;

    // 清空消息输入框
    messageInput.value = '';
    // 滚动到聊天显示区域底部
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 处理按下回车键触发消息发送
function handleKeyDown(event) {
    if (event.key === 'Enter') {
        sendMessage();
        event.preventDefault(); // 阻止回车键换行
    }
}

function showEditIcon(element) {
    var editIcon = element.nextElementSibling || element;
    editIcon.style.display = 'inline';
}

function hideEditIcon(element) {
    var editIcon = element.nextElementSibling || element;
    if (!editIcon.matches(':hover')) {
        editIcon.style.display = 'none';
    }
}


