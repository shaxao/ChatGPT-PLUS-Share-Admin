// 在合适的地方定义全局变量，用于数据校验
document.addEventListener('DOMContentLoaded', function() {
    var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
    var header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    let validUsername = '';
    let validPassword =  '';
    let validPhoneOrEmail =  '';
    let validCode =  '';
   // const base_url = "http://localhost:9090/"

// 输入框和验证图标的映射关系
const inputIconMap = {
    username: 'usernameIcon',
    password: 'passwordIcon',
    phoneOrEmail: 'phoneOrEmailIcon',
    verificationCode: 'verificationCodeIcon',
};

// 输入框正则验证规则
const inputRegexMap = {
    username: /^(?:[\u4e00-\u9fa5a-zA-Z0-9_]{1,8})$/,
    password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
    phoneOrEmail: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$|^\d{11}$/,
    verificationCode: /^\d{6}$/,
};

// 输入框和错误信息的映射关系
const inputErrorMap = {
    username: 'usernameError',
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

// 更新注册按钮状态
function updateRegisterButtonState() {
    console.log(validPassword + validUsername + validPhoneOrEmail)
    if (validPassword === '' || validUsername === '' || validPhoneOrEmail === ''){
        document.getElementById('registerBtn').disabled = true;
        return;
    }

    const isValidUsername = inputRegexMap.username.test(validUsername);
    const isValidPassword = inputRegexMap.password.test(validPassword);
    const isValidPhoneOrEmail = inputRegexMap.phoneOrEmail.test(validPhoneOrEmail);

    const isValid = isValidUsername && isValidPassword && isValidPhoneOrEmail;
    console.log('isValid',isValid)
    // 校验所有输入是否满足条件，更新注册按钮状态
    document.getElementById('registerBtn').disabled = !isValid;
}

// 监听用户名输入框失去焦点事件
const usernameElement = document.getElementById('username');
usernameElement.addEventListener('blur', async function() {
    const username = usernameElement.value;
    console.log(username)

    if (inputRegexMap.username.test(username)) {
        const isExist = await checkUsernameExist(username);
        console.log('isExist',isExist)
        if (isExist) {
            showError('username', '用户已存在。');
            //document.getElementById('usernameIcon').className = 'validation-icon invalid';
            validUsername = '';
            console.log(validUsername);
        } else {
            hideError('username');
            //document.getElementById('usernameIcon').className = 'validation-icon valid';
            validUsername = username;
            console.log('validUsername',validUsername);
        }
    }else{
        showError('username',"只包含字母、数字和下划线，且长度在 4 到 16 个字符之间")
        //document.getElementById("usernameIcon").className = 'validation-icon invalid';
        validUsername = '';
        console.log(validUsername);
    }
});

const fetchWithTimeout = (url, options, timeout = 3000) => {
    return Promise.race([
        fetch(url, options),
        new Promise((_, reject) =>
            setTimeout(() => reject(new Error('Timeout')), timeout)
        )
    ]);
};

// 检查用户名是否存在
async function checkUsernameExist(username) {
    //  向后端发起请求，检查用户名是否存在
    try {
        const response = await fetchWithTimeout("/userName?userName=" + encodeURIComponent(username), { method: 'GET' })
        if (!response.ok) {
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

        // 根据需要对data进行进一步处理
        // 例如，如果data包含关于用户名是否存在的信息
        // 你可以在这里返回这个信息或进行相应的处理
    } catch (error) {
        console.error('Error:', error);
    }
    // 此处模拟一个异步请求，实际中需要使用实际的后端接口
    // return new Promise(resolve => {
    //     setTimeout(() => {
    //         // 模拟后端返回的数据，true表示用户名已存在，false表示用户名可用
    //         const isExist = username === 'muhuo';
    //         resolve(isExist);
    //     }, 1000);
    // });
}


// 监听密码输入框输入事件，隐藏密码提示信息
const passwordElement = document.getElementById('password');
const eyeIcon = document.getElementById('togglePassword');
const eye = document.getElementById('eye');
const eyeSlash = document.getElementById('eye-slash');

eyeIcon.addEventListener('click', () => {
    const type = passwordElement.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordElement.setAttribute('type', type);

    // 切换眼睛图标的显示状态
    eye.style.display = type === 'password' ? 'inline' : 'none';
    eyeSlash.style.display = type === 'password' ? 'none' : 'inline';
});
passwordElement.addEventListener('blur', async function() {
    const password = passwordElement.value;

    if (inputRegexMap.password.test(password)) {
            hideError('password');
            //document.getElementById('passwordIcon').className = 'validation-icon valid';
            validPassword = password;
            console.log(validPassword);

    }else {
        showError('password', '密码必须包含至少 8 个字符,包括大写、小写和数字.');
        //document.getElementById('passwordIcon').className = 'validation-icon invalid';
        validPassword = '';
        console.log(validPassword + '错误');
    }
});

// 监听手机号或邮箱输入框失去焦点事件
const phoneOrEmailElement = document.getElementById('phoneOrEmail');
phoneOrEmailElement.addEventListener('blur', async function() {
    const phoneOrEmail = phoneOrEmailElement.value;

    if (inputRegexMap.phoneOrEmail.test(phoneOrEmail)) {
        const isExist = await checkPhoneOrEmailExist(phoneOrEmail);

        if (isExist) {
            showError('phoneOrEmail', '用户已存在.');
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
// 监听验证码输入框的输入事件，启用验证按钮
const verificationCodeElement = document.getElementById('verificationCode');
// verificationCodeElement.addEventListener('input', function() {
//     const isValidVerificationCode = inputRegexMap.verificationCode.test(verificationCodeElement.value);
//
//     // 如果用户重新输入验证码，隐藏错误信息
//     hideError('verificationCode');
// });

// 监听验证码输入框输入事件
verificationCodeElement.addEventListener('input', async function() {
    const verificationCode = verificationCodeElement.value;

    if (inputRegexMap.verificationCode.test(verificationCode)) {
            validCode = verificationCode;
            console.log('validCode',validCode);
            updateRegisterButtonState();
            hideError('verificationCode');
            //document.getElementById('verificationCodeIcon').className = 'validation-icon valid';
    }else {
        validCode = '';
        showError('verificationCode', '验证码格式错误，请重新输入.');
        //document.getElementById('verificationCodeIcon').className = 'validation-icon invalid';
    }
});


// 监听注册按钮点击事件
document.getElementById('registerBtn').addEventListener('click',  function() {
     event.preventDefault();  // 阻止表单的默认提交行为
    // 向后端发起注册请求前再次验证数据
    // 获取当前保存的正确数据
    const formData = {
        username: validUsername,
        password: validPassword,
        phoneOrEmail: validPhoneOrEmail,
        code: validCode
    };

    // 向后端发起注册请求
    // 此处模拟一个异步请求，实际中需要使用实际的后端接口
    //const registerResult = await registerUser(formData);
    fetch(`/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token // 将CSRF token添加到请求头
        },
        body: JSON.stringify(formData)
    })
        .then(response => response.json())
        .then(data => {
            if(data.code === 200){
                alert(data.msg);
                window.location.reload(); // 刷新页面
            }else {
                alert(data.msg);
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });

});

// // 向后端发起注册请求
// async function registerUser(formData) {
//
// }

// 初始化时更新注册按钮状态
updateRegisterButtonState();

});
