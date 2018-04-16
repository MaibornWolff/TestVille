"use strict";

import * as THREE          from "three";
import {StaticJsonHandler} from "./dataTypes/StaticJsonHandler.js";
import {CodeMapUtilities}  from "./codeMapUtilities";
import {SearchableFactory} from "../core/searchableFactory/searchableFactory";


/**
 * Main service to manage the state of the rendered code map
 */
export class CodeMapService {

    /* @ngInject */

    /**
     * @external {Object3D} https://threejs.org/docs/?q=Object3#Reference/Core/Object3D
     * @external {Mesh} https://threejs.org/docs/?q=mesh#Reference/Objects/Mesh
     * @constructor
     * @param {Scope} $rootScope
     * @param {ThreeSceneService} threeSceneService
     * @param {TreeMapService} treeMapService
     * @param {TreeMapService} settingsService
     */
    constructor($rootScope, threeSceneService, treeMapService, settingsService) {

        /**
         *
         * @type {Scene}
         */
        this.scene = threeSceneService.scene;
        /**
         *
         * @type {TreeMapService}
         */
        this.treemapService = treeMapService;
        /**
         *
         * @type {TreeMapService}
         */
        this.settingsService = settingsService;

        /**
         * the root of the scene graph
         * @type {Object3D}
         */
        this.root = {};

        /**
         * the root of the buildings
         * @type {Object3D}
         */
        this.buildings = {};

        /**
         * the root of the labels
         * @type {Object3D}
         */
        this.labels = {};

        /**
         * the root of the floors
         * @type {Object3D}
         */
        this.floors = {};

        /**
         * the root of floors and buildings
         * @type {Object3D}
         */
        this.labels = {};

        this.utilities = new CodeMapUtilities();

        /** my own code */
        this.metricSet = [];
        this.choosedMetrics = [];
        this.maxMetricValues  = [];

        this.colorRange = null;
        this.clickList = [];
        this.sceneRoot = null;
        this.searchListe = [];
        this.lastFocusedObject = null;
        /** **/

        let ctx = this;

        $rootScope.$on("settings-changed", (e, s)=> {
            ctx.applySettings(s);
        });

        this.addDefaultLights();

    }

    /**
     * Applies the given settings and redraws the scene
     * @param {Settings} s
     */
    applySettings(s) {
        this.colorRange = s.neutralColorRange;
        let leafs  = [];

        StaticJsonHandler.getLeafs(s.map, leafs);

        this.metricSet = StaticJsonHandler.getMetricsList(leafs[0]);

        this.choosedMetrics = [s.areaMetric, s.heightMetric, s.colorMetric];

        this.maxMetricValues = StaticJsonHandler.getAttribMaxValue(leafs, this.metricSet);

        this.clearScene();
        this.sceneRoot = this.drawRoot(s.map);
        this.clickList = CodeMapUtilities.mergeAndAddObjectsToScene(this.scene, this.sceneRoot);
        this.searchList = SearchableFactory.extractSearchableElementsFrom(s.map);
    }

    /**
     * Add scene lighting
     */
    addDefaultLights() {

        const ambilight = new THREE.AmbientLight(0x707070); // soft white light
        const light1 = new THREE.DirectionalLight(0xe0e0e0, 1);
        light1.position.set(50, 10, 8).normalize();
        light1.castShadow = false;
        light1.shadow.camera.right = 5;
        light1.shadow.camera.left = -5;
        light1.shadow.camera.top = 5;
        light1.shadow.camera.bottom = -5;
        light1.shadow.camera.near = 2;
        light1.shadow.camera.far = 100;
        const light2 = new THREE.DirectionalLight(0xe0e0e0, 1);
        light2.position.set(-50, 10, -8).normalize();
        light2.castShadow = false;
        light2.shadow.camera.right = 5;
        light2.shadow.camera.left = -5;
        light2.shadow.camera.top = 5;
        light2.shadow.camera.bottom = -5;
        light2.shadow.camera.near = 2;
        light2.shadow.camera.far = 100;
        this.scene.add(ambilight);
        this.scene.add(light1);
        this.scene.add(light2);

    }

    /**
     * clear the current scene. Lights remain in the scene
     */
    clearScene() {

        while(this.scene.children.length > 0) {
            this.scene.remove(this.scene.children[0]);
        }
        this.addDefaultLights();
    }

