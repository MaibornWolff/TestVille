require("./threeViewer.js");

/**
 * @test {ThreeOrbitControlsService}
 */
describe("app.testVille.codeMap.threeViewer.threeOrbitControlsService", function() {

    beforeEach(angular.mock.module("app.testVille.codeMap.threeViewer"));
    
    it("should retrieve the angular service instance", angular.mock.inject(function(threeOrbitControlsService){
        expect(threeOrbitControlsService).to.not.equal(undefined);
    }));

});