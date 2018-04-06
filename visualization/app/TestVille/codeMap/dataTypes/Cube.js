"use strict";

import {Abstract3D}     from "./Abstract3D";

export class Cube extends Abstract3D {
    constructor(id, width, height, color, depth) {
        super(0, id, width, height, color, depth);
    }
}