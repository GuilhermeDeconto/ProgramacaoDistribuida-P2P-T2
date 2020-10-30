'use strict'
const joi = require('joi');

exports.idParameterValidator = joi.object({
    id: joi.string().required()
})

exports.insertPeer = joi.object({
    ip: joi.string().required(),
    name: joi.string().required(),
    files: joi.array().items(joi.object({
        hash: joi.string().optional(),
        name: joi.string().optional()
    })),
});