// detail-script.js
//const base_url = "http://localhost:9090"
//const base_url = "https://ttzi.top"
document.addEventListener('DOMContentLoaded', function() {
  var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
  var header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

  const quantityInput = document.getElementById('quantity');

  const decreaseButton = document.getElementById('decrease');
  const increaseButton = document.getElementById('increase');
    // 获取class为'detail-stock'的元素的data-stock属性值
    const priceElement = document.querySelector('.detail-price');
    const price = parseFloat(priceElement.getAttribute('data-price'));

    const titleElement = document.querySelector('.detail-title');
    const title = titleElement.getAttribute('data-title');

    var stockElement = document.querySelector('.detail-stock');
    var maxStock = parseInt(stockElement.getAttribute('data-stock'), 10);

    var userId = document.getElementById('userId');
    var productId = userId.getAttribute("data-id");

    //console.log('库存值：', maxStock);
// 假设div有一个id为'userDiv'
    var userDiv = document.getElementById('userDiv');
    var username = userDiv.getAttribute('data-name');
   // console.log(username); // 这将输出data-name属性的值

  function updateQuantityButtons() {
      decreaseButton.disabled = quantityInput.value <= 1;
      increaseButton.disabled = quantityInput.value >= maxStock;
  }

  // 减少数量
  decreaseButton.addEventListener('click', function() {
      let quantity = parseInt(quantityInput.value, 10);
      if (quantity > 1) {
          quantityInput.value = quantity - 1;
      }
      updateQuantityButtons();
  });

  // 增加数量
  increaseButton.addEventListener('click', function() {
      let quantity = parseInt(quantityInput.value, 10);
      if (quantity < maxStock) {
          quantityInput.value = quantity + 1;
      }
      updateQuantityButtons();
  });

  // 初始化按钮状态
  updateQuantityButtons();
// 显示订单详情弹出窗口的新函数
  function showOrderDetails(createTime, price, productName, tradeNo, orderStatus) {
      let date = new Date(createTime);
// 获取年份的后两位
      let year = date.getFullYear().toString().substr(-2);
// 获取月份，月份从0开始计数，所以加1，并确保始终是两位数
      let month = ('0' + (date.getMonth() + 1)).slice(-2);
// 获取日，确保始终是两位数
      let day = ('0' + date.getDate()).slice(-2);
// 获取小时，确保始终是两位数
      let hours = ('0' + date.getHours()).slice(-2);
// 获取分钟，确保始终是两位数
      let minutes = ('0' + date.getMinutes()).slice(-2);
// 获取秒，确保始终是两位数
      let seconds = ('0' + date.getSeconds()).slice(-2);
// 组合成所需格式
      let formattedDate = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      document.getElementById('order-sidebar').classList.add('order-sidebar-show');
      //document.getElementById('order-details-popup').classList.remove('hidden');
      document.getElementById('orderStatus').textContent = orderStatus/* 订单状态 */;
      document.getElementById('productName').textContent = productName/* 产品名称 */;
      document.getElementById('price').textContent = price/* 价格 */;
      document.getElementById('creationTime').textContent = formattedDate/* 创建时间 */;
      document.getElementById('orderNum').textContent = tradeNo/* 订单编号 */;
  }
    // 隐藏侧边栏的函数
  // function hideSidebar() {
  //     document.getElementById('order-sidebar').classList.remove('order-sidebar-show');
  // }
  // document.getElementById('close-sidebar-btn').addEventListener('click', hideSidebar);
  // document.getElementById('cancel-order-btn').addEventListener('click', hideSidebar);

    const orderSidebar = document.getElementById('order-sidebar');
    const paymentMethodsCard = document.getElementById('payment-methods-card');
    const confirmPurchaseBtn = document.getElementById('confirm-purchase-btn');
    const choosePaymentBtn = document.getElementById('choose-payment-btn');
    // 获取取消订单按钮
    const cancelOrderBtn = document.getElementById('cancel-order-btn');
    // 绑定点击事件到取消订单按钮
    cancelOrderBtn.addEventListener('click', function() {
        // 添加类名来隐藏侧边栏
        orderSidebar.classList.remove('order-sidebar-show');
    });
    // 功能函数，用于展示支付方式卡片
    function showPaymentMethods() {
        paymentMethodsCard.classList.remove('payment-methods-hidden');
        orderSidebar.classList.add('show-payment-methods');
    }

    // 功能函数，用于隐藏支付方式卡片
    function hidePaymentMethods() {
        orderSidebar.classList.remove('show-payment-methods');
        // 使用setTimeout确保CSS过渡完成后再隐藏元素
        setTimeout(() => {
            paymentMethodsCard.classList.add('payment-methods-hidden');
            orderSidebar.classList.remove('order-sidebar-show');
        }, 400); // 500ms是CSS过渡时间
    }

    async function postFetch(url, data) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(data)
            });
            return response;
        }catch (Eerror) {
            console.error('Error:', error);
            alert("请求失败，请重试！")
        }
    }

    async function getFetch(url) {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                [header]: token
            }
        });
        const data = await response.json();
        return data;
    }

    // TODO 绑定选择支付方式按钮事件   由于支付限制，返回用户微信群二维码，微信内完成订单支付
    choosePaymentBtn.addEventListener('click', function() {
        var orderNo = document.getElementById('orderNum').innerText;
        //console.log("加群需要订单号", orderNo);
        //window.location.href = '/addWechat.html?orderNo=' + orderNo;
        showPaymentMethods();
    });

    // 当点击订单详情头部时，隐藏支付方式
    document.querySelector('.order-sidebar-header').addEventListener('click', function(event) {
        if (!event.target.matches('#choose-payment-btn')) {
            hidePaymentMethods();
        }
    });

    // 绑定支付方式选项事件监听器
    document.querySelectorAll('input[name="payment-method"]').forEach(function(input) {
        input.addEventListener('change', function() {
            confirmPurchaseBtn.disabled = false; // 启用确认购买按钮
        });
    });

    // 绑定确认购买按钮事件，处理选择的支付方式
    confirmPurchaseBtn.addEventListener('click', async function() {
        const selectedMethod = document.querySelector('input[name="payment-method"]:checked').value;
        const orderNo =  document.getElementById('orderNum').textContent;
        //console.log('Selected payment method:', selectedMethod);
        confirmPurchaseBtn.disabled = true;
        hidePaymentMethods(); // 支付方式选择后隐藏支付方式卡片
        resetBuyButton();
        // 处理支付逻辑
        // 支付逻辑处理完毕后，刷新页面
        try {
            const data = await getFetch('/pay/order/pay?orderNo=' + orderNo + '&payType=' + selectedMethod);
            console.log(data)
            document.querySelectorAll('input[name="payment-method"]').forEach(button => {
                button.checked = false;
            });
            if(data.error) {
                $('#infoModalLabel').text('错误')
                $('#infoModalBody').text('服务器出差啦！请稍后再试！！！');
                $('#infoModal').modal('show');
            }
            if(data.code === 400) {
                $('#infoModalLabel').text('错误')
                $('#infoModalBody').text(data.msg);
                $('#infoModal').modal('show');
                return;
            }
            if (data.data.code !== 1) {
                $('#infoModalLabel').text('错误')
                $('#infoModalBody').text(data.msg);
                $('#infoModal').modal('show');
                return;
            }
            if (data.data.payUrl) {
                window.location.href = data.data.payUrl;
            } else if (data.data.qrcode) {
                const redirectUrl = `/addWechat.html?payUrl=${encodeURIComponent(data.data.qrcode)}&orderNo=${encodeURIComponent(orderNo)}`;
                window.location.href = redirectUrl;
            }
        }catch (error) {
            $('#infoModalLabel').text('错误')
            $('#infoModalBody').text('服务器出差啦！请稍后再试！！！');
            $('#infoModal').modal('show');
            console.error('Fetch error:', error);
        }

        // if (data.code === 200) {
        //     // alert("即将跳转......")
        //     // window.location.href = '/addWechat.html?payUrl=' + data.data + '&orderNo=' + orderNo;
        // } else {
        //     alert(data.msg);
        // }
        // TODO 支付成功刷新页面
        // if (isPaymentSuccessful()) {
        //     location.reload(); // 刷新页面
        // } else {
        //     // 处理支付失败的情况
        // }
    });
    // 绑定取消订单按钮事件，隐藏支付方式卡片
    document.getElementById('cancel-order-btn').addEventListener('click', async function () {
        const orderNo = document.getElementById('orderNum').textContent;
        // console.log("取消订单号:" + orderNo)
        //发起请求，将订单状态设为已取消，回滚库存
        try {
            const response = await fetch('/pay/order/cancelOrder?orderNo=' + orderNo);
            const data = await response.json();
            $('#infoModalLabel').text('成功')
            $('#infoModalBody').text(data.msg);
            $('#infoModal').modal('show');
        } catch (error) {
            $('#infoModalLabel').text('错误')
            $('#infoModalBody').text('服务器出差啦！请稍后再试！！！');
            $('#infoModal').modal('show');
            console.error('Fetch error:', error);
        }
        hidePaymentMethods();
        resetBuyButton();
    });

    // 绑定关闭按钮事件，隐藏订单详情卡片
    document.getElementById('close-sidebar-btn').addEventListener('click', function() {
        hidePaymentMethods();
        orderSidebar.classList.remove('order-sidebar-show');
        resetBuyButton();
    });

    // 重置购买按钮的函数
    function resetBuyButton() {
        const buyButton = document.querySelector('.buy-button');
        buyButton.disabled = false;
        buyButton.classList.remove('disabled');
    }

    function closePaymentMethods() {
        document.getElementById('payment-methods-card').classList.add('payment-methods-hidden');
        // 如果有.show-payment-methods类，也要移除
        document.getElementById('order-sidebar').classList.remove('show-payment-methods');
    }

// 绑定关闭支付方式按钮的点击事件
    document.getElementById('close-payment-methods').addEventListener('click', closePaymentMethods);

  // 绑定支付按钮事件
  document.querySelector('.buy-button').addEventListener('click', async function () {
      // 禁用按钮
      this.disabled = true;
      // 更改按钮样式以显示不可用状态
      this.classList.add('disabled');
      // TODO 为了便于展示，暂时先放在这，实际需要相应成功   由于限制，关闭支付功能，转到微信自动处理转账 用户点击跳转到添加微信群界面，微信群内完成操作
      //showOrderDetails();
      const quantity = quantityInput.value;
      const paymentData = {
          username: username,
          price: price,
          quantity: quantity,
          productId: productId,
          title: title
      };
      // console.log("paymentData", paymentData);
      // 发送POST请求到服务器,返回订单的信息
      try {
          const response = await postFetch('/pay/order/createOrder', paymentData);
          const data = await response.json();
          if(data.code == 200) {
              showOrderDetails(data.data.createTime, data.data.price, data.data.productName, data.data.tradeNo, data.data.orderStatus);
          }else {
              alert(data.msg);
          }
      }catch (error) {
          console.error('Error:', error);
          alert("请求失败，请重试！")
      }
  });
  });
