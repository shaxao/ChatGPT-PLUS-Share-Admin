let validPhoneOrEmail =  '';
let validCode =  '';
let validPassword =  '';
var token;
var header;
//const base_url = "http://localhost:9090/"
//const base_url = "https://ttzi.top/"

document.addEventListener('DOMContentLoaded', function() {
    token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

})
// 输入框正则验证规则
const inputRegexMap = {
    password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
    phoneOrEmail: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$|^\d{11}$/,
    verificationCode: /^\d{6}$/,
};

// 输入框和错误信息的映射关系
const inputErrorMap = {
    password: 'passwordError',
    phoneOrEmail: 'phoneOrEmailError',
    verificationCode: 'verificationCodeError',
};

// 显示错误信息
function showError(inputName, errorMessage) {
    const errorElement = document.getElementById(inputErrorMap[inputName]);
    errorElement.textContent = errorMessage;
    errorElement.style.color = '#f00';
}

// 隐藏错误信息
function hideError(inputName) {
    const errorElement = document.getElementById(inputErrorMap[inputName]);
    errorElement.textContent = '';
}

//更新重置密码按钮
function updateReset() {
    if (validPhoneOrEmail === '' && validCode === '' && validPassword === '') {
        document.getElementById('resetBtn').disabled = true;
        return;
    }
    const isValidPassword = inputRegexMap.password.test(validPassword);
    const isValidPhoneOrEmail = inputRegexMap.phoneOrEmail.test(validPhoneOrEmail);

    const isValid = isValidPassword && isValidPhoneOrEmail;
    console.log('isValid',isValid);
    document.getElementById('resetBtn').disabled = !isValid;
}

const fetchWithTimeout = (url, options, timeout = 3000) => {
    return Promise.race([
        fetch(url, options),
        new Promise((_, reject) =>
            setTimeout(() => reject(new Error('Timeout')), timeout)
        )
    ]);
};


// 监听手机号或邮箱输入框失去焦点事件
const phoneOrEmailElement = document.getElementById('phoneOrEmail');
phoneOrEmailElement.addEventListener('blur', async function() {
    const phoneOrEmail = phoneOrEmailElement.value;

    if (inputRegexMap.phoneOrEmail.test(phoneOrEmail)) {
        const isExist = await checkPhoneOrEmailExist(phoneOrEmail);

        if (!isExist) {
            showError('phoneOrEmail', '用户不存在.');
            //document.getElementById('phoneOrEmailIcon').className = 'validation-icon invalid';
            validPhoneOrEmail = '';
            console.log(validPhoneOrEmail);
        } else {
            hideError('phoneOrEmail');
            //document.getElementById('phoneOrEmailIcon').className = 'validation-icon valid';
            validPhoneOrEmail = phoneOrEmail;
            console.log(validPhoneOrEmail);
        }
    }else {
        showError('phoneOrEmail', '请输入正确的手机号或邮箱格式.');
        //document.getElementById('phoneOrEmailIcon').className = 'validation-icon invalid';
        validPhoneOrEmail = '';
        console.log(validPhoneOrEmail);
    }
});
// 检查手机号或邮箱是否已注册
async function checkPhoneOrEmailExist(phoneOrEmail) {
    //  向后端发起请求，检查手机号或邮箱是否已注册
    const phoneRegex = /^1\d{10}$/;
    const qqEmailRegex = /^[1-9]\d{4,10}@qq\.com$/;
    let response;
    try {
        if (phoneRegex.test(phoneOrEmail)) {
            response = await fetchWithTimeout("/phone?phone=" + phoneOrEmail,{ method: 'GET'});
        } else if (qqEmailRegex.test(phoneOrEmail)) {
            response = await fetchWithTimeout("/email?email=" + phoneOrEmail, {method: 'GET'});
        }
        if (response && !response.ok) {
            alert('网络异常，请再试一次')
            throw new Error('网络异常，请再试一次');
        }
        //const response = await fetch("/userName?username=" + encodeURIComponent(username));
        const jsonReadear = await response.json();
        //console.log("response:", jsonReadear);  // 打印整个响应体
        //console.log("json:", jsonReadear.code);
        if(jsonReadear.code === 400) {
            const isExist = false;
            return isExist;
        }else{
            const isExist = true;
            return isExist;
        }
    }catch (error) {
        console.error('Error:', error);
    }
    // 此处模拟一个异步请求，实际中需要使用实际的后端接口
    // return new Promise(resolve => {
    //     setTimeout(() => {
    //         // 模拟后端返回的数据，true表示已注册，false表示未注册
    //         const isExist = phoneOrEmail === 'muhuo@example.com' || phoneOrEmail === '12345678901';
    //         resolve(isExist);
    //     }, 1000);
    // });
}

//点击获取验证码
document.getElementById('getVerifyCodeBtn').addEventListener('click', async function() {
    this.style.backgroundColor = '#ccc'; // 灰色背景
    startCountdown(60, this); // 假设倒计时60秒
    const result = await getVerifyCode(validPhoneOrEmail);
});

