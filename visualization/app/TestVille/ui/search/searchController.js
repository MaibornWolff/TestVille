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

        this.metricList= ["reporter", "assignee", "type", "created", "name"];


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

        var metrics= [];
        var inputs= [];

        if(this.startsWithMetric(input)){

            var metricWithInput= input.split(",");

            for( var i=0; i<metricWithInput; i++ ) {

                var metricAndInput = metricWithInput.split(":");
                metrics.push(this.theCorrectMetric(metricAndInput[0]));
                inputs.push(metricAndInput[1]);
                this.filterTheList(metrics, inputs);

            }

        }else {

            metrics.push("name");
            inputs.push(input);
            this.filterTheList(metrics, inputs);
        }

        this.raiseUpperLimit(keyCode);
        this.selectAndUnselect(keyCode, input);


    }

    startsWithMetric(input){

        console.log("startswithmetric");
        for(var i=0; i<this.metricList.length; i++){
            if(input.toLowerCase().startsWith(this.metricList[i])){

                return true;
            }
        }

        return false;
    }

    theCorrectMetric(metric){

        for(var i=0; i<this.metricList.length; i++){
            if(metric.toLowerCase().equals(this.metricList[i].toLowerCase())){
                return this.metricList[i];
            }
        }
    }

    filterTheList(metrics, inputs){

        this.resultsList = this.codeMapService.searchList.filter(function (el){

            for(var i=0; i<metrics.length; i++){


                console.log("hier vielleicht");
                if(el[metrics[i]].toLowerCase().indexOf(inputs[i].toLowerCase()) === -1){
                    console.log("vielleicht");
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

        if (this.InputMatchesExactlyOneElementInTheList(input)) {

            const spaceKey = 32;
            if (keyCode === spaceKey) {

                this.input = "";
                this.$rootScope.$broadcast("offsearch");
                this.myStyle = {width: `${225}px`};
                this.eventNotThrownYet = true;


            } else if (this.eventNotThrownYet) {

                this.$rootScope.$broadcast("onsearch", {searched: this.resultsList[0]});

                this.eventNotThrownYet = false;

            }

        }
    }

    InputMatchesExactlyOneElementInTheList(input){

        return this.resultsList.length===1 && input.toLowerCase()===this.resultsList[0].name.toLowerCase();
    }
}



export {SearchController};

