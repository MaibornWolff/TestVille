"use strict";

import {Abstract3D}     from "./Abstract3D";

export class Cylinder extends Abstract3D {
    constructor(id, width, height, color) {
        super(1, id, width, height, color, width);
    }
}