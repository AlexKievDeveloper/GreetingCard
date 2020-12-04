import {serverService} from "./serverService";

export const cardService = {
    getCards,
    getCard,
    createCard,
    deleteCard,
    leaveCard,
    finishCard,
    deleteUsers,
    addUser,
    getUsers
}

function getCards(type = 'all') {
    return serverService.getData(`/cards?type=${type}`);
}

function getCard(id) {
    return serverService.getData(`/card/${id}`);
}

function createCard(nameCard) {
    return serverService.sendRequest(`/card`, 'POST', {name: nameCard});
}

function deleteCard(id) {
    return serverService.sendRequest(`/card/${id}`, 'DELETE');
}

function leaveCard(id) {
    return serverService.sendRequest(`/card/${id}/user`, 'DELETE');
}

function finishCard(id) {
    serverService.sendRequest(`/card/${id}/status`, 'PUT');
}

function deleteUsers(cardId, listUserId) {
    let listOfObj = listUserId.map((id) => {let obj={}; obj["id"] = id; return obj;});
    console.log(listOfObj);
    return serverService.sendRequest(`/card/${cardId}/users`, 'DELETE', listOfObj);
}

function addUser(cardId, login) {    
    return serverService.sendRequest(`/card/${cardId}/user`, 'POST', {login: login});
}
    
function getUsers(cardId) {
   return serverService.getData(`/card/${cardId}/users`);
}
