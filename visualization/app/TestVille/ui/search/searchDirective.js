"use strict";

import {SearchController} from "./searchController.js";


class SearchDirective{




    constructor() {


        this.templateUrl = "./search.html";


        this.restrict = "E";


        this.scope = {};


        this.controller = SearchController;


        this.controllerAs = "ctrl";


        this.bindToController = true;
    }

    //declares whether the inputfield is visible or not
    link(scope, element) {
        element.find("#searchButton").bind("click", this.toggle);
        element.find("#searchPanel").bind("change", this.method);

    }

    //animates the inputfield
    toggle(){
        if (this.visible) {
            $("#searchPanel").animate({left: -900 + "px"});
            this.visible = false;
        } else {
            $("#searchPanel").animate({left: 4.0+"em"});
            this.visible = true;
        }
    }







}




export {SearchDirective};