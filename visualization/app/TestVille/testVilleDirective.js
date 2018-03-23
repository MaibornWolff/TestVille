"use strict";

import {TestVilleController} from "./testVilleController";

/**
 * This directive is the entry point of the TestVille application
 */
class TestVilleDirective {

    constructor() {
        this.templateUrl = "./testVille.html";
        this.restrict = "E";
        this.scope = {};
        this.controller = TestVilleController;
        this.controllerAs = "ctrl";
        this.bindToController = true;
    }

}

export {TestVilleDirective};

