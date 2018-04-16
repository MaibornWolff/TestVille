/**
 * visualization of test data
 * Maibornwolff_ATM_TestLandKarten(TesLa)
 *
 * @Module:     visualization
 * @File :      CodeMapUtilities
 * @LastUpdate: __/12/2017
 * @PO:         Francesco Guiseppe La Torre
 * @Author:     JNiada
 * @Abstract:
 *          This class contains auxiliary functions for the draw of the test-city-map.
 *          Its provide values as the distance between buildings etc... and methods to
 *          builds the scene graph of the map.
 */


"use strict";

import {Abstract3D}        from "./dataTypes/Abstract3D.js";
import * as THREE          from "three";
import {StaticJsonHandler} from "./dataTypes/StaticJsonHandler.js";
import {Cube} from "./dataTypes/Cube";
import {Cylinder} from "./dataTypes/Cylinder";
import {Plane} from "./dataTypes/Plane";
import {CodeMapService} from "./codeMapService";

export class CodeMapUtilities {

    constructor () {

        this.severityColors = [0xff0000, // 1-critical (requirementCategory / testCaseCategory)
            0xffa500, // 2-high (requirementCategory / testCaseCategory)
            0xffff00, // 3-medium (requirementCategory / testCaseCategory)
            0x0000ff, // 4-low (requirementCategory / testCaseCategory)
            0xff00ff, //  Trivial xray
            0x00ff00, // clear, ok (for testCase)
            0x8b4513// braun unknown (for self-defined severity)
        ];

        this.streetWidth = 4;

        this.mainStreetWidth = 10;

        this.distanceBetweenBuildings   = 0.25;
        this.distanceBetweenBlocks      = 1;
        this.distanceBetweenMainStreets = 18; // was 15
        this.distanceBetweenStreets     = 4;

        this.mainStreetMaxLength = 800;

        // maximal values for a Building
        this.maxBuildingWidth  = 8;
        this.maxBuildingHeight = 10;
        this.maxBuildingArea   = 48;

        //minimal values for a Building
        this.minBuildingWidth  = 3;
        this.minBuildingHeight = 2;
        this.minBuildingDepth  = 1.8;

    }



    /**
     * This function compute the normal of an given face.
     * @param face:                 THREE.Face3-Object.
     * @param verticesOfCurrentGeo: Vertices of the geometry.
     * @returns {Vector3}         : Return the face normal as an THREE.Vector3-Object.
     */
    static computeFaceNormal(face, verticesOfCurrentGeo) {

        let a = verticesOfCurrentGeo[face.a];
        let b = verticesOfCurrentGeo[face.b];
        let c = verticesOfCurrentGeo[face.c];

        let ab = new THREE.Vector3();
        ab.subVectors(b, a);
        let ac = new THREE.Vector3();
        ac.subVectors(c, a);

        ab.cross(ac);

        return ab;

    }



    /**
     * This function merge the attributes of an given face in the arrays: colors, normals and vertices.
     * this function is used to merge the faces of an geometry.
     * @param face:                 Face to be merged.
     * @param verticesOfCurrentGeo: vertices of the geometry.
     * @param color:                color of the face.
     * @param colors:               Arrays to merge the colors of the vertices of the face.
     * @param normals:              Arrays to merge the normals at the vertices of the face.
     * @param vertices:             Arrays to merge the vertices of the face.
     */
    static mergeAttributeOfFace (face, verticesOfCurrentGeo, color, colors, normals, vertices) {
        let normalN = CodeMapUtilities.computeFaceNormal(face, verticesOfCurrentGeo);
        for (let i = 0; i < 3; i ++) {
            colors  .push(255*color.r, 255*color.g, 255*color.b);
            normals .push(normalN.x, normalN.y, normalN.z);
        }

        vertices.push(verticesOfCurrentGeo[face.a].x, verticesOfCurrentGeo[face.a].y, verticesOfCurrentGeo[face.a].z);
        vertices.push(verticesOfCurrentGeo[face.b].x, verticesOfCurrentGeo[face.b].y, verticesOfCurrentGeo[face.b].z);
        vertices.push(verticesOfCurrentGeo[face.c].x, verticesOfCurrentGeo[face.c].y, verticesOfCurrentGeo[face.c].z);

    }



