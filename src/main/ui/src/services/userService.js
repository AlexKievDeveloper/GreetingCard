import {serverService} from './serverService.js';

export const userService = {
    login,
    logout,
    getUser,
    setUserId,
    setLanguage,
    registerUser,
    getProfile,
    updateProfile,
    updateLanguage,
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

function setLanguage(userLanguage) {
    let language;
    if (userLanguage === 'ENGLISH') {
        language = 'EN';
    } else if (userLanguage === 'UKRAINIAN') {
        language = 'UA'
    } else {
        language = userLanguage;
    }
    localStorage.setItem('userLanguage', language); 
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('userId');
    localStorage.removeItem('userToken');
    localStorage.removeItem('userLanguage');
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

function updateLanguage(newLanguage) {
   let user = getUser();
   if (user.hasOwnProperty('userId') && user.userId > 0) {
      let languageForBackend = newLanguage === 'EN' ? 'ENGLISH' : 'UKRAINIAN';
      return serverService.sendRequest(`/user/language/${languageForBackend}`, 'PUT');
   }
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