// document.addEventListener('DOMContentLoaded', function() {
//     // 这里可以添加代码来从服务器获取订单数据
//     // 以下是一个示例数据结构
//     const orders = [
//         { id: '12345', name: '商品A', created: '2023-01-01', expires: '2023-01-10', price: '100元' },
//         // 更多订单数据...
//     ];
//
//     // 填充表格数据
//     const table = document.getElementById('order-table');
//     let tableHTML = '<tr><th>订单ID</th><th>商品名称</th><th>创建时间</th><th>过期时间</th><th>价格</th></tr>';
//     orders.forEach(order => {
//         tableHTML += `<tr><td>${order.id}</td><td>${order.name}</td><td>${order.created}</td><td>${order.expires}</td><td>${order.price}</td></tr>`;
//     });
//     table.innerHTML = tableHTML;
// });
var token;
var header;
// const base_url = "http://localhost:9090"
//const base_url = "https://ttzi.top"
document.addEventListener('DOMContentLoaded', function() {
    token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
    // displayTable(currentPage);

})

// 示例数据和表格填充逻辑与之前相同
function refreshOrder(id,userId) {
    // 发送 GET 请求到后端
    console.log("id", id)
    fetch(`/email/sendToken?id=${id}&userId=${userId}`, {
        method: 'GET',
        headers: {
            [header]: token
        }
    })
        .then(response => response.json())
        .then(data => {
            if(data.code == 200){
                //创建订单成功，展开侧边栏
                alert(data.msg)
            }else {
                alert(data.msg);
            }
        })
        .catch(error => {
            console.error('Error sending refresh request:', error);
        });
}

const currentPageElement = document.getElementById('current-page');
const totalPagesElement = document.getElementById('total-pages');
let currentPage = parseInt(currentPageElement.getAttribute('data-current-page'), 10);
let totalPages = parseInt(totalPagesElement.getAttribute('data-total-pages'), 10);
console.log(typeof(currentPage) , typeof totalPages);
// 点击“上一页”按钮的事件处理函数
function previousPage() {
    if (currentPage > 1) {
        currentPage--;
        showPageInfo();
        loadOrdersForPage(currentPage); // 调用后端接口加载当前页的订单数据
    }
}
// document.getElementById('previous-page').addEventListener('click', function() {
//
// });

function nextPage() {
    console.log("totalPages:", totalPages)
    if (currentPage < totalPages) {
        currentPage++;
        showPageInfo();
        loadOrdersForPage(currentPage); // 调用后端接口加载当前页的订单数据
    }
}
// 点击“下一页”按钮的事件处理函数
// document.getElementById('next-page').addEventListener('click', function() {
//     if (currentPage < totalPages) {
//         currentPage++;
//         showPageInfo();
//         loadOrdersForPage(currentPage); // 调用后端接口加载当前页的订单数据
//     }
// });

// 显示分页信息
function showPageInfo() {
    currentPageElement.textContent = currentPage;
}

// 更改显示条数
function changeItemsPerPage() {
    const itemsPerPage = document.getElementById('items-per-page').value;
    console.log('更改显示条数:', itemsPerPage);
    loadOrdersForPage(1, itemsPerPage); // 重置到第一页并使用新的条目数
}

// 加载分页数据
function loadOrdersForPage(pageNumber, itemsPerPage = 10) {
    console.log('加载页数:', pageNumber, 'with', itemsPerPage, 'items per page');

    // 替换为实际的后端API URL
    const url = `/api/orders?page=${pageNumber}&limit=${itemsPerPage}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('请求返回异常');
            }
            return response.json();
        })
        .then(data => {
            totalPages = data.total;
            totalPagesElement.textContent = totalPages;
            updateOrdersTable(data.data);
        })
        .catch(error => {
            console.error('请求异常:', error);
        });
}

function updateOrdersTable(ordersData) {
    const tableBody = document.getElementById('order-table').getElementsByTagName('tbody')[0];
    tableBody.innerHTML = '';

    ordersData.forEach(order => {
        const row = tableBody.insertRow();
        row.innerHTML = `
            <td>${order.id}</td>
            <td>${order.productName}</td>
            <td>${formatDate(order.createTime)}</td>
            <td>${formatDate(order.expireTime)}</td>
            <td>${order.price}</td>
            <td>${order.orderStatus}</td>
            <td>${order.orderStatus === '已支付' ? '<button class="refresh-button" onclick="refreshOrder(\'' + order.id + '\', \'' + order.userId + '\')">刷新</button>' : ''}</td>
        `;
    });
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleString();
}

function goBack() {
    // 设置浏览器的当前位置为 product.html，从而导航到该页面
    window.location.href = 'product.html';
}


