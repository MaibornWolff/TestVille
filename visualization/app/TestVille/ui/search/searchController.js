"use strict";

class SearchController{

    constructor( settingsService, dataService, codeMapService, $rootScope){

        this.$rootScope = $rootScope;

        this.settingsService = settingsService;

        this.dataService = dataService;

        this.codeMapService = codeMapService;

        this.resultsList = [];

        this.input = "";

        //limits the Number of the shown suggestions
        this.upperLimit= 25;

        this.container = document.getElementById("container");

        this.myStyle = {width: `${225}px`};

        this.eventNotThrownYet = true;

        this.attributeList= ["reporter", "assignee", "type", "name"];


    }




    changingInput(){

        this.upperLimit=25;
        this.eventNotThrownYet=true;



        //calculates the inputfieldwidth
        var element = document.createElement("pre");
        element.style = "font: inherit; display: inline";
        this.container.appendChild(element);
        element.innerHTML = this.input;
        var rect = element.getBoundingClientRect();
        element.remove();

        this.px = rect.width;

        if(this.px>205){

            this.myStyle = {width: `${this.px+20}px`};

        }else{

            this.myStyle={width: `${225}px`};

        }

    }

    keyPressed(keyCode, input){

        var attributes= [];
        var inputs= [];

        if(this.startsWithAttribute(input)){

            var attributeWithInput= input.split(",");

            for( var i=0; i<attributeWithInput.length; i++ ) {

                var attributeAndInput = attributeWithInput[i].split(":");
                attributes.push(this.CorrectAttribute(attributeAndInput[0]));
                inputs.push(attributeAndInput[1]);
                this.filterTheList(attributes, inputs);

            }

        }else {

            attributes.push("name");
            inputs.push(input);
            this.filterTheList(attributes, inputs);
        }

        this.raiseUpperLimit(keyCode);
        this.selectAndUnselect(keyCode, input);


    }

    startsWithAttribute(input){

        for(var i=0; i<this.attributeList.length; i++){
            if(input.toLowerCase().startsWith(this.attributeList[i])){

                return true;
            }
        }

        return false;
    }

    CorrectAttribute(attribute){

        for(var i=0; i<this.attributeList.length; i++){
            if(attribute.toLowerCase()===this.attributeList[i].toLowerCase()){
                return this.attributeList[i];
            }
        }
    }

    filterTheList(attributes, inputs){



        this.resultsList = this.codeMapService.searchList.filter(function (el){

            for(var i=0; i<attributes.length; i++){

                inputs[i]=inputs[i].trim();
                if(el[attributes[i]].toLowerCase().indexOf(inputs[i].toLowerCase()) === -1){

                    return false;
                }
            }
            return true;
        });



    }

    raiseUpperLimit(keyCode){

        const altKey = 18;
        if(keyCode === altKey){

            this.upperLimit= this.upperLimit+25;
        }


    }

    selectAndUnselect(keyCode, input) {



            const spaceKey = 32;
            const enterKey = 13;

            if(this.resultsList.length === 1) {

                if (this.eventNotThrownYet) {

                    if (keyCode === enterKey) {

                        this.$rootScope.$broadcast("onsearch", {searched: this.resultsList[0].name});

                        this.eventNotThrownYet = false;
                    }


                } else if (keyCode === spaceKey) {

                    this.input = "";
                    this.$rootScope.$broadcast("offsearch");
                    this.myStyle = {width: `${225}px`};
                    this.eventNotThrownYet = true;


                }

            }

    }

}



export {SearchController};

