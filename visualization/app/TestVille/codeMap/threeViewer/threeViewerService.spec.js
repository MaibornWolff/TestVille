require("./threeViewer.js");

/**
 * @test {ThreeViewerService}
 */
describe("app.testVille.codeMap.threeViewer.threeViewerService", function() {

    beforeEach(angular.mock.module("app.testVille"));

    it("should retrieve the angular service instance", angular.mock.inject(function(threeViewerService){
        expect(threeViewerService).to.not.equal(undefined);
    }));

    /**
     * @test {ThreeViewerService#onWindowResize}
     */
    it("camera and renderer should be adjusted when the window gets resized", angular.mock.inject(function(threeViewerService){

        //mocks
        threeViewerService.RendererService.renderer.setSize = sinon.spy();
        threeViewerService.CameraService.camera.aspect = sinon.spy();
        threeViewerService.CameraService.camera.updateProjectionMatrix = sinon.spy();

        //action
        threeViewerService.onWindowResize();

        //expectations
        expect(threeViewerService.RendererService.renderer.setSize.calledOnce);
        expect(threeViewerService.CameraService.camera.aspect.calledOnce);
        expect(threeViewerService.CameraService.camera.updateProjectionMatrix);

    }));

});