    /**
     * This function merge all object of the scene graph and add this to the scene.
     * @param scene:      The scene.
     * @param sceneGraph: scene graph contained all object to be added in the scene.
     * @returns {Array} : Array of selectable object in the scene.
     *                    This list is used later to perform the selection of object in the scene.
     */
    static mergeAndAddObjectsToScene(scene, sceneGraph) {
        if(sceneGraph === undefined){
            return [];
        }

        CodeMapUtilities.traversSceneGraph(sceneGraph);

        // add the streets to the scene.
        let streetsObjects = CodeMapUtilities.extractStreetObjects(sceneGraph);
        CodeMapUtilities.mergeStreetsAndAddToScene(scene, streetsObjects);

        // Add the main streets to the scene!
        let mainStreets       = CodeMapUtilities.extractMainStreets(sceneGraph);
        CodeMapUtilities.mergeMainStreetsAndAddToScene(scene, mainStreets);

        // Add the final street to the scene
        let finalStreet = this.extractFinalStreet(sceneGraph);
        this.addFinalStreetToScene(scene, finalStreet);

        return streetsObjects;
    }

    static extractFinalStreet(sceneGraph) {
        let number    = sceneGraph.children.length;
        return (number >= 1) ? sceneGraph.children [number - 1] : null;
    }

    static addFinalStreetToScene(scene, finalStreet) {
        if(finalStreet === null ) {
            return;
        }

        finalStreet.myContent.transformModelMatrix(finalStreet.matrix);
        let endStreetGeo = finalStreet.myContent.createGeometry();
        let endStreetMat = finalStreet.myContent.createMaterial();
        endStreetGeo.applyMatrix(finalStreet.myContent.getModelMatrix());
        let mesh3 = new THREE.Mesh(endStreetGeo, endStreetMat);
        scene.add(mesh3);

    }

    static mergeMainStreetsAndAddToScene(scene, mainStreets) {

        for(let i = 0; i < mainStreets.length; i++) {
            let number = mainStreets[i].children.length;
            let mainStreet = mainStreets[i].children[number-1];

            mainStreet.myContent.transformModelMatrix(mainStreet.matrix);

            let mGeo = mainStreet.myContent.createGeometry();
            mGeo.applyMatrix(mainStreet.myContent.getModelMatrix());

            let mat  = mainStreet.myContent.createMaterial();
            CodeMapUtilities.addMeshToScene(scene, new THREE.Mesh(mGeo, mat));
        }

    }


    static mergeStreetsAndAddToScene(scene, streets) {

        let colors   = [];
        let normals  = [];
        let vertices = [];

        let usedGeometries     = CodeMapUtilities.generateUsedGeometries();
        let originalGeometries = CodeMapUtilities.generateUsedGeometries();
        let geometry = new THREE.BufferGeometry();
        geometry.dynamic = true;
        let mergedGeoId = geometry.id;
        let leafs = [];

        for (let i = 0; i < streets.length; i ++) {
            CodeMapUtilities.extractLeafs(streets[i], leafs);
            CodeMapUtilities.mergeObjects(leafs, mergedGeoId, usedGeometries, originalGeometries, colors, normals, vertices);
            leafs.length = 0;
        }

        let positionAttribute = new THREE.Float32BufferAttribute(vertices, 3);
        let normalAttribute   = new THREE.Int16BufferAttribute(normals, 3);
        let colorAttribute    = new THREE.Uint8BufferAttribute(colors, 3);

        normalAttribute.normalized = true;
        colorAttribute .normalized = true;

        geometry.addAttribute("position", positionAttribute);
        geometry.addAttribute("normal",   normalAttribute);
        geometry.addAttribute("color",    colorAttribute);

        let material = new THREE.MeshLambertMaterial({
            color: 0xffffff,
            vertexColors: THREE.VertexColors
        });

        CodeMapUtilities.addMeshToScene(scene, new THREE.Mesh(geometry, material));
    }

    static addMeshToScene(scene, meshToAdd) {
        scene.add(meshToAdd);
    }


    /**
     * This function extract from the scene graph all object that represent an main street.
     * @param sceneGraph    : the scene graph.
     */
    static extractMainStreets(sceneGraph) {
        let container = [];
        CodeMapUtilities.extractElementsWithDepth(sceneGraph, container, 4);
        return container;
    }



