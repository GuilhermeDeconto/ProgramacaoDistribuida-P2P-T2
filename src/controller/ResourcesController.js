"use strict";

var Hapi = require('@hapi/hapi');
var Q = require('q');

var ResourcesService = require('../service/ResourcesService.js');

exports.getAllResources = async function (request, resp) {
    try {
        const contents = await ResourcesService.getAllResources();

        var response = {
            success: true,
            message: `All resources were retrieved successfully`,
            data: contents
        }
        return resp.response(response);
    } catch (error) {
        return resp.response(error).code(500);
    }
}

exports.deleteAllPeers = async function (request, resp) {
    try {
        const contents = await ResourcesService.deleteAllPeers();

        var response = {
            success: true,
            message: `All peers were deleted successfully`,
            data: contents
        }
        return resp.response(response);
    } catch (error) {
        return resp.response(error).code(500);
    }
}

exports.getPeerById = async function (request, resp) {
    try {
        const contents = await ResourcesService.getPeerById(request.params.id);

        var response = {
            success: true,
            message: `Peer with id ${request.params.id} retrieved successfully`,
            data: contents
        }
        return resp.response(response);
    } catch (error) {
        return resp.response(error).code(500);
    }
}

exports.getPeerByName = async function (request, resp) {
    try {
        const contents = await ResourcesService.getPeerByName(request.params.id);
        
        var response = {
            success: true,
            message: `Peer with name ${request.params.id} retrieved successfully`,
            data: contents
        }
        return resp.response(response);
    } catch (error) {
        return resp.response(error).code(500);
    }
}

exports.getPeerByFile = async function (request, resp) {
    try {
        const contents = await ResourcesService.getPeerByFile(request.params.id);

        var response = {
            success: true,
            message: `Peer with file ${request.payload} retrieved successfully`,
            data: contents
        }
        return resp.response(response);
    } catch (error) {
        return resp.response(error).code(500);
    }
}

exports.insertPeer = async function (request, resp) {
    try {
        let response = await ResourcesService.insertPeer(request.payload);
        let data;
        if(!response) {
            data = {
                success: false,
                message: "Could not create peer."
            }
            return resp.response(data).code(400);
        }
        if(response){
            data = {
                success: true,
                message: "Peer created successfully!",
                data: response
            }
            return resp.response(data).code(201);
        }
    } catch (error) {
        return resp.response(error).code(500);
    }
}