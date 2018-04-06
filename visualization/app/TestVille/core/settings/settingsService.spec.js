import {Settings} from "./model/settings";
import {Range} from "./model/range";
import {Scale} from "./model/scale";

require("./settings.js");

/**
 * @test {SettingsService}
 */
describe("app.testVille.core.settings.settingsService", function() {

    beforeEach(angular.mock.module("app.testVille.core.settings"));
    
    describe("settingsService.constructor.test", ()=>{

        it("should retrieve the angular service instance with enabled delta cubes and no details selected", angular.mock.inject(function(settingsService){
            expect(settingsService).to.not.equal(undefined);
            expect(settingsService.settings.deltas).to.equal(true);
        }));

    });

    describe("settingsService.onDataChange.tests",()=>{

        it("should change the metrics when data has diferent",angular.mock.inject(function(settingsService){

            var data= {

                currentmap:{"name":"somename"},
                metrics:["c","a","b"]
            }
            var codemapBefore= settingsService.settings.map;
            var areaMetricBefore= settingsService.settings.areaMetric;
            var heightMetricBefore= settingsService.settings.heightMetric;
            var colorMetricBefore= settingsService.settings.colorMetric;



            settingsService.onDataChanged(data);


            expect(settingsService.settings.map).to.not.deep.equal(codemapBefore);
            expect(settingsService.settings.areaMetric).to.not.deep.equal(areaMetricBefore);
            expect(settingsService.settings.heightMetric).to.not.deep.equal(heightMetricBefore);
            expect(settingsService.settings.colorMetric).to.not.deep.equal(colorMetricBefore);


        }));

        it("should not change the metrics when data has the same",angular.mock.inject(function (settingsService) {

            var data= {

                currentmap:settingsService.settings.map,
                metrics: [
                    settingsService.areaMetric,
                    settingsService.heightMetric,
                    settingsService.colorMetric
                ]
            }
            var areaMetricBefore= settingsService.settings.areaMetric;
            var heightMetricBefore= settingsService.settings.heightMetric;
            var colorMetricBefore= settingsService.settings.colorMetric;


            settingsService.onDataChanged(data);


            expect(settingsService.settings.areaMetric).to.equal(areaMetricBefore);
            expect(settingsService.settings.heightMetric).to.equal(heightMetricBefore);
            expect(settingsService.settings.colorMetric).to.equal(colorMetricBefore);



        }))


    });

    describe("service.onCameraChanged.test", ()=>{


        it("should react to camera-changed events", angular.mock.inject(function(settingsService, $rootScope){

            settingsService.onCameraChanged = sinon.spy();

            $rootScope.$broadcast("camera-changed", {});

            expect(settingsService.onCameraChanged.calledOnce).to.be.true;

        }));


        it("should update settings object but not call onSettingsChanged to ensure performance", angular.mock.inject(function(settingsService){

            settingsService.onSettingsChanged = sinon.spy();


            settingsService.onCameraChanged({position: {x:0, y:0, z: 42}});


            expect(settingsService.onSettingsChanged.called).to.be.false;
            expect(settingsService.settings.camera.z).to.equal(42);
        }));

        it("should react to data-changed events", angular.mock.inject(function(settingsService, $rootScope){

            settingsService.onSettingsChanged = sinon.spy();

            $rootScope.$broadcast("data-changed", {currentmap: {"name":"some map"}, metrics: ["a","b","c"]});

            expect(settingsService.settings.map.name).to.equal("some map");
            expect(settingsService.settings.areaMetric).to.equal("a");
            expect(settingsService.settings.heightMetric).to.equal("b");
            expect(settingsService.settings.colorMetric).to.equal("c");

            $rootScope.$broadcast("data-changed", {currentmap: {"name":"another map"}, metrics: ["a"]});

            expect(settingsService.settings.map.name).to.equal("another map");
            expect(settingsService.settings.areaMetric).to.equal("a");
            expect(settingsService.settings.heightMetric).to.equal("a");
            expect(settingsService.settings.colorMetric).to.equal("a");

            expect(settingsService.onSettingsChanged.calledTwice).to.be.true;

        }));


        it("should react to data-changed events and set metrics correctly", angular.mock.inject(function(settingsService, $rootScope){

            settingsService.onSettingsChanged = sinon.spy();

            $rootScope.$broadcast("data-changed", {currentmap: {"name":"yet another map"}, metrics: ["a", "b"]});

            expect(settingsService.settings.map.name).to.equal("yet another map");
            expect(settingsService.settings.areaMetric).to.equal("a");
            expect(settingsService.settings.heightMetric).to.equal("b");
            expect(settingsService.settings.colorMetric).to.equal("b");

            expect(settingsService.onSettingsChanged.calledOnce).to.be.true;

        }));


    });


    describe("service.updateSettingsFromUrl.tests", ()=>{

        it("should update settings from url", angular.mock.inject(function(settingsService, $location){

            $location.url("http://something.de?scaling.x=42&areaMetric=myMetric&scaling.y=0.32");


            settingsService.updateSettingsFromUrl();


            expect(settingsService.settings.scaling.x).to.equal(42);
            expect(settingsService.settings.scaling.y).to.equal(0.32);
            expect(settingsService.settings.areaMetric).to.equal("myMetric");
            expect(settingsService.urlUpdateDone).to.be.true;


        }));

        it("should not update settings.map from url", angular.mock.inject(function(settingsService, $location){

            $location.url("http://something.de?map=aHugeMap");
            settingsService.settings.map="correctMap";


            settingsService.updateSettingsFromUrl();

            expect(settingsService.settings.map).to.equal("correctMap");
            expect(settingsService.urlUpdateDone).to.be.true;
        }));


    });

    describe("Service.getQueryParamString.test", ()=>{

        it("should retrieve the correct query param strings", angular.mock.inject(function(settingsService){


            settingsService.settings.areaMetric = "areaStuff";
            settingsService.settings.camera.x = "2";


            expect(settingsService.getQueryParamString()).to.include("areaMetric=areaStuff");
            expect(settingsService.getQueryParamString()).to.include("camera.x=2");
            expect(settingsService.getQueryParamString()).to.include("neutralColorRange.from=10");
        }));



    });

    describe("Service.applySettings", ()=>{

        it("should apply the settings and throw the event", angular.mock.inject(function(settingsService){

            var newSettings = new Settings(
                "map",
                new Range(10,20,false),
                "area",
                "height",
                "color",
                true,
                1,
                new Scale(1,1,1),
                new Scale(0,25,40)
            );
            settingsService.correctSettings = sinon.stub().returns(newSettings);
            settingsService.onSettingsChanged = sinon.spy();

            settingsService.applySettings(newSettings);

            expect(settingsService.onSettingsChanged.calledOnce).to.be.true;
            expect(settingsService.settings).to.deep.equal(newSettings);
        }))

    });


    describe("Service.correctSettings.tests", ()=>{

        it("should replace metric with default if metric is not available", angular.mock.inject(function(settingsService, dataService){

            dataService.data.metrics = ["a", "f", "g", "h"];
            const settings = {areaMetric:"a", heightMetric:"b", colorMetric:"c"};
            const expected = {areaMetric: "a", heightMetric:"f", colorMetric:"g"};


            const result = settingsService.correctSettings(settings);


            expect(result).to.deep.equal(expected);

        }));

        it("should return input if metrics are available", angular.mock.inject(function(settingsService){

            const settings = {areaMetric:"a", heightMetric:"b", colorMetric:"c"};


            const result = settingsService.correctSettings(settings);


            expect(result).to.deep.equal(settings);

        }));

    });


    describe("Service.getMetricByIdOrLast",()=>{

        it("should return last value when id is bigger than or equal to metrics length", angular.mock.inject(function(settingsService, $rootScope){

            var arr = ["a", "b", "c"];

            var result = settingsService.getMetricByIdOrLast(32, arr);

            expect(result).to.equal("c");





            result = settingsService.getMetricByIdOrLast(3, arr);

            expect(result).to.equal("c");

        }));

        it("should return correct value when id is smaller than metrics length", angular.mock.inject(function(settingsService, $rootScope){

            var arr = ["a", "b", "c"];


            var result = settingsService.getMetricByIdOrLast(1, arr);


            expect(result).to.equal("b");

        }));

    });


    describe("Service.getMetricOrDefault.tests", ()=>{

        it("should return defaultValue when metric is not in array", angular.mock.inject(function(settingsService){

            const arr = ["a", "b", "c"];
            const name = "lookingForThis";
            const defaultValue = "default";

            const result = settingsService.getMetricOrDefault(arr, name, defaultValue);

            expect(result).to.equal(defaultValue);

        }));

        it("should return the searched value when metric is in array", angular.mock.inject(function(settingsService){

            const arr = ["a", "b", "lookingForThis"];
            const name = "lookingForThis";
            const defaultValue = "default";

            const result = settingsService.getMetricOrDefault(arr, name, defaultValue);

            expect(result).to.equal(name);

        }));

    });

});