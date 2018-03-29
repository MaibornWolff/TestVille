require("./fileChooser.js");

/**
 * @test {FileChooserController}
 * note: more tests with protractor(e2e)
 */
describe("app.testVille.ui.fileChooser.fileChooserController", function() {

    var dataService, fileChooserController, $controller, $httpBackend, $rootScope, originalReader;

    beforeEach(angular.mock.module("app.testVille.ui.fileChooser"));

    beforeEach(angular.mock.inject((_$controller_, _dataService_, _$httpBackend_, _$rootScope_)=>{
        dataService = _dataService_;
        $rootScope = _$rootScope_;
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        fileChooserController = $controller("fileChooserController", {$scope: $rootScope, dataService: dataService});


        //bind filereader
        global.FileReader = FileReader;

    }));

    describe("controller.printErrors.tests", function() {

        it("printing errors should call console.log", ()=>{
            var o = console.log;
            console.log = sinon.spy();

            fileChooserController = $controller("fileChooserController", {$scope: $rootScope, dataService: dataService});
            fileChooserController.printErrors({errors:[{message:"a", dataPath:"b"}]});

            expect(console.log.calledOnce).to.be.true;
            console.log = o;

        });


    });

});