    /**
     * This function extract all elements of the scene graph that represent a street.
     * @param sceneGraph:    the scene graph.
     */
    static extractStreetObjects(sceneGraph) {
        let container = [];
        CodeMapUtilities.extractElementsWithDepth(sceneGraph, container, 3);
        return container;
    }



    /**
     * This function extract the leafs of the scene graph
     * @param object:    the scene graph
     * @param container: container to store the leafs
     */
    static extractLeafs(object, container) {
        CodeMapUtilities.extractElementsWithDepth(object, container, 0);
    }

    static extractLeafsI(object) {
        let leafs = [];
        CodeMapUtilities.extractElementsWithDepth(object, leafs, 0);
        return leafs;
    }



    /**
     * This function extract from the scene graph all element with element.depth = depth.
     * @param object:    The scene graph.
     * @param container: The container to store all ok-objects.
     * @param depth:     The depth that the elements should have.
     */
    static extractElementsWithDepth(object, container, depth) {

        const d = CodeMapUtilities.getObjectMaxDepth(object);

        if (d === depth) {
            container.push(object);
        } else if (d > depth) {

            for(let i = 0; i < object.children.length; i++) {
                CodeMapUtilities.extractElementsWithDepth(object.children[i], container, depth);
            }

        }

    }



    /**
     * This function is used to create unit geometries.
     * @returns {Array} : an array of 3 geometries (CubeGeometry, CylinderGeometry, PlaneGeometry).
     */
    static generateUsedGeometries() {

        let cube     = new THREE.CubeGeometry(1, 1, 1);
        let plane    = new THREE.PlaneGeometry(1, 1);
        let cylinder = new THREE.CylinderGeometry(1, 1, 1, 20);

        cube.matrix     = new THREE.Matrix4();
        plane.matrix    = new THREE.Matrix4();
        cylinder.matrix = new THREE.Matrix4();

        let result = [];
        result.push(cube);
        result.push(cylinder);
        result.push(plane);

        return result;
    }



    /**
     * This function merge the geometries of objects. The colors, normals and
     * vertices of the objects are stored in arrays.
     * @param objects:     Array of object to be stored.
     * @param mergedGeoId: Id of the merged geometry.
     * @param geometries:  Array of 3 geometries. one of them will be used.
     * @param originals:  Unmodified geometries. this are used to reset the geometries after merge.
     * @param colors:     Array for the colors of all merged elements.
     * @param normals:    Array for the normals of all merged elements.
     * @param vertices:   Array for the vertices of all merged elements.
     */
    static mergeObjects(objects, mergedGeoId, geometries, originals, colors, normals, vertices) {

        for(let i = 0; i < objects.length; i++) {
            CodeMapUtilities.mergeAttributesOfObject(objects[i], mergedGeoId, geometries, originals, colors, normals, vertices);
        }

    }



    /**
     * This function create and return an scale transform matrix
     * for an given Abstract3D-object.
     * @param abstract3d: Object for their the matrix will be created.
     * @returns {*}     : Return a THREE.Matrix4-Object.
     */
    static generateScaleMatrix(abstract3d) {

        let result;

        switch (abstract3d.getType()) {
            case 0:
                result = new THREE.Matrix4().makeScale(abstract3d.getWidth(), abstract3d.getHeight(), abstract3d.getDepth());
                break;
            case 1:
                result = new THREE.Matrix4().makeScale(abstract3d.getWidth()/2, abstract3d.getHeight(), abstract3d.getWidth()/2);
                break;
            case 2:
                result = new THREE.Matrix4().makeScale(abstract3d.getWidth(), abstract3d.getHeight(), 1);
                break;
            default:
                result = new THREE.Matrix4().makeScale(1, 1, 1);
        }

        return result;
    }



    /**
     * This function traverse the scene graph and set for each object the
     * transform matrix.
     * @warning: *This function should only be called once.
     *           *There are side effects within the inner nodes (matrices).
     * @param object: this object contains the scene graph.
     * @param matrix: matrix to transform the scene.
     */
    static traverseSceneGraph(object, matrix) {

        if(CodeMapUtilities.getObjectMaxDepth(object) === 0) {
            object.matrix.premultiply(matrix);
        } else {
            let nextMatrix = object.matrix.premultiply(matrix);
            for(let i = 0; i < object.children.length; i++) {
                CodeMapUtilities.traverseSceneGraph(object.children[i], nextMatrix);
            }
        }

    }



