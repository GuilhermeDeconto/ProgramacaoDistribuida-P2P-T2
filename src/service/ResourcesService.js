var ResourceModel = require('../model/ResourceModel');
const { idParameterValidator } = require('../validator/ResourceValidator');

const getAllResources = async () => {
    try {
        result = await ResourceModel.find();
    } catch (error) {
        console.log('error ', error);
    }
    return result
}

const deleteAllPeers = async () => {
    try {
        result = await ResourceModel.deleteMany();
    } catch (error) {
        console.log('error ', error);
    }
    return result
}

const deletePeerById = async (id) => {
    try {
        result = await ResourceModel.deleteOne({name: id});
    } catch (error) {
        console.log('error ', error);
    }
    return result
}


const getPeerById = async (payload) => {
    var result = null;
    try {
        result = await ResourceModel.findById(payload);
    } catch (error) {
        console.log("error", error)
    }
    return result;
}

const getPeerByFile = async (payload) => {
    var result = null;
    var finalResult = null;
    console.log(`Searching peer with file hash ${payload}`)
    try {
        result = await ResourceModel.find();
        console.log(result.length)
        if (result.length >= 1){
            for (var i = 0; i < result.length; i++){
                for (var j = 0; j < result[i].files.length; j++){
                    if (result[i].files[j].hash == payload){
                        finalResult = result[i];
                        console.log(`Peer with file hash ${payload} found`)
                    }
                }
            }
        }
    } catch (error) {
        console.log("error", error)
    }
    console.log(finalResult)
    return finalResult;
}

const insertPeer = async (payload) => {
    var result = null;
    var resourceModel = new ResourceModel(payload);
    try {
        result = await resourceModel.save();
    } catch (error) {
        console.log("error", error)
    }
    return result;
}

exports.getPeerByFile = getPeerByFile;
exports.deletePeerById = deletePeerById;
exports.getPeerById = getPeerById;
exports.deleteAllPeers = deleteAllPeers;
exports.insertPeer = insertPeer;
exports.getAllResources = getAllResources;
