"use strict";

import {Abstract3D}     from "./Abstract3D";

export class Plane extends Abstract3D {
    constructor(id, width, height, color) {
        super(2, id, width, height, color, -1);
    }
}