"use strict";

/**
 * This is the main controller of the TestVille application
 */
class TestVilleController {

    /* @ngInject */

    /**
     * @constructor
     * @param {UrlService} urlService
     * @param {DataService} dataService
     * @param {SettingsService} settingsService
     */
    constructor(dataService, urlService, settingsService, scenarioService) {
        this.initHandlers();
        this.loadFileOrSample(urlService, dataService, settingsService);
        this.scenarioService = scenarioService;
        this.testvar= false;
    }

    /**
     * Tries to load the file specified in the given url. Loads sample data if it fails.
     * @param {UrlService} urlService
     * @param {DataService} dataService
     * @param {SettingsService} settingsService
     */


    loadFileOrSample(urlService, dataService, settingsService) {

        this.loadFile(urlService, dataService, settingsService, "./sample.json",
            () =>  {



                window.alert("failed loading sample data");


            }

        );

    }


    /**
     * called after map loading finished. Applies the default scenario.
     */
    loadingFinished() {
        this.scenarioService.applyScenario(this.scenarioService.getDefaultScenario());
    }

    loadFile(urlService, dataService, settingsService, path, toDoIfFail) {

        let ctx = this;
        urlService.getFileDataFromFile(path).then(
            (data) => {

                // set loaded data

                dataService.setFileData(data).then(
                    () => {
                        ctx.loadingFinished();
                        settingsService.updateSettingsFromUrl();
                    },
                    (r) => {ctx.printErrors(r);}
                );

            },

            () => toDoIfFail()
        );
    }

    /**
     * initializes keypress handlers
     */
    initHandlers() {

        $(window).keyup(function(event){
            if (event.which === 116) {
                window.location.reload();
            }
        });

        $(window).keypress(function(event){
            if (event.which === 18 && (event.ctrlKey || event.metaKey)) {
                window.location.reload();
            }
        });

    }

    /**
     * Prints errors to the browser console and alerts the user
     * @param {Object} errors an errors object
     */
    printErrors(errors){
        window.alert("Wrong format. See console logs for details.");
        console.log(errors);
    }
    
}

export {TestVilleController};


