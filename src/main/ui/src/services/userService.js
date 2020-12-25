import {serverService} from './serverService.js';

export const userService = {
    login,
    logout,
    getUser,
    setUserId,
    registerUser,
    getProfile,
    updateProfile,
    updatePassword,
    forgotPassword,
    recoverPassword
}

function login(login, password) {

    const headerSecurityName = 'Authorization';

    return serverService.sendRequest('/auth', 'POST', {login, password}, false)
        .then(response => {
            console.log(response);
            if (!response.ok) {
                console.log('response not ok')
                return response.json();
            } else {
                localStorage.setItem('user', login);
                let headers = response.headers;
                if (headers.has(headerSecurityName)) {
                    let token = headers.get(headerSecurityName); 
                    localStorage.setItem('userToken', token);
                }
                return response.json();
            }
        });
}

function setUserId(id) {
    localStorage.setItem('userId', id); 
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
    localStorage.removeItem('userToken');
}

function getUser() {
    let user = localStorage.getItem('user');
    let userId = localStorage.getItem('userId');
    let obj = {};
    obj['user'] = user;
    obj['userId'] = userId;
    return obj;
}


function registerUser(data) {
    return serverService.sendRequest('/user', 'POST', data);
}

function getProfile() {
    return serverService.getData('/user');
}
    
function updateProfile(formData) {
   return serverService.sendFormData('/user', 'PUT', formData);
}
    
function updatePassword(oldPassword, newPassword) {
    console.log('update password ');  
    return serverService.sendRequest('/user/password', 'PUT', {oldPassword:oldPassword, newPassword:newPassword});
}

function forgotPassword(email) {
   return serverService.sendRequest('/user/forgot_password', 'POST', {email:email});
}

function recoverPassword(new_password, hash) {
   return serverService.sendRequest(`/user/recover_password/${hash}`, 'PUT', {password:new_password}); 
}