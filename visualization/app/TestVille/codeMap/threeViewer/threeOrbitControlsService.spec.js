require("./threeViewer.js");

import * as THREE from "three";
import * as Toc from "three-orbit-controls";

/**
 * @test {ThreeOrbitControlsService}
 */
describe("app.testVille.codeMap.threeViewer.threeOrbitControlsService", function() {

    let $rootScope;

    beforeEach(angular.mock.module("app.testVille.codeMap.threeViewer"));

    beforeEach(angular.mock.inject((_$rootScope_)=>{
        $rootScope= _$rootScope_;

    }));

    describe("Service.constructor.tests", ()=>{

        it("should retrieve the angular service instance", angular.mock.inject(function(threeOrbitControlsService){

            expect(threeOrbitControlsService).to.not.equal(undefined);
        }));
    });

    describe("Service.init.tests", ()=>{

        xit("", angular.mock.inject(function(threeOrbitControlsService){

            var cameraSpy= sinon.spy();
            threeOrbitControlsService.rootScope.$on("camera-changed", cameraSpy);

            var domElement = document.createElement("span");
            var cameraData = new THREE.Camera();
            threeOrbitControlsService.cameraService.camera = cameraData;

            //var event= new Event("change")
            //threeOrbitControlsService.rootScope.appendChild(domElement);


            threeOrbitControlsService.init(domElement);
            threeOrbitControlsService.controls.$broadcast("change");

            //domElement.dispatchEvent(event);



            //expect(threeOrbitControlsService.controls).to.be.true;
            //expect(threeOrbitControlsService.controls.domElement).to.not.equal(domElement);
            expect(cameraSpy.called).to.be.true;
        }));
    });


});