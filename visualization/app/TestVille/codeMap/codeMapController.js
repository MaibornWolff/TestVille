"use strict";

import * as THREE from "three";
import {Abstract3D} from "./dataTypes/Abstract3D";
import {CodeMapUtilities} from "./codeMapUtilities";

/**
 * Controls the codeMapDirective
 */
class CodeMapController {

    /* @ngInject */

    /**
     * @external {Raycaster} https://threejs.org/docs/?q=rayca#Reference/Core/Raycaster
     * @constructor
     * @param {Scope} $rootScope
     * @param {ThreeCameraService} threeCameraService
     * @param {ThreeRendererService} threeRendererService
     * @param {ThreeSceneService} threeSceneService
     * @param {ThreeSceneService} threeUpdateCycleService
     *
     */
    constructor($rootScope, threeCameraService, threeRendererService, threeSceneService, threeUpdateCycleService, codeMapService){

        /**
         * hovered mesh
         * @type {object}
         */
        this.hovered = null;

        /**
         * selected mesh
         * @type {object}
         */
        this.selected = null;

        /**
         *
         * @type {ThreeCameraService}
         */
        this.cameraService = threeCameraService;

        /**
         *
         * @type {ThreeRendererService}
         */
        this.renderService = threeRendererService;

        /**
         *
         * @type {ThreeSceneService}
         */
        this.sceneService = threeSceneService;

        /**
         *
         * @type {Scope}
         */
        this.$rootScope = $rootScope;

        /**
         *
         * @type {Raycaster}
         */
        this.raycaster = new THREE.Raycaster();


        /**  **/

        this.mapService = codeMapService;

        this.prevPicked = {
            mergedGeoId:           -1,
            empty:                 true,
            verticesNum:           0,
            verticesOffsetByMerge: -1,
            color:                 null
        };

        this.activePicking           = false;
        this.isFocusOnSearchedActive = false;

        /**  **/

        /**
         * current mouse position
         * @type {{x: number, y: number}}
         */
        this.mouse = {x: 0, y: 0};
        let ctx = this;

        document.addEventListener("mousemove", this.onDocumentMouseMove.bind(this), false);
        document.addEventListener("click",     this.onDocumentMouseDown.bind(this), false);
        //document.addEventListener("onsearch",  this.onDocumentMouseDown.bind(this), false);
        //document.addEventListener("offsearch", this.onDocumentMouseDown.bind(this), false);

        this.$rootScope.$on("onsearch",  (e, data) => {ctx.reactOnOnSearchEvent(data);});
        this.$rootScope.$on("offsearch", ()        => {ctx.reactOnOffSearchEvent();});

        threeUpdateCycleService.updatables.push(this.update.bind(this));

    }

    /**
     * Update method which is bound to the {@link UpdateCycleService}
     */
    update() {
        this.executePickingEvent();
    }

    /**
     * updates {CodeMapController.mouse} on mouse movement
     * @param {MouseEvent} event
     */
    onDocumentMouseMove (event) {
        this.mouse.x = ( event.clientX / this.renderService.renderer.domElement.width ) * 2 - 1;
        this.mouse.y = -( event.clientY / this.renderService.renderer.domElement.height ) * 2 + 1;
    }

    /**
     * updates {CodeMapController} on mouse down
     */
    onDocumentMouseDown() {
        this.activePicking = true;
    }

    reactOnOnSearchEvent(data) {
        this.tryToFocusObjectByName(data.searched);
    }

    reactOnOffSearchEvent(){
        this.deleteOldInformation();
    }


    /**
     * called when a building is selected.
     * @param from previously selected building
     * @param to currently selected building
     * @emits {building-selected} when building is selected
     */
    onBuildingSelected(from, to){
        this.$rootScope.$broadcast("building-selected", {to: to, from:from});
    }



    firstLevelPicking(streets) {

        this.raycaster.setFromCamera(this.mouse, this.cameraService.camera);
        let ray = this.raycaster.ray;
        let point;

        let possibleStreets = [];

        for(let i = 0; i < streets.length; i++) {

            let street = streets[i];
            let depth  = street.streetDepthOver + street.streetDepthUnder;
            let height = street.highestBuilding;
            let width  = street.streetWidth;
            let dontNeedCentralization = this.mapService.utilities.streetWidth/2;
            // to simplify the positioning of a street, their components placed unther have been translated with
            // (0, 0, streetWidth). And the street has been recentralize with (__, __, -streetWidth/2).
            // But The boundingbox don't need this recentralization by z. To deactivate this must translate the bb on z
            // with streetWidth/2.

            let firstTransform = new THREE.Matrix4().makeTranslation(width/2, height/2, dontNeedCentralization + street.streetDepthUnder - (depth/2));
            let box = new Abstract3D(0, -1, width, height, 0xffffff, depth);
            box.transformModelMatrix(firstTransform);
            box.transformModelMatrix(street.matrix);

            let streetBoundingBox = box.getBoundingBox();

            if((point = ray.intersectBox(streetBoundingBox)) !== null) {
                possibleStreets.push(street);
                street.touchedPointX   = point.x;
                street.cameraPositionX = this.cameraService.camera.position.x;
                street.rayPositionX    = ray.direction.x;
            }

        }

        possibleStreets.sort((x, y) => {
            return CodeMapController.factorPointToCamera(x.touchedPointX, x.cameraPositionX, x.rayPositionX) -
            CodeMapController.factorPointToCamera(y.touchedPointX, y.cameraPositionX, y.rayPositionX);
        });

        return possibleStreets;
    }

