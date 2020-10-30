"use strict"

const mongoose = require('mongoose');

const ClassModel = mongoose.model('peer', {
    ip: {
        type: String,
        required: false
    },
    name: {
        type: String,
        required: false
    },
    files: []
});

module.exports = ClassModel;