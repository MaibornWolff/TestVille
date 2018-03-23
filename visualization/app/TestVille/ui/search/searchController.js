"use strict";

class SearchController{

    constructor( settingsService, dataService, codeMapService, $rootScope){

        this.$rootScope = $rootScope;

        this.settingsService = settingsService;

        this.dataService = dataService;

        this.codeMapService = codeMapService;

        this.resultsList = [];

        this.input = "";

        this.upperLimit= 25;

        this.container = document.getElementById("container");

        this.myStyle = {width: `${225}px`};


    }




    keyPressed(keyCode, input){



        this.resultsList = this.codeMapService.searchList.filter(function (el){
        return el.toLowerCase().indexOf(input.toLowerCase()) > -1;
        });


        if(this.InputMatchesExactlyOneElementInTheList(input)){

            const spaceKey = 32;
            if(keyCode === spaceKey) {

                this.input = "";
                this.$rootScope.$broadcast("offsearch");
                this.myStyle = {width: `${225}px`};

            }else{
                this.$rootScope.$broadcast("onsearch", {searched: this.resultsList[0]});
            }

        }


        const altKey = 18;
        if(keyCode === altKey){
            this.upperLimit= this.upperLimit+25;
        }


    }

    InputMatchesExactlyOneElementInTheList(input){

        return this.resultsList.length===1 && input.toLowerCase()===this.resultsList[0].toLowerCase();
    }



    changingInput(){

        this.upperLimit=25;




        this.element = document.createElement("span");
        this.container.appendChild(this.element);
        this.element.innerHTML = this.input;
        this.rect = this.element.getBoundingClientRect();
        this.element.remove();

        this.px = this.rect.width;

        if(this.px>205){

            this.myStyle = {width: `${this.px+20}px`};

        }else{

            this.myStyle={width: `${225}px`};

        }

    }

}



export {SearchController};