    /**
     * This function is do the same as traverseSceneGraph(object, matrix).
     * @warning: This function should only be called once.
     * @param object: this object contains the scene graph.
     */
    static traversSceneGraph(object) {
        CodeMapUtilities.traverseSceneGraph(object, new THREE.Matrix4());
    }



    /**
     * This function compute the maximum depth of an given element of the scene graph.
     * @param object:     Object their maxDepth is searched.
     * @returns {number}: maximum depth of the Object.
     */
    static getObjectMaxDepth(object) {
        if (object.children.length === 0) {
            return 0;
        }

        let result = 0;
        for (let i = 0; i < object.children.length; i++) {
            let childDepth = CodeMapUtilities.getObjectMaxDepth(object.children[i]);
            if (result < childDepth) {
                result = childDepth;
            }
        }

        return 1 + result;
    }



    /**
     * This function is used to merge an object.
     * @param object:      Object to be merged
     * @param mergedGeoId: The ID of the merged geometry.
     * @param geometries:  Array of 3 geometries. one of them will be used.
     * @param originals:   Unmodified geometries. this are used to reset the geometries after merge.
     * @param colors:      Array for the colors of all merged elements.
     * @param normals:     Array for the normals of all merged elements.
     * @param vertices:    Array for the vertices of all merged elements.
     */
    static mergeAttributesOfObject(object, mergedGeoId, geometries, originals, colors, normals, vertices) {

        let content  = object.myContent;
        let geoIndex = content.getType();
        let geometry = geometries[geoIndex];

        let scaleM          = CodeMapUtilities.generateScaleMatrix(content);
        let transformMatrix = content.getModelMatrix().premultiply(object.matrix);
        transformMatrix     = scaleM.premultiply(transformMatrix);

        geometry.applyMatrix(transformMatrix);

        content.verticesOffsetByMerge = colors.length;
        content.verticesNum           = geometry.faces.length*3;
        content.mergedGeoId           = mergedGeoId;

        let color    = new THREE.Color(content.getColor());

        for (let i = 0; i < geometry.faces.length; i ++) {
            CodeMapUtilities.mergeAttributeOfFace(geometry.faces[i], geometry.vertices, color, colors, normals, vertices);
        }

        CodeMapUtilities.reinitializeGeometry(geometry, originals[geoIndex]);
    }



    /**
     * This function is used to reinitialize a geometry after the merge action. By merge the vertices
     * and normals are changed (transform) therefore we use an original copy for reinitializing.
     * @param copy:   dirty geometry.
     * @param origin: original copy.
     */
    static reinitializeGeometry(copy, origin) {

        for(let i = 0; i < copy.vertices.length; i++) {
            copy.vertices[i].copy(origin.vertices[i]);
        }

        for(let i = 0; i < copy.faces.length; i++) {
            copy.faces[i].copy(origin.faces[i]);
        }

        copy.matrix.identity();
    }



    /**
     * This function create the ground of an column.
     * @param columnWidth
     * @param columnDepth
     * @param color
     * @returns {Abstract3D}
     */
    static createGroundOfColumn (columnWidth, columnDepth, color) {
        let ground = new Abstract3D(2, -1, columnWidth, columnDepth, color);
        ground.transformModelMatrix(new THREE.Matrix4().makeRotationX(-0.5*Math.PI));
        return ground;
    }



    /**
     * This function create an empty street.
     * @param streetWidth
     * @param streetDepth
     * @param marge
     */
    static createEmptyStreet (streetWidth, streetDepth, marge) {

        let emptyStreet = new Abstract3D(2, -1, streetWidth, streetDepth, "#ffffff");
        emptyStreet.transformModelMatrix(new THREE.Matrix4().makeRotationX(- 0.5*Math.PI));
        emptyStreet.transformModelMatrix(new THREE.Matrix4().makeTranslation((streetWidth/2) - marge, 0, streetDepth/2));
        let result = new THREE.Object3D();
        result.myContent = emptyStreet;
        return result;
    }



    /**
     * This function create an empty main street.
     * @param mainStreetWidth
     * @param streetDepth
     * @param marge
     * @param color
     * @returns {SEA3D.Object3D|THREE.SEA3D.Object3D|*}
     */
    static createEmptyMainStreet (mainStreetWidth, streetDepth, marge, color) {

        let emptyMainStreet = new Abstract3D(2, -1, mainStreetWidth, streetDepth, color);
        emptyMainStreet.texture = "mstr";
        emptyMainStreet.transformModelMatrix(new THREE.Matrix4().makeRotationX(-0.5*Math.PI));
        emptyMainStreet.transformModelMatrix(new THREE.Matrix4().makeTranslation(-mainStreetWidth/2, 0, marge+(-streetDepth/2)));
        let result = new THREE.Object3D();
        result.myContent = emptyMainStreet;
        return result;
    }



