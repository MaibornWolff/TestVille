/**
 * visualization of test data
 * Maibornwolff_ATM_TestLandKarten(TesLa)
 *
 * @Module:      visualization
 * @File :      Abstract3D.js
 * @LastUpdate: __/10/2017
 * @PO:         Francesco Guiseppe La Torre
 * @Author:     JNiada
 * @Comment:
 *          Abstract3D is an abstract definition of a 3d object.
 *          18.09.2017: 2 types(Cube or Cylinder)
 *          model is the transformMatrix of the 3d Object. At End we 
 *          just have to apply the transformMatrix on the object to set the position of the
 *          3d Object in the scene. Object3D
 */

"use strict";

import {GraphNodeInfo} from "./GraphNodeInfo.js";
import * as THREE from "three";


export class Abstract3D {

    /**
     * data type: Abstract3D
     *
     * @param type: 
     * @param id:
     * @param width: 
     * @param height: 
     * @param color:
     * @param depth: 
     */
    constructor(type, id, width, height, color, depth) {
	    this.id                    = id;
	    this.type                  = type;
	    this.height                = height; // y-axe
        this.width                 = width;  // x-axe
        this.depth                 = depth;  // z-axe
        this.color                 = color;
	    this.transparent           = false;
	    this.texture               = ""; // str or mw or mstr (street,  mainStreet)
        this.verticesNum           = 0;
        this.verticesOffsetByMerge = 0;
	    this.faceOffsetInMergedGeo = 0;
	    this.mergedGeoId           = 0;
        this.model                 = new THREE.Matrix4(); // Transform matrix
        this.nodeInfo              = null;
    }
    
    
    
    /**
     * Getter for faceOffsetInMergedGeo
     */
    getFaceOffset() {
	    return this.faceOffsetInMergedGeo;
    }
    


    /**
     * Setter for faceOffsetInMergedGeo
     */
    setFaceOffset(offset) {
	    this.faceOffsetInMergedGeo = offset;
    }



    /**
     * Getter for id
     */
    getId() { return this.id; }



    /**
     * Getter for texture
     */
    getTex() {  return this.texture; }



    /**
     * Setter for texture
     */
    setTex(tex) {   this.texture = tex; }
    
    

    /**
     * Getter for type.
     * @return: {Number}: type of the Abstract3D-Object.
     */
    getType() {    return this.type; }



    /**
     * Getter for width.
     * @return: {Number}: width of Abstract3D or an Exception.
     */
    getWidth() { return this.width; }
    
    /**
     * Getter for height.
     * @return: {Number}: height of Abstract3D.
     */
    getHeight() { return this.height; }



    /**
     * Getter for depth.
     * @return: {Number}: depth of the Abstract3D object or an Exception.
     */
    getDepth() { return this.depth; }
    

    
    /**
     * Getter for model (Transform matrix).
     * @return: {Matrix4}
     */
    getModelMatrix() { return this.model; }



    /**
     * Getter for color.
     * @return {String}
     */
    getColor() { return this.color; }
    
    
    
    /**
     * to manipulate the modelMatrix of Abstract3D-Object
     * Warning: we use here preMultiply. that mean the correspondent
     * transformation in matrix have to be apply after the correspondent
     * transformation in model
     */
    transformModelMatrix(matrix) { this.model.premultiply(matrix); }
    
    
    
    /**
     * create the correspondent material for the Abstract3D-Object.
     * @return: {Material} for drawing the Abstract3D object.
     */
    createMaterial() {

	    let material = new THREE.MeshLambertMaterial({color: this.getColor(), needsUpdate:true});

	    if(this.getType() === 2) {

            let texture;

	        if(this.getTex().toLowerCase().localeCompare("str") === 0) {

	             texture = this.loadTexture();

                texture.wrapS = THREE.RepeatWrapping;
                texture.wrapT = THREE.RepeatWrapping;
                texture.repeat.set(this.getWidth()/15, 1);

                material = new THREE.MeshLambertMaterial({map: texture});
		
            } else if(this.getTex().toLowerCase().localeCompare("mstr") === 0) {

                texture = this.loadTexture();
                texture.wrapT = THREE.RepeatWrapping;
                texture.wrapS = THREE.RepeatWrapping;
                texture.repeat.set(1, this.getHeight()/12);

                material = new THREE.MeshLambertMaterial({map: texture});


            }
                material.side = THREE.DoubleSide;
        }

        material.vertexColors = THREE.FaceColors;


        return material;
	
    }



    /**
     * create the correspondent geometry to Abstract3D object.
     * @return: {Geometry}.
     */
    createGeometry() {
	
        let geometry;

        switch(this.getType()) {

            case 0:
                geometry = new THREE.CubeGeometry(this.getWidth(), this.getHeight(), this.getDepth());
                break;

            case 1:
                geometry = new THREE.CylinderGeometry(this.getWidth()/2, this.getWidth()/2, this.getHeight(), 30);
                break;

            case 2:
                geometry = new THREE.PlaneGeometry(this.getWidth(), this.getHeight());
                break;

            default:
                throw "the Abstract3D-Object has a unknown geometry type!";
        }

        return geometry;
	
    }