    getMetricFactors() {

        let index;
        let result = [];

        for(let i = 0; i < this.choosedMetrics.length; i++) {
            index =  StaticJsonHandler.getMaxMetricIndex(this.metricSet, this.choosedMetrics[i]);
            result.push(StaticJsonHandler.inverseOrOne(this.maxMetricValues[index]));
        }

        return result;
    }

    /**
     * return an correspondent color to severity.
     * @param severity: 1-Critical | 2-High | 3-Medium | 4-Low.
     * @returns {*}: color as String.
     */
    mapCategorySeverityToColor(severity) {
        let severityHead = severity.toLowerCase().substring(0, 3);
        if(severityHead === null) {
            console.log(severity);
        }
        switch (severityHead) {
            case "1::" : return this.utilities.severityColors[0];
            case "2::" : return this.utilities.severityColors[2];
            case "3::" : return this.utilities.severityColors[5];
            case "4::" : return this.utilities.severityColors[3];
            case "5::" : return this.utilities.severityColors[4];
            default    : return this.utilities.severityColors[6];
        }
    }

    /**
     * return a correspondent color dependent to value and colorRange.
     * @param value: value of the attribute.
     * @param colorRange: dynamic color.
     * @returns {*}: color as String.
     */
    mapAttributeToColor(value, colorRange) {

        if(value < colorRange.from) {
            return this.utilities.severityColors[5];
        }else if (value < colorRange.to) {
            return this.utilities.severityColors[2];
        }
        return this.utilities.severityColors[0];

    }

    createTestCityMap (node) {

       if(!CodeMapUtilities.depthCheck(node, 4)) { // the node must have a depth equals 3.
           return; //throw "can't represent node as TestCityMap";
       }
       let mapGroup     = new THREE.Object3D();
       let categories   = node.children;
       let mapW = this.putMainStreetsInTestCityMap(mapGroup, categories);

       let endStreetLength = mapW.mapWidthOver < mapW.mapWidthUnder ? mapW.mapWidthUnder : mapW.mapWidthOver;
       endStreetLength += this.utilities.distanceBetweenMainStreets;
       let endStreet = CodeMapUtilities.createEmptyStreet(endStreetLength, this.utilities.distanceBetweenMainStreets, this.utilities.distanceBetweenMainStreets);
       endStreet.myContent.texture = "str";
       mapGroup.add(endStreet);
       mapGroup.matrix = new THREE.Matrix4().makeTranslation(0, 0, -this.utilities.distanceBetweenMainStreets/2);
       return mapGroup;
    }

    putMainStreetsInTestCityMap(testCityMap, asMainStreetRepresentableJsonNodes) {

        let mapWidthOver  = 0;
        let mapWidthUnder = 0;

        for(let i = 0; i < asMainStreetRepresentableJsonNodes.length; i++) {
            let category = this.createMainStreet(asMainStreetRepresentableJsonNodes[i]);

            if(mapWidthOver < mapWidthUnder) {
                category.matrix.premultiply(CodeMapUtilities.createMatrixToPlaceMainStreetOver(mapWidthOver));
                mapWidthOver += category.mainStreetsWidth + this.utilities.distanceBetweenMainStreets;
            } else {
                category.matrix.premultiply(CodeMapUtilities.
                    createMatrixToPlaceMainStreetUnder(category.mainStreetsWidth + mapWidthUnder, this.utilities.distanceBetweenMainStreets)
                );
                mapWidthUnder += category.mainStreetsWidth + this.utilities.distanceBetweenMainStreets;
            }
            testCityMap.add(category);
        }

        return {mapWidthOver: mapWidthOver, mapWidthUnder:mapWidthUnder};
    }