async function getVerifyCode(toUserEmail){
    try {
        const response = await fetchWithTimeout("/email/code?toUserEmail=" + toUserEmail,{ method: 'GET'});
        if(!response.ok){
            alert('网络异常，请再试一次')
            throw new Error('网络异常，请再试一次');
        }
        const jsonReader = await response.json();
        if(jsonReader.code === 200){
            console.log('code',jsonReader.code);
            alert("验证码发送成功");
        }else{
            alert("亲,验证码发送失败或者发送频繁,请稍后再试");
        }
    }catch (error){
        console.error('Error:', error);
    }

}

function startCountdown(duration, btnElement) {
    var timer = duration;
    var originalText = btnElement.textContent; // 保存原始按钮文本
    btnElement.disabled = true; // 禁用按钮

    var interval = setInterval(function () {
        var seconds = parseInt(timer % 60, 10);
        btnElement.textContent = seconds + "秒后重试";

        if (--timer < 0) {
            clearInterval(interval);
            btnElement.textContent = originalText; // 重置按钮文本
            btnElement.style.backgroundColor = '#4caf50';
            btnElement.disabled = false; // 启用按钮
        }
    }, 1000);
}

let passwordInputAdded = false; // 新增一个标志用于检查密码输入框是否已添加

// 监听验证码输入框输入事件
const verificationCodeElement = document.getElementById('verificationCode');
verificationCodeElement.addEventListener('input', async function() {
    const verificationCode = verificationCodeElement.value;

    if (inputRegexMap.verificationCode.test(verificationCode)) {
        if (!passwordInputAdded) {
            addPasswordInput();
            passwordInputAdded = true; // 标记密码输入框已添加
        }
        validCode = verificationCode;
        console.log('validCode',validCode);
        hideError('verificationCode');
        //document.getElementById('verificationCodeIcon').className = 'validation-icon valid';
    }else {
        validCode = '';
        showError('verificationCode', '验证码格式错误，请重新输入.');
        //document.getElementById('verificationCodeIcon').className = 'validation-icon invalid';
    }
});

// 获取包含动态生成输入框的父元素
var resetForm = document.getElementById('resetForm');

// 添加事件监听器到父元素上
resetForm.addEventListener('input', function(event) {
    // 检查事件是否来自于密码输入框
    if (event.target.tagName === 'INPUT' && event.target.type === 'password') {
        // 在这里处理密码输入框的输入事件
        const password = event.target.value;

        if (inputRegexMap.password.test(password)) {
            hideError('password');
            //document.getElementById('passwordIcon').className = 'validation-icon valid';
            validPassword = password;
            // 启用提交按钮
            updateReset();
            console.log(validPassword);
        } else {
            showError('password', '密码必须包含至少 8 个字符,包括大写、小写和数字.');
            //document.getElementById('passwordIcon').className = 'validation-icon invalid';
            validPassword = '';
            console.log('错误:',validPassword);
        }
        console.log('Password input changed:', event.target.value);
    }
});


//重置密码按钮触发
const resetPasswordButton = document.getElementById("resetBtn");
resetPasswordButton.addEventListener("click", async function(){
    event.preventDefault();  // 阻止表单的默认提交行为
    //  向后端发起注册请求前再次验证数据
    // 获取当前保存的正确数据
    const formData = {
        password: validPassword,
        phoneOrEmail: validPhoneOrEmail,
        code: validCode
    };

    //  向后端发起重置密码请求
    const resetResult = await resetPassword(formData);
});

async function resetPassword(formData){
    try {
        const response = await fetch(`/resetPassword`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(formData),
        });

        const data = await response.json();
        if(data.code === 200){
            //刷新表单
            location.reload();
            alert(data.msg);
            // 重置密码成功，跳转到登录页面
            window.location.href = `/login`;
        }else {
            alert(data.msg);
        }
    } catch (error) {
        // 处理发生的错误
        console.error('Error during registration:', error);
        return 'An error occurred during registration.';
    }
}

function addPasswordInput() {
    var resetForm = document.getElementById('resetForm');

    // 创建包含验证图标和错误信息的外层容器
    var inputContainer = document.createElement('div');
    inputContainer.className = 'input-container';

    // 创建密码输入框
    var passwordInput = document.createElement('input');
    passwordInput.type = 'password';
    passwordInput.id = 'password';
    passwordInput.className = 'input-text';
    passwordInput.name = 'password';
    passwordInput.required = true;

    // 创建验证图标
    var validationIcon = document.createElement('div');
    validationIcon.className = 'validation-icon';
    validationIcon.id = 'passwordIcon';

    // 创建错误信息容器
    var errorContainer = document.createElement('div');
    errorContainer.className = 'error-message';
    errorContainer.id = 'passwordError';

    // 将密码输入框、验证图标和错误信息容器添加到外层容器中
    inputContainer.appendChild(passwordInput);
    inputContainer.appendChild(validationIcon);
    inputContainer.appendChild(errorContainer);

    // 将外层容器插入到表单中
    var submitBtn = document.getElementById('resetBtn');
    resetForm.insertBefore(inputContainer, submitBtn);


}