    /**
     * construct and return the correspondent BoundingBox of Abstract3D.
     * For Ray-tracing(Picking).
     * @return: {Box3}: BoundingBox
     */
    getBoundingBox() {
	    let arr = this.getMinAndMax();
	    return new THREE.Box3(arr[0], arr[1]);
    }
    
    
    
    /**
     * Compute the Vertices of the correspondent BoundingBox of Abstract3D
     * @return: {Array} with the 8 Vertices of the BoundingBox
     */
    getVertices() {

        const result = [];

        if(this.getType() === 2) {
            result.push(new THREE.Vector3(-this.getWidth()/2, -this.getHeight()/2, 0));
            result.push(new THREE.Vector3(-this.getWidth()/2, this.getHeight()/2, 0));
            result.push(new THREE.Vector3(this.getWidth()/2, -this.getHeight()/2, 0));
            result.push(new THREE.Vector3(this.getWidth()/2, this.getHeight()/2, 0));
            return result;
        }
	    result.push(new THREE.Vector3(-this.getWidth()/2, -this.getHeight()/2, this.getDepth()/2));
        result.push(new THREE.Vector3(this.getWidth()/2, -this.getHeight()/2, this.getDepth()/2));
        result.push(new THREE.Vector3(-this.getWidth()/2, this.getHeight()/2, this.getDepth()/2));
        result.push(new THREE.Vector3(this.getWidth()/2, this.getHeight()/2, this.getDepth()/2));
        result.push(new THREE.Vector3(-this.getWidth()/2, -this.getHeight()/2, -this.getDepth()/2));
        result.push(new THREE.Vector3(this.getWidth()/2, -this.getHeight()/2, -this.getDepth()/2));
        result.push(new THREE.Vector3(-this.getWidth()/2, this.getHeight()/2, -this.getDepth()/2));
        result.push(new THREE.Vector3(this.getWidth()/2, this.getHeight()/2, -this.getDepth()/2));

        return result;

    }
    


    /**
     * Compute the coordinates of the Vertices(BoundingBox) after applying of the model-Matrix
     * @return: {Array}: with transformed Vertices
     */
    getVerticesAfterTransform() {
        let result = this.getVertices();

        for(let i = 0; i < result.length; i++) {
            result[i].applyMatrix4(this.getModelMatrix());
        }

        return result;
    }



    /**
     * this function select the max und min values in each Axes of the
     * BoundingBox.
     * @returns {Array}: min and max-vector in an array
     */
    getMinAndMax() {
	
        let toCheck = this.getVerticesAfterTransform();

        let min = toCheck[0].toArray();
        let max = toCheck[0].toArray();

        let array;
        let j = 0;

        for(let i = 1; i < toCheck.length; i++) {
            array = toCheck[i].toArray();

            for(j = 0; j < array.length; j++) {

                if(min[j] > array[j]) {
                    min[j] = array[j];
                }

                if(max[j] < array[j]) {
                    max[j] = array [j];
                }
            }

        }

        let result = [];

        let u = new THREE.Vector3(min[0], min[1], min[2]);
        let v = new THREE.Vector3(max[0], max[1], max[2]);

        result.push(u);
        result.push(v);

        return result;
    }


    initInformationToDisplay(nodeInfoType, name, areaM, heightM, colorM) {
        this.nodeInfo = new GraphNodeInfo(nodeInfoType);
        this.nodeInfo.init(this.getId(), name, areaM, heightM, colorM);
    }

    getToDisplayedInfo() { return this.nodeInfo; }


    /**
     * load the correspondent Texture for drawing the Abstract3D object.
     * @return: {Texture}.
     */
    loadTexture() {

        if(this.getTex().length === 0) {
            return null;
        }

        let texture;
        let path = "./img/";

        if(this.getTex().localeCompare("str") === 0){
            path += "reqStr.jpg";
        }else{
            path += Abstract3D.colorAsString(this.getColor()) + ".jpg";
        }

        texture = new THREE.TextureLoader().load(path);

        return texture;
    }


    /**
     * return the Hex-representation of str.
     * @param color: color as hex-value.
     * @returns {string}: Hex-value of the color.
     */
    static colorAsString(color) {

        if(color === 0x00ff00) {
            return  "green";
        }else if(color === 0xff0000) {
            return "red";
        }else if(color === 0xffff00) {
            return "yellow";
        }else if(color === 0x0000ff) {
            return "blue";
        }else if(color === 0xff8c00){
            return "orange";
        }

        return "unknown";
    }

}