    createMainStreet(node) {

        if(!CodeMapUtilities.depthCheck(node, 3)) { // the node must have a depth equals 3.
            throw "can't represent node as MainStreet";
        }
        let mainStreetsGroup = new THREE.Object3D();
        let mainStreetsWidth = 0;

        let i = 0;

        let color = this.mapCategorySeverityToColor(/*StaticJsonHandler.getName(node)*/StaticJsonHandler.getPriority(node));

        while (i < node.children.length) {
            let portionResult     = this.createPortionOfMainStreet(node, i, color);
            let mainStreetPortion = portionResult.createdPortion;
            mainStreetPortion.matrix.premultiply(new THREE.Matrix4().makeTranslation(mainStreetsWidth, 0, 0));
            mainStreetsWidth += mainStreetPortion.mainStreetPortionWidth + this.utilities.mainStreetWidth;

            mainStreetsGroup.add(mainStreetPortion);
            i = portionResult.nextOffset;
        }
        mainStreetsGroup.matrix           = new THREE.Matrix4();
        mainStreetsGroup.mainStreetsWidth = mainStreetsWidth;

        return mainStreetsGroup;
    }

    createPortionOfMainStreet(node, offset, color) {
        let streetsAsJsonNodes = node.children;
        if(streetsAsJsonNodes <= offset) {
            throw "FatalError:: offset >= length!!";
        }

        let next = true;
        let mainStreetGroup = new THREE.Object3D();
        mainStreetGroup.mainStreetProperties = {mainStreetRightDepth: 0,
            mainStreetLeftDepth:  0,
            mainStreetRightWidth: 0,
            mainStreetLeftWidth:  0
        };

        let i;
        for(i = offset; (i < streetsAsJsonNodes.length ) && next; i++) {
            this.createAndPutStreet0nMainStreetPortion(mainStreetGroup, streetsAsJsonNodes[i]);
            next = (mainStreetGroup.mainStreetProperties.mainStreetRightDepth <= this.utilities.mainStreetMaxLength) ||
                   (mainStreetGroup.mainStreetProperties.mainStreetLeftDepth  <= this.utilities.mainStreetMaxLength);
        }

        let mainStreetPortionDepth = CodeMapService.computeMainStreetPortionDepth(mainStreetGroup, this.utilities.distanceBetweenStreets);
        let mainStreetPortion = CodeMapUtilities.createEmptyMainStreet(this.utilities.mainStreetWidth,
            mainStreetPortionDepth, this.utilities.distanceBetweenStreets, color)
        ;
        mainStreetGroup.add(mainStreetPortion);
        mainStreetGroup.mainStreetPortionWidth = mainStreetGroup.mainStreetProperties.mainStreetLeftWidth + this.utilities.mainStreetWidth + mainStreetGroup.mainStreetProperties.mainStreetRightWidth;
        mainStreetGroup.matrix = new THREE.Matrix4().makeTranslation(
            mainStreetGroup.mainStreetProperties.mainStreetLeftWidth + this.utilities.mainStreetWidth,
            0,
            -this.utilities.distanceBetweenStreets)
        ;
        return {createdPortion: mainStreetGroup, nextOffset: i};
    }

    static computeMainStreetPortionDepth(mainStreetPortionNode, additionalSpace) {
        if(mainStreetPortionNode.mainStreetProperties.mainStreetRightDepth < mainStreetPortionNode.mainStreetProperties.mainStreetLeftDepth) {
            return additionalSpace + mainStreetPortionNode.mainStreetProperties.mainStreetLeftDepth;
        }
        return additionalSpace + mainStreetPortionNode.mainStreetProperties.mainStreetRightDepth;
    }

    createAndPutStreet0nMainStreetPortion(mainStreetPortionNode, jsNode) {
        let street = this.createStreet(jsNode);
        this.placeStreetOnMainStreetPortion(mainStreetPortionNode, street);
        mainStreetPortionNode.add(street);
    }

    placeStreetOnMainStreetPortion(mainStreetPortionNode, street) {
        if(mainStreetPortionNode.mainStreetProperties.mainStreetRightDepth < mainStreetPortionNode.mainStreetProperties.mainStreetLeftDepth) {
            this.placeStreetOnMainStreetPortionRightSide(mainStreetPortionNode, street);
        } else {
            this.placeStreetOnMainStreetPortionLeftSide(mainStreetPortionNode, street);
        }
    }

