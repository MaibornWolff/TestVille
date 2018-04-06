require("./testVille.js");


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

    describe("controller.inithandlers.test",()=>{
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
    });

    describe("controller.loadFile.tests",()=>{
        it("should load file from existing url file param",(done)=>{



            var path="./sample.json";

            $httpBackend
                .when("GET", path)
                .respond(200, "someData");

            dataService.setFileData= sinon.stub().returns(
                new Promise((resolve, reject) => {resolve();})
            );



            testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});


            testVilleController.loadFile(urlService, dataService, settingsService, path).then(
                ()=>{
                    expect(dataService.setFileData.calledTwice).to.be.true;
                    done();
                },()=>{
                    done("badnews");
                }


            )

            $httpBackend.flush();




        });

        it("should alert if no file can be loaded initially",(done)=>{


            var path = "someFile";


            $httpBackend
                .when("GET", path)
                .respond(404, null);

            $httpBackend
                .when("GET", "./sample.json")
                .respond(404, null);


            var a = window.alert;
            window.alert = sinon.spy();

            testVilleController = $controller("testVilleController", {dataService: dataService, urlService: urlService, settingsService:settingsService, scenarioService:scenarioService});

            testVilleController.loadFile(urlService, dataService, settingsService, path).then(
                ()=>{
                    done("bad news");

                },
                ()=>{
                    expect(window.alert.calledTwice);
                    done()
                }
            )


            window.alert = a;
            $httpBackend.flush();

        });
    });


});