    static factorPointToCamera(pointX, cameraPositionX, rayDirectionX) {
        return (pointX - cameraPositionX)/(rayDirectionX);
    }



    lastLevelPicking(streets) {

        this.raycaster.setFromCamera(this.mouse, this.cameraService.camera);
        let ray = this.raycaster.ray;
        let leafs = [];
        let theBox;
        let touchedObjects = [];
        let point;

        for(let i = 0; i < streets.length; i++) {

            CodeMapUtilities.extractLeafs(streets[i], leafs);

            for(let j = 0; j < leafs.length; j ++) {
                theBox = leafs[j].myContent.getBoundingBox();

                if((point = ray.intersectBox(theBox)) !== null) {
                    touchedObjects.push(leafs[j]);
                    leafs[j].touchedPointX   = point.x;
                    leafs[j].cameraPositionX = this.cameraService.camera.position.x;
                    leafs[j].rayPositionX    = ray.direction.x;
                }

            }

            if(touchedObjects.length !== 0) {
                break;
            }

            leafs.length = 0;
        }

        touchedObjects.sort((x, y) => {
            return CodeMapController.factorPointToCamera(x.touchedPointX, x.cameraPositionX, x.rayPositionX) -
                CodeMapController.factorPointToCamera(y.touchedPointX, y.cameraPositionX, y.rayPositionX);
        });

        return touchedObjects.length !== 0 ? touchedObjects[0] : null;
    }


    executePickingEvent() {

        if(!this.activePicking){ return; }

        let touchedStreets = this.firstLevelPicking(this.mapService.clickList);
        let touchedObject  = this.lastLevelPicking(touchedStreets);

        this.deleteOldInformation();

        if(touchedObject !== null) {
            this.displayNewInformation(touchedObject.myContent);
        }

        this.activePicking = false;
    }



    deleteOldInformation() {

        if(!this.prevPicked.empty) {

            for(let i = 0; i < this.sceneService.scene.children.length; i++) {

                if((!this.sceneService.scene.children[i].type.endsWith("Light")) &&
                    (this.prevPicked.mergedGeoId === this.sceneService.scene.children[i].geometry.id)) {

                    this.sceneService.scene.children[i].dynamic = true;
                    let colors = this.sceneService.scene.children[i].geometry.attributes.color.array;
                    let k = this.prevPicked.verticesOffsetByMerge;
                    for(let j = 0; j < this.prevPicked.verticesNum; j++) {
                        colors[k]   = this.prevPicked.color.r * 255;
                        colors[k+1] = this.prevPicked.color.g * 255;
                        colors[k+2] = this.prevPicked.color.b * 255;
                        k += 3;
                    }
                    this.sceneService.scene.children[i].geometry.attributes.color.needsUpdate = true;
                }

            }
            this.prevPicked.empty = true;
            this.onBuildingSelected(null, null);
        }

    }



    displayNewInformation(touchedAbstract3d) {

        if(touchedAbstract3d !== null) { // nur wenn es auf ein Object geklickt wurde

            for(let i = 0; i < this.sceneService.scene.children.length; i++) {

                if((!this.sceneService.scene.children[i].type.endsWith("Light")) &&
                    (touchedAbstract3d.mergedGeoId === this.sceneService.scene.children[i].geometry.id)) {

                    this.prevPicked.mergedGeoId           =   touchedAbstract3d.mergedGeoId;
                    this.prevPicked.empty                 =   false;
                    this.prevPicked.verticesNum           =   touchedAbstract3d.verticesNum;
                    this.prevPicked.verticesOffsetByMerge =   touchedAbstract3d.verticesOffsetByMerge;
                    this.prevPicked.color                 =   new THREE.Color(touchedAbstract3d.getColor());

                    let colors = this.sceneService.scene.children[i].geometry.attributes.color.array;
                    let k = touchedAbstract3d.verticesOffsetByMerge;
                    let intersectedColor = new THREE.Color(0x000000);
                    for(let j = 0; j < touchedAbstract3d.verticesNum; j++) {
                        colors[k]   = intersectedColor.r*255;
                        colors[k+1] = intersectedColor.g*255;
                        colors[k+2] = intersectedColor.b*255;
                        k +=3;
                    }
                    this.sceneService.scene.children[i].geometry.attributes.color.needsUpdate = true;
                }

            }
            this.onBuildingSelected(null, touchedAbstract3d.getToDisplayedInfo());
        }

    }

    tryToFocusObjectByName(objectName) {

        let toFocus = this.identifyToFocusObjectByName(objectName);

        this.deleteOldInformation();

        if(toFocus !== null) {
            this.setFocusOnObject(toFocus);
        }
    }

    setFocusOnObject(toFocusObject) {
        this.displayNewInformation(toFocusObject);
    }




    identifyToFocusObjectByName(objectName) {

        for(let i = 0; i < this.mapService.searchList.length; i++) {

            let leafs = CodeMapUtilities.extractLeafsI(this.mapService.clickList[i]);

            for(let j = 0; j < leafs.length; j ++) {
                let nodeInfo = leafs[j].myContent.getToDisplayedInfo();
                if((nodeInfo !== null) && (nodeInfo.getName().toLowerCase().localeCompare(objectName.toLowerCase()) === 0)) {
                    return leafs[j].myContent;
                }
            }

        }
        return null;
    }

}

export {CodeMapController};