    placeStreetOnMainStreetPortionLeftSide(mainStreetPortionNode, street) {
        let l = mainStreetPortionNode.mainStreetProperties.mainStreetLeftDepth + street.streetDepthOver;
        street.matrix.premultiply(CodeMapUtilities.createMatrixToPlaceStreetLeft(l, this.utilities.mainStreetWidth));
        mainStreetPortionNode.mainStreetProperties.mainStreetLeftDepth += street.streetDepthUnder +
            street.streetDepthOver +
            this.utilities.distanceBetweenStreets
        ;
        if (mainStreetPortionNode.mainStreetProperties.mainStreetLeftWidth < street.streetWidth) {
            mainStreetPortionNode.mainStreetProperties.mainStreetLeftWidth = street.streetWidth;
        }
    }

    placeStreetOnMainStreetPortionRightSide(mainStreetPortionNode, street) {
        let l = mainStreetPortionNode.mainStreetProperties.mainStreetRightDepth + street.streetDepthUnder;
        street.matrix.premultiply(CodeMapUtilities.createMatrixToPlaceStreetRight(l));
        mainStreetPortionNode.mainStreetProperties.mainStreetRightDepth += street.streetDepthUnder +
            street.streetDepthOver +
            this.utilities.distanceBetweenStreets
        ;
        if (mainStreetPortionNode.mainStreetProperties.mainStreetRightWidth < street.streetWidth) {
            mainStreetPortionNode.mainStreetProperties.mainStreetRightWidth = street.streetWidth;
        }
    }

    createStreet(node) {
        if(!CodeMapUtilities.depthCheck(node, 2)) { throw "can't represent node as street"; }

        let streetGroup = new THREE.Object3D();
        streetGroup.streetProperties = {
            streetLengthOver:  0,
            streetLengthUnder: 0,
            streetDepthOver:   0,
            streetDepthUnder:  0,
            streetHeight:      0
        };

        let blocksAsJsonNodes = node.children;
        for(let i = 0; i < blocksAsJsonNodes.length; i++) {
            this.createAndPutBlockInStreet(streetGroup, blocksAsJsonNodes[i], i%2);
        }

        let streetWidth = CodeMapService.computeStreetWidth(streetGroup.streetProperties.streetLengthUnder,
            streetGroup.streetProperties.streetLengthOver, this.utilities.distanceBetweenBlocks
        );

        let street = CodeMapUtilities.createEmptyStreet(streetWidth, this.utilities.streetWidth, this.utilities.distanceBetweenBlocks);
        street.myContent.initInformationToDisplay("requirement", StaticJsonHandler.getName(node), null, null, null);
        streetGroup.add(street);
        streetGroup.matrix = new THREE.Matrix4().makeTranslation(this.utilities.distanceBetweenBlocks, 0, -this.utilities.streetWidth/2);
        streetGroup.streetWidth      = streetWidth;
        streetGroup.streetDepthOver  = streetGroup.streetProperties.streetDepthOver  + (this.utilities.streetWidth/2);
        streetGroup.streetDepthUnder = streetGroup.streetProperties.streetDepthUnder + (this.utilities.streetWidth/2);
        streetGroup.highestBuilding  = streetGroup.streetProperties.streetHeight;
        return streetGroup;
    }

    static computeStreetWidth(lengthOver, lengthUnder, additionLength) {
        return lengthOver < lengthUnder ? lengthUnder + additionLength : lengthOver + additionLength;
    }

    createAndPutBlockInStreet(streetNode, jsNode, placeBlockOverOrUnderStreet) {
        let color = this.mapCategorySeverityToColor(StaticJsonHandler.getPriority(jsNode));
        let block = this.createBlock(jsNode.children, color);

        CodeMapService.placeBlockOnStreet(streetNode, block, placeBlockOverOrUnderStreet, this.utilities.distanceBetweenBlocks, this.utilities.streetWidth);

        if( streetNode.streetProperties.streetHeight < block.blockProperties.blockHeight) {
            streetNode.streetProperties.streetHeight = block.blockProperties.blockHeight;
        }
        streetNode.add(block);
    }

    static placeBlockOnStreet(streetNode, block, PlaceBlockOverOrUnderStreet, spaceBetweenBlocks, streetWidth) {
        if (PlaceBlockOverOrUnderStreet === 0) {
            CodeMapService.placeBlockOverStreet (streetNode, block, spaceBetweenBlocks);
        } else {
            CodeMapService.placeBlockUnderStreet(streetNode, block, spaceBetweenBlocks, streetWidth);
        }
    }

