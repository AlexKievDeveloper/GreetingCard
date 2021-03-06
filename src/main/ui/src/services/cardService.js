import {serverService} from "./serverService";

export const cardService = {
    getCards,
    getCard,
    getFinishedCard,
    createCard,
    deleteCard,
    leaveCard,
    finishCard,
    unFinishCard,
    updateName,
    deleteUsers,
    addUser,
    getUsers,
    changeBackground,
    joinToCard,
    getInvitationLink,
    saveUserOrder,
    saveDate
}

function getCards(type = 'ALL') {
    return serverService.getData(`/cards?type=${type}`);
}

function getCard(id) {
    return serverService.getData(`/card/${id}`);
}

function getFinishedCard(id, hash) {
    return serverService.getData(`/card/${id}/card_link/${hash}`);
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
    return serverService.sendRequest(`/card/${id}/status/ISOVER`, 'PUT');
}

function unFinishCard(id) {
    return serverService.sendRequest(`/card/${id}/status/STARTUP`, 'PUT');
}

function updateName(id, newName) {
    return serverService.sendRequest(`/card/${id}/name`, 'PUT', {name: newName});
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

function changeBackground(cardId, backgroundColorBlocks, backgroundCardLink, backgroundCardFile) {
    let formData = new FormData();
    if (backgroundCardFile != null) {
       formData.append("backgroundCardFile", backgroundCardFile, backgroundCardFile.filename);
    }

    formData.append("backgroundColorCongratulations", backgroundColorBlocks);
    formData.append("backgroundCard", backgroundCardLink);
    serverService.sendFormData(`/card/${cardId}/background`, 'PUT', formData);
}

function joinToCard(cardId, hash) {
    return serverService.sendRequest(`/card/${cardId}/user/hash/${hash}`, 'POST');
}

function getInvitationLink(cardId) {
    return serverService.getData(`/card/${cardId}/generate_card_link/`);
}

function saveUserOrder(id, users) {
    return serverService.sendRequest(`/card/${id}/users/order`, 'PUT', users);
}

function saveDate(id, date) {
    return serverService.sendRequest(`/card/${id}/date`, 'PUT', {dateOfFinish:date});
}