require("./threeViewer.js");

import * as THREE from "three";
/**
 * @test {ThreeCameraService}
 */
describe("app.testVille.codeMap.threeViewer.threeCameraService", function() {

    beforeEach(angular.mock.module("app.testVille.codeMap.threeViewer"));

    it("should retrieve the angular service instance", angular.mock.inject(function(threeCameraService){
        expect(threeCameraService).to.not.equal(undefined);
    }));

    describe("Service.init.tests",()=>{
        it("init should create a new PerspectiveCamera", angular.mock.inject(function(threeCameraService){

            //mocks
            let spy = sinon.spy(THREE, "PerspectiveCamera");

            //action
            threeCameraService.init();

            //expectations
            expect(spy.calledOnce).to.be.true;

        }));

        describe("Service.setPosition.tests", ()=>{
            it("init should set the camera position", angular.mock.inject(function(threeCameraService, settingsService){

                //action
                threeCameraService.init();

                //expectations
                expect(threeCameraService.camera.position).to.not.equal(undefined);

            }));


            it("the near plane of the viewing frustum should be at least 100 to prevent flickering of planes", angular.mock.inject(function(threeCameraService){

                var containerWidth= 100;
                var containerHeight= 20;
                var x= 1;
                var y= 2;
                var z= 0;


                threeCameraService.init(containerWidth, containerHeight, x, y, z);


                expect(threeCameraService.camera.aspect).to.equal(5);
                expect(threeCameraService.camera.position).to.deep.equal(new THREE.Vector3(1,2,0));

            }));
        });
    });
});