    static placeBlockOverStreet(streetNode, block, spaceBetweenBlocks, streetWidth) {
        block.matrix = CodeMapUtilities.createMatrixToPlaceBlockOverStreet(streetNode.streetProperties.streetLengthOver, streetWidth);
        streetNode.streetProperties.streetLengthOver += block.blockProperties.blockWidth + spaceBetweenBlocks;

        if( streetNode.streetProperties.streetDepthOver < block.blockProperties.blockDepth) {
            streetNode.streetProperties.streetDepthOver = block.blockProperties.blockDepth;
        }
    }

    static placeBlockUnderStreet(streetNode, block, spaceBetweenBlock, streetWidth) {
        block.matrix = CodeMapUtilities.createMatrixToPlaceBlockUnderStreet(block,
            streetNode.streetProperties.streetLengthUnder,
            streetWidth
        );
        streetNode.streetProperties.streetLengthUnder += block.blockProperties.blockWidth + spaceBetweenBlock;

        if( streetNode.streetProperties.streetDepthUnder < block.blockProperties.blockDepth) {
            streetNode.streetProperties.streetDepthUnder = block.blockProperties.blockDepth;
        }
    }

    createBlock(leafs, groundColor) {
        /** We sort the for ästhetics Cause */
        //let th = this;
        /*leafs.sort(function(a, b){
            let a1 = Number(th.jsonHandler.getAttribValue(a, th.choosedMetrics[0]));
            let b1 = Number(th.jsonHandler.getAttribValue(b, th.choosedMetrics[0]));
            return a1 - b1;
        });*/

        let step = Math.floor(Math.sqrt(leafs.length));
        let metricFactors = this.getMetricFactors();
        let blockGroup    = new THREE.Object3D();
        blockGroup.blockProperties = {blockWidth: 0, blockDepth: 0, blockHeight: 0};
        let to;

        for(let i = 0; i < leafs.length; i = i + step) {
            to = (i + step) < leafs.length ? (i + step) : leafs.length;
            this.createAndPutColumnInBlock(blockGroup, leafs, groundColor, metricFactors, i, to);
        }
        blockGroup.matrix = new THREE.Matrix4();
        return blockGroup;
    }

    createAndPutColumnInBlock(blockNode, jsNodes, groundColor, metricFactors, from, to) {

        let column = this.createColumn(jsNodes, groundColor, metricFactors, from , to);
        column.matrix = new THREE.Matrix4().makeTranslation(blockNode.blockProperties.blockWidth, 0, 0);
        blockNode.add(column);
        // BlockProperties actualization
        blockNode.blockProperties.blockWidth     += column.columnProperties.columnWidth;
        if (blockNode.blockProperties.blockDepth  < column.columnProperties.columnDepth) {
            blockNode.blockProperties.blockDepth  = column.columnProperties.columnDepth;
        }
        if (blockNode.blockProperties.blockHeight < column.columnProperties.columnHeight) {
            blockNode.blockProperties.blockHeight = column.columnProperties.columnHeight;
        }
    }

    createColumn (leafs, groundColor, metricFactors, from, to) {
        let columnProperties = {
            columnWidth:  0,
            columnDepth:  this.utilities.distanceBetweenBuildings,
            columnHeight: 0
        };
        let columnGroup = new THREE.Object3D();

        for(let i = from; i < to; i++) {
            columnProperties = this.createAndPutBuildingInColumn(columnGroup, leafs[i], metricFactors, columnProperties);
        }
        columnProperties.columnWidth += this.utilities.distanceBetweenBuildings;

        CodeMapService.createAndPutGroundInColumn(columnGroup, columnProperties.columnWidth, columnProperties.columnDepth, groundColor);
        columnGroup.columnProperties = columnProperties;
        return columnGroup;
    }

