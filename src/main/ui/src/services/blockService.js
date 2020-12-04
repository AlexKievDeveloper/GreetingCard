import {serverService} from "./serverService";

export const blockService = {
    deleteBlock,
    createBlock,
    getBlock,
    updateBlock,
    deleteLinks
}

function deleteBlock(id) {
    return serverService.sendRequest('/congratulation/' + id, 'DELETE');
}

function getBlock(id) {
    return serverService.getData('/congratulation/' + id);
}

function createBlock(blockData) {
    let formData = new FormData();
    addFiles(formData, blockData);
    const block = { card_id: blockData.card_id,
                message: blockData.message,
                youtube: blockData.youtube};
    formData.append("json", JSON.stringify(block));            
    return serverService.sendFormData('/congratulation', 'POST', formData);
}

function updateBlock(blockData) {
    let formData = new FormData();
    addFiles(formData, blockData);
    const block = {message: blockData.message,
                   youtube: blockData.youtube};
    formData.append("json", JSON.stringify(block));            
    return serverService.sendFormData('/congratulation/' + blockData.block_id, 'PUT', formData);
}

function addFiles(formData, blockData) {
    for (let i = 0; i < blockData.imageFilesToAdd.length; i++) {
        formData.append("files_image", blockData.imageFilesToAdd[i], blockData.imageFilesToAdd[i].filename);
    }
    for (let i = 0; i < blockData.audioFilesToAdd.length; i++) {
        formData.append("files_audio", blockData.audioFilesToAdd[i], blockData.audioFilesToAdd[i].filename);
    }
}

function deleteLinks(id, links) {
    let listOfObj = links.map((id) => {let obj={}; obj['id'] = id; return obj;});
    console.log(listOfObj); 
    return serverService.sendRequest('/congratulation/' + id + '/links', 'DELETE', listOfObj);
}