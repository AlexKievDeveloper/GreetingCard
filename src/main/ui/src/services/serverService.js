import config from './config.js';

export const serverService = {
    sendRequest,
    getData,
    sendFormData
}

function getHeaderAuthorization() {
    return localStorage.getItem('userToken');
}

function authHeader() {
    let token = getHeaderAuthorization();

    if (token) {
        return {'Authorization' : token};
    } else {
        return {};
    }
}

async function sendRequest(url, methodRequest, data = {}, isHeaderAuthorization = true) {
    
    console.log('sendRequest ' + config.apiUrl + url + ' method: ' + methodRequest);
    
    const headerSecurityName = 'Authorization';
    let header = {'Content-Type': 'application/json'};
    if (isHeaderAuthorization) {
        header[headerSecurityName] = getHeaderAuthorization();
    }
    
    try {
        const response = await fetch(config.apiUrl + url, {
            method: methodRequest,
            credentials: 'include',
            body: JSON.stringify(data),
            headers: header
        });
        if (response.ok)
            console.log('Успех:' + url + ' method: ' + methodRequest);   
        return response;
    } catch (error) {
        console.error('Ошибка:', error);
        throw error;
    }
}


async function getData(url) {
    console.log('getData ' + config.apiUrl + url);
    try {
        const response = await fetch(config.apiUrl + url, {
            method: 'GET',
            credentials: 'include',
            headers: authHeader()
        });
        const jsonData = await response.json();
        console.log('Успех:', JSON.stringify(jsonData));
        return jsonData;
    } catch (error) {
        console.error('Ошибка:', error);
        throw error;
    }
}

async function sendFormData(url, method, formData) {
    console.log('sendFormData  ' + config.apiUrl + url + ' method: ' + method);
    try {
        const response = await fetch(config.apiUrl + url, {
            method: method,
            credentials: 'include',
            body: formData,
            headers: authHeader()
        });
        return response;
    } catch (error) {
        console.error('Ошибка:', error);
        throw error;
    }
}