    /**
     *
     * @param metricValue
     * @param metricFactor
     * @param minLength
     * @param maxLength
     * @returns {*}
     */
    static metricValueToLength (metricValue, metricFactor, minLength, maxLength) {
        return minLength + (metricValue * metricFactor * maxLength);
    }



    /**
     * This function create an Abstract3D-Object.
     * @param geometryTyp
     * @param id
     * @param width
     * @param height
     * @param depth
     * @param diameter
     * @param color
     * @returns {Abstract3D}
     */
    static createAbstract3D (geometryTyp, id, width, height, depth, diameter, color, isTransparent) {
        let result = null;
        switch (geometryTyp) {
            case 0: result = new Cube    (id, width, height, color, depth); break;
            case 1: result = new Cube    (id, width, height, color, depth); break;//new Cylinder(id, diameter, height, color);
            case 2: result = new Plane   (id, width, height, color);        break;
        }
        result.transparent = isTransparent;
        return result;
    }

    /**
     * This function create an matrix to place a block over an street.
     * @param length: block length.
     * @returns {*}:  return an THREE.Matrix4-Object.
     */
    static createMatrixToPlaceBlockOverStreet(length) {
        return new THREE.Matrix4().makeTranslation(length, 0, 0);
    }



    /**
     * This function create an matrix to place a block under an street.
     * @param block:       the block, that have to be place under the street.
     * @param length:      the length of the current street.
     * @param streetDepth: the depth of the street.
     * @returns {*}
     */
    static createMatrixToPlaceBlockUnderStreet(block, length, streetDepth) {
        let matrix = new THREE.Matrix4().makeRotationY(Math.PI);
        let translationX =  (block.blockProperties.blockWidth + length);
        matrix.premultiply(new THREE.Matrix4().makeTranslation(translationX, 0, streetDepth));

        return matrix;
    }



    /**
     * This function generate an matrix to place an main street over the final street.
     * @param length: the current length of the final street (x-axe).
     * @returns {*}:  return an THREE.Matrix4-Object for the transformation.
     */
    static createMatrixToPlaceMainStreetOver(length) {
        return new THREE.Matrix4().makeTranslation(length, 0, 0);
    }



    /**
     * This function generate an matrix to place an main street under the final street.
     * @param length: current length of the final street(x-axe).
     * @param marge:  current width of the final street (z-axe).
     * @returns {*}:  return an THREE.Matrix4 for the transformation.
     */
    static createMatrixToPlaceMainStreetUnder(length, marge) {

        let result = new THREE.Matrix4().makeRotationY(Math.PI);
        result.premultiply(new THREE.Matrix4().makeTranslation(length, 0, marge));
        return result;

    }




    /**
     * This function generate an matrix to place an street on left side of an main street.
     * @param depth:           the depth of the main street (z-axe).
     * @param mainStreetWidth: the width of the main street (x-axe).
     * @returns {*}:           return an THREE.Matrix4-Object.
     */
    static createMatrixToPlaceStreetLeft(depth, mainStreetWidth) {

        let matrix = new THREE.Matrix4().makeRotationY(Math.PI);
        matrix.premultiply(new THREE.Matrix4().makeTranslation(-mainStreetWidth, 0, -depth));
        return matrix;

    }



    /**
     * This function is used to place an street on the right side of an main street.
     * @param depth: the current depth of the main street(z-axe and right side).
     * @returns {*}: return an THREE.Matrix4 for the transformation.
     */
    static createMatrixToPlaceStreetRight(depth) {
        return new THREE.Matrix4().makeTranslation(0, 0, -depth);
    }



    /**
     * this function get an JSON-Node and an expected depth. the function control
     * if the the expected depth equals the actual depth.
     * @param node        -- JSON-Node
     * @param expected    -- expected depth as Int
     * @returns {boolean} -- return the predicate result;
     */
    static depthCheck(node, expected) {
        return StaticJsonHandler.treeDepth(node) === expected;
    }
}