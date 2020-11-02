"use strict"
const HealthController = require('../controller/HealthController.js');
const ResourcesController = require('../controller/ResourcesController.js');
const HTTPMethod = require('../config/HTTPMethod.js');
const Validator = require('../validator/ResourceValidator.js');
const FailureHandlerController = require('../controller/FailureHandlerController.js');

module.exports = [
    {
        method: HTTPMethod.GET,
        path: "/",
        options: {
            description: "Index endpoint to test if application is up and running",
            notes: "Returns a hello world",
            tags: ["api", "Health"],
            handler: HealthController.getHealthController
        }
    },
    {
        method: HTTPMethod.GET,
        path: "/resources",
        options: {
            description: "Retuns all resources from connected peers",
            notes: "Returns all available resources",
            tags: ["api", "Resources"],
            handler: ResourcesController.getAllResources
        }
    },
    {
        method: HTTPMethod.POST,
        path: "/resources/peer",
        options: {

            description: "Register a new peer",
            notes: "Register a new peer",
            tags: ["api", "Peer"],
            validate: {
                payload: Validator.insertPeer,
                failAction: FailureHandlerController.failureHandler
            },
            handler: ResourcesController.insertPeer
        }
    },
    {
        method: HTTPMethod.DELETE,
        path: "/resources/peer",
        options: {
            description: "Delete all peers",
            notes: "Delete all peers",
            tags: ["api", "Peer"],
            handler: ResourcesController.deleteAllPeers
        }
    },
    {
        method: HTTPMethod.GET,
        path: "/resources/peer/{id}",
        options: {
            description: "Retuns peer with a given id",
            notes: "Retuns peer with a given id",
            tags: ["api", "Peer"],
            validate: {
                params: Validator.idParameterValidator,
                failAction: FailureHandlerController.failureHandler
            },
            handler: ResourcesController.getPeerById
        }
    },
    {
        method: HTTPMethod.GET,
        path: "/resources/peer/name/{id}",
        options: {
            description: "Retuns peer with a given name",
            notes: "Retuns peer with a given name",
            tags: ["api", "Peer"],
            validate: {
                params: Validator.idParameterValidator,
                failAction: FailureHandlerController.failureHandler
            },
            handler: ResourcesController.getPeerByName
        }
    },
    {
        method: HTTPMethod.GET,
        path: "/resources/peer/file/{id}",
        options: {
            description: "Retuns peer with a given file",
            notes: "Retuns peer with a given file",
            tags: ["api", "Peer"],
            validate: {
                params: Validator.idParameterValidator,
                failAction: FailureHandlerController.failureHandler
            },
            handler: ResourcesController.getPeerByFile
        }
    },
    {
        method: HTTPMethod.GET,
        path: "/resources/peer/heartbeat/{id}",
        options: {
            description: "Retuns peer with a given id",
            notes: "Retuns peer with a given id",
            tags: ["api", "Peer"],
            validate: {
                params: Validator.idParameterValidator,
                failAction: FailureHandlerController.failureHandler
            },
            handler: HealthController.getHealthController
        }
    },
];
