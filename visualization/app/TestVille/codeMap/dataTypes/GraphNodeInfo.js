/**
 * visualisierung von Testdaten
 * Maibornwolff_ATM_TestLandKarten(TesLa)
 *
 * @Modul:      visualization
 * @File :      GraphNode.js
 * @LastUpdate: __/10/2017
 * @PO:         Francesco Guiseppe La Torre
 * @Author:     JNiada
 * @Coment:
 *          GraphNode contains  information of test or Requirement
 */


"use strict";



export class GraphNodeInfo {



    /**
     * to construct a new GraphNodeInfo Object
     */
    constructor(type) {

        // Properties
        this.name   = "No_Name";
        this.id     = -1;

        if((type.localeCompare("test") === 0) || (type.localeCompare("requirement") === 0)) {
            this.type = type;
        }else{
            throw "unknown type of GraphNode";
        }

        //Metrics
        this.areaMetric   = -1;
        this.heightMetric = -1;
        this.colorMetric  = -1;

    }



    /**
     * initialize the graphNode object
     * @param id
     * @param name
     * @param areaM
     * @param heightM
     * @param colorM
     */
    init(id, name, areaM, heightM, colorM) {

        this.id = id;
        this.name = name;
        this.areaMetric = areaM;
        this.heightMetric = heightM;
        this.colorMetric = colorM;

    }



    /**
     * Getter for name
     * @return {String}
     */
    getName() { return this.name;}

    

    /**
     * Getter for id.
     * @return {*}
     */
    getId() {   return this.id;}



    /**
     * Getter for areaMetric.
     * @return {Number}
     */
    getAreaMetric() {   return this.areaMetric;}


    

    /**
     * Getter for heightMetric.
     * @return {Number}
     */
    getHeightMetric() { return this.heightMetric;}



    /**
     * Getter for colorMetric.
     * @return {Number}
     */
    getColorMetric() {  return this.colorMetric;}



    /**
     * Getter for type.
     * @return {String}
     */
    getType() { return this.type;}



    /**
     * check if GraphNode represent a Test.
     * @return: {Boolean}
     */
    isTest() {  return (this.getType().localeCompare("test") === 0);}


    
    /**
     * check if GraphNode represent a requirement.
     * @return: {Boolean}
     */
    isRequirement() {   return (this.getType().localeCompare("requierment") === 0);}
    
}