"use strict";

import * as THREE from "three";

import "./threeViewer/threeViewer.js";
import "../core/core.js";

import {CodeMapDirective} from "./codeMapDirective.js";
import {CodeMapService} from "./codeMapService.js";
import {CodeMapAssetService} from "./codeMapAssetService.js";
import {CodeMapController} from "./codeMapController";

angular.module("app.testVille.codeMap",["app.testVille.codeMap.threeViewer", "app.testVille.core"]);

angular.module("app.testVille.codeMap").directive(
    "codeMapDirective",
    ["threeViewerService", "codeMapService", (a, b) => new CodeMapDirective(a, b)]
);

angular.module("app.testVille.codeMap").service(
    "codeMapService",
    CodeMapService
);

angular.module("app.testVille.codeMap").service(
    "codeMapAssetService",
    CodeMapAssetService
);

angular.module("app.testVille.codeMap").controller(
    "codeMapController",
    CodeMapController
);

angular.module("app.testVille.codeMap").factory(
    "codeMapMaterialFactory",
    () => {return {
                    positive: () => {return new THREE.MeshLambertMaterial({color: 0x69AE40});},
                    neutral: () => {return new THREE.MeshLambertMaterial({color: 0xddcc00});},
                    negative: () => {return new THREE.MeshLambertMaterial({color: 0x820E0E});},
                    odd: () => {return new THREE.MeshLambertMaterial({color: 0x501A1C});},
                    even: () => {return new THREE.MeshLambertMaterial({color: 0xD1A9A9});},
                    selected: () => {return new THREE.MeshLambertMaterial({color: 0xEB8319});},
                    default: () => {return new THREE.MeshLambertMaterial({color: 0x89ACB4});},
                    positiveDelta: () => {return new THREE.MeshLambertMaterial({color: 0x69ff40});}, //building grew -> positive delta, the change may be negative for specific metrics
                    negativeDelta: () => {return new THREE.MeshLambertMaterial({color: 0xff0E0E});}
            };}

);