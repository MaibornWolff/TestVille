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

        var before = threeViewerService.CameraService.camera.aspect;
        threeViewerService.RendererService.renderer.setSize = sinon.spy();
        threeViewerService.CameraService.camera.aspect = sinon.spy();
        threeViewerService.CameraService.camera.updateProjectionMatrix = sinon.spy();


        threeViewerService.onWindowResize();


        expect(threeViewerService.RendererService.renderer.setSize.calledOnce, "RendererService").to.be.true;
        expect(threeViewerService.CameraService.camera.aspect, "camera.aspect").to.not.equal(before);
        expect(threeViewerService.CameraService.camera.updateProjectionMatrix.calledOnce, "camera.updateProjectionMatrix").to.be.true;

    }));

});