    createAndPutBuildingInColumn(columnNode, jsNodeToRepresentAsBuilding, metricFactors, columnProperties) {
        let columnHeight  = columnProperties.columnHeight;
        let columnWidth   = columnProperties.columnWidth;
        let columnDepth   = columnProperties.columnDepth;

        let building    = this.createBuilding(jsNodeToRepresentAsBuilding, metricFactors, columnWidth);
        if(columnHeight < building.myContent.getHeight()) { columnHeight = building.myContent.getHeight();}
        if(columnWidth  < building.myContent.getWidth())  { columnWidth  = building.myContent.getWidth() ;}

        let translationY = building.myContent.getHeight()/2;                                              // translation on y-Axis
        let translationZ = -((building.myContent.getDepth()/2) + columnDepth);                           // translation on z-Axis
        let translationX = (building.myContent.getWidth() + this.utilities.distanceBetweenBuildings)/2; // translation on x-Axis

        let buildingPlacementMatrix = new THREE.Matrix4().makeTranslation(translationX, translationY, translationZ);
        building.myContent.transformModelMatrix(buildingPlacementMatrix);

        columnDepth += building.myContent.getDepth() + this.utilities.distanceBetweenBuildings;
        columnNode.add(building);

        return {columnWidth: columnWidth, columnDepth: columnDepth, columnHeight: columnHeight};
    }

    static createAndPutGroundInColumn(columnNode, groundWidth, groundDepth, groundColor) {
        let ground = CodeMapUtilities.createGroundOfColumn(groundWidth, groundDepth, groundColor);
        let translationZ = -groundDepth/2;
        let translationX =  groundWidth/2;
        ground.transformModelMatrix(new THREE.Matrix4().makeTranslation(translationX, 0, translationZ));
        let groundObj = new THREE.Object3D();
        groundObj.myContent = ground;
        columnNode.add(groundObj);
    }

    /**
     * This function get a JsonNode and return the correspondent Abstract3D-Object.
     *
     * @param leaf          -- JsonNode to represent as building
     * @param metricFactors --
     * @param columnWidth   -- width of the column that the building belong.
     * @returns {*}         -- Abstract3D
     * @Warning             -- the created Object is wrapped in a Three.Object3D-Object.
     */
    createBuilding (leaf, metricFactors, columnWidth) {

        let areaM   = Number(StaticJsonHandler.getAttributeValue(leaf, this.choosedMetrics[0]));
        let heightM = Number(StaticJsonHandler.getAttributeValue(leaf, this.choosedMetrics[1]));
        let colorM  = Number(StaticJsonHandler.getAttributeValue(leaf, this.choosedMetrics[2]));

        let width         = CodeMapService.computeBuildingWidth(columnWidth, metricFactors[0], areaM, this.utilities.minBuildingWidth, this.utilities.maxBuildingWidth);
        let depth         = this.utilities.minBuildingDepth  + (areaM   * metricFactors[0] * this.utilities.maxBuildingArea / width);
        let height        = this.utilities.minBuildingHeight + (heightM * metricFactors[1] * this.utilities.maxBuildingHeight);
        let geometryType  = 0;
        let isTransparent =  Number(StaticJsonHandler.getAttributeValue(leaf, "countExecutions")) === 0;
        let buildingCol   = isTransparent ? "#ffffff" : this.mapAttributeToColor(colorM, this.colorRange);
        let id            = StaticJsonHandler.getId(leaf);

        let building = CodeMapUtilities.createAbstract3D(geometryType, id, width, height, depth, /*diameter*/0, buildingCol, isTransparent);
        building.initInformationToDisplay("test", StaticJsonHandler.getName(leaf), areaM, heightM, colorM);

        let result       = new THREE.Object3D();
        result.myContent = building;
        return result;
        /*let diameter = 2 * (this.utilities.minBuildingDepth + Math.sqrt(((areaM * metricFactors[0] * this.utilities.maxBuildingArea)/3)));
        //let geoType = (Number(StaticJsonHandler.getAttribValue(leaf, "testResult")) === 1) ? 1 : 0;*/
    }

    static computeBuildingWidth(widthOfCurrentColumn, factor, areaValue, minWith, maxWith) {
        if (widthOfCurrentColumn === 0) {
            return minWith + (areaValue * factor * maxWith);
        }
        return widthOfCurrentColumn;
    }

    drawRoot(jsonNode) {
        let depth = StaticJsonHandler.treeDepth(jsonNode);
        let result;

        switch (depth) {
            case 4 : result = this.createTestCityMap(jsonNode); break;
            case 3 : result = this.createMainStreet (jsonNode); break;
            case 2 : result = this.createStreet     (jsonNode); break;
            default:
        }
        return result;
    }
}