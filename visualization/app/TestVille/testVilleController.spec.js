import {TestVilleController} from "./testVilleController";

require("./testVille.js");

/**
 * @test {TestVilleController}
 */
describe("app.testVille.testVilleController", function() {

    var dataService, urlService, settingsService, testVilleController, $controller, $httpBackend , scenarioService, $rootscope, dataValidatorService, $location, $http;

    beforeEach(()=>{
        angular.mock.module("app.testVille");
        angular.mock.module("app.testVille.core.data");
        angular.mock.module("app.testVille.core.settings");
        angular.mock.module("app.testVille.core.url");


    });

    beforeEach(angular.mock.inject((_$controller_, _dataService_, _settingsService_, _urlService_, _$httpBackend_, _scenarioService_)=>{
        dataService = _dataService_;
        settingsService = _settingsService_;
        urlService = _urlService_;
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        scenarioService = _scenarioService_;

    }));

    /**
     * @test {TestVilleController#initHandlers}
     */
    it("should reload page on key 18 and key 116", ()=>{

        window.location.reload = sinon.spy();

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        var event18c = $.Event( "keypress" );
        event18c.which = 18;
        event18c.ctrlKey = true;
        $(window).trigger(event18c);

        var event18m = $.Event( "keypress" );
        event18m.which = 18;
        event18m.metaKey = true;
        $(window).trigger(event18m);

        var event116 = $.Event( "keyup" );
        event116.which = 116;
        $(window).trigger(event116);

        $(window).trigger("other");

        expect(window.location.reload.calledThrice).to.be.true;

    });

    /**
     * @test {testVilleController#loadFileOrSample}
     */
    xit("should load file from existing url file param",(done)=>{

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        urlService.getUrl = ()=>{ return "file=someFile"};

        $httpBackend
            .when("GET", "someFile")
            .respond(200, "someData");

        dataService.setFileData = sinon.stub();

        var toDoIfFail= ()=>{console.log("test")}
        var path="./sample.json";

        testVilleController.loadFile(urlService, dataService, settingsService, path, toDoIfFail).then(
            ()=>{
                expect(dataService.setFileData.calledOnce).to.be.true;
                done();
            },()=>{
                done("badnews");
            }


        )

        $httpBackend.flush();




    });

    /**
     * @test {testVilleController#loadFileOrSample}
     */
    xit("should load sample data from file if no param specified",(done)=>{

        urlService.getUrl = ()=>{ return "file=schema.json"};

        $httpBackend
            .when("GET", "schema.json")
            .respond(200, "someData");

        dataService.setFileData = sinon.spy();

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        expect(dataService.setFileData.calledWithExactly("someData"));

    });

    /**
     * @test {testVilleController#loadFileOrSample}
     */
    xit("should update settings from url params if file was loaded from url param",()=>{

        urlService.getUrl = ()=>{ return "file=someFile&someSetting=true"};

        $httpBackend
            .when("GET", "someFile")
            .respond(200, "someData");

        settingsService.onSettingsChanged = sinon.spy();

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        expect(settingsService.onSettingsChanged.calledOnce).to.be.true;

    });

    /**
     * @test {testVilleController#loadFileOrSample}
     * @test {testVilleController#printErrors}
     */
    xit("should alert if no file can be loaded initially",()=>{

        urlService.getUrl = ()=>{ return "file=someFile"};

        $httpBackend
            .when("GET", "someFile")
            .respond(404, null);

        $httpBackend
            .when("GET", "sample.json")
            .respond(404, null);

        var a = window.alert;
        window.alert = sinon.spy();

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        expect(window.alert.calledWithExactly("failed loading sample data")).to.be.true;

        window.alert = a;

    });

    /**
     * @test {testVilleController#loadFileOrSample}
     * @test {testVilleController#printErrors}
     */
    xit("should print errors when data is not loaded correctly",()=>{

        urlService.getUrl = ()=>{ return "file=someFile"};

        $httpBackend
            .when("GET", "someFile")
            .respond(404, null);

        $httpBackend
            .when("GET", "sample.json")
            .respond(404, null);

        var o = console.log;
        console.log = sinon.spy();

        testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

        expect(console.log.calledOnce).to.be.true;

        console.log = o;
    });

});