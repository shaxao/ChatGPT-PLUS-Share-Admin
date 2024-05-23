// script.js
document.addEventListener('DOMContentLoaded', function() {
    async function requestResult() {
        const url = '/getNotice';
        const response = await fetch(url, {
            method: 'GET', // 修正：使用对象而不是字符串
        });
        const data = await response.json(); // 解析响应为 JSON
        return data;
    }

    const isHTML = (str) => {
        const doc = new DOMParser().parseFromString(str, 'text/html');
        return Array.from(doc.body.childNodes).some(node => node.nodeType === 1);
    }
    /**
     * 公告
     * @param message 消息内容
     * @param duration 延迟时间 /ms
     */
    async function showToast( duration = 5000) {
        const toastId = 'toast' + new Date().getTime();
        const message = await requestResult();
        const toastHtml = `
        <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-delay="${duration}">
            <div class="toast-header">
                <strong class="mr-auto">公告</strong>
                <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="toast-body">
            </div>
        </div>
    `;
        if(message.data) {
            $('#toastContainer').append(toastHtml);
            if (isHTML(message.data)) {
                $(`#${toastId} .toast-body`).html(message.data); // 插入HTML
            } else {
                $(`#${toastId} .toast-body`).text(message.data); // 插入纯文本
            }
            $(`#${toastId}`).toast('show');
        }
        // Optionally, remove the toast from DOM after it hides
        $(`#${toastId}`).on('hidden.bs.toast', function () {
            $(this).remove();
        });
    }

    showToast(5000);

    // 绑定退出登录按钮事件
    document.getElementById('logout').addEventListener('click', function() {
        // 这里添加退出登录的逻辑，可能是清除登录状态、调用后端API等
        console.log('用户已退出');
        // 通常会重定向到登录页面
        window.location.href = 'login.html';
    });
  // 绑定订单查询按钮事件
  document.getElementById('order-query').addEventListener('click', function() {
      // 实现订单查询功能，可能需要跳转到查询订单页面
      const username = this.getAttribute('data-name');
      console.log('Username:', username);
      window.location.href = '/orderQuery?username=' + username;
  });

  // 绑定商品点击事件
  const productItems = document.querySelectorAll('.product-item');
  productItems.forEach(function(item) {
      item.addEventListener('click', function() {
          const productId = this.getAttribute('data-id');
          console.log("商品id",productId);
          window.location.href = '/product-detail.html?prodId=' + productId;
      });
  });
});
