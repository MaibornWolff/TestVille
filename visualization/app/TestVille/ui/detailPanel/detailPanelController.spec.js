
require("./detailPanel.js");
/**
 * @test {DetailPanelController}
 */
describe("app.testVille.ui.detailPanel.detailPanelController", function() {

    var detailPanelController, scope, codeMapMaterialFactory, timeout, settingsService;


   beforeEach(()=>{



        angular.mock.module("app.testVille.ui.detailPanel");

    });

    beforeEach(()=>{

        angular.mock.module("app.testVille.codeMap");

        angular.module("app.testVille.codeMap").factory("codeMapMaterialFactory", () => {
            return {
                positive: () => {return new THREE.MeshLambertMaterial({color: 0x000000});},
                neutral: () => {return new THREE.MeshLambertMaterial({color: 0x111111});},
                negative: () => {return new THREE.MeshLambertMaterial({color: 0x222222});},
                odd: () => {return new THREE.MeshLambertMaterial({color: 0x333333});},
                even: () => {return new THREE.MeshLambertMaterial({color: 0x444444});},
                selected: () => {return new THREE.MeshLambertMaterial({color: 0x555555});},
                hovered: () => {return new THREE.MeshLambertMaterial({ color: 0x666666, emissive: 0x111111});},
                default: () => {return new THREE.MeshLambertMaterial({color: 0x777777});},
                positiveDelta: () => {return new THREE.MeshLambertMaterial({color: 0x888888});},
                negativeDelta: () => {return new THREE.MeshLambertMaterial({color: 0x999999});}
            }
        });

    });


    beforeEach(angular.mock.inject((_codeMapMaterialFactory_,_$timeout_, _settingsService_, _$rootScope_, $controller)=>{
        scope = _$rootScope_;
        codeMapMaterialFactory = _codeMapMaterialFactory_;
        settingsService = _settingsService_;
        timeout = _$timeout_;
        detailPanelController = $controller("detailPanelController", {$scope: scope, $rootScope: scope, codeMapMaterialFactory: codeMapMaterialFactory, settingsService:settingsService, $timeout: timeout});
    }));



    /**
     * @test {DetailPanelController}
     */
    describe("should react to events on its scope", ()=>{


        it("should accept the thrown event",(done)=>{
            detailPanelController.onSettingsChanged = (payload)=>{
                expect(payload).to.equal("payload");
                done();
            };
            scope.$broadcast("settings-changed", "payload");
        });



        it("should set common attributes when settings change",() => {
            var settings = {
                "areaMetric":"a",
                "colorMetric":"b",
                "heightMetric":"c"
            };
            detailPanelController.onSettingsChanged(settings);
            expect(detailPanelController.details.common.areaAttributeName).to.equal("a");
            expect(detailPanelController.details.common.colorAttributeName).to.equal("b");
            expect(detailPanelController.details.common.heightAttributeName).to.equal("c");
        });

    });

    describe("controller.onSelect.test", ()=>{


        it("should setSelectedDetails when valid node is selected",() => {
            var data = {
                "to": {
                    "node": "somenode"
                }
            };
            detailPanelController.setSelectedDetails = sinon.spy();
            detailPanelController.onSelect(data);
            expect(detailPanelController.setSelectedDetails.called).to.be.true;
        });




        it("should accept the thrown event",(done)=>{
            detailPanelController.onSelect = (payload)=>{
                expect(payload).to.equal("payload");
                done();
            };
            scope.$broadcast("building-selected", "payload");
        });


        it("should clearSelectedDetails when an invalid dataname is selected", ()=>{

            var data = {
                "from": null,
                "to": null
            };
            detailPanelController.clearSelectedDetails = sinon.spy();

            detailPanelController.onSelect(data);

            expect(detailPanelController.clearSelectedDetails.called).to.be.true;
            detailPanelController.restore;


        });


    })


    describe("controller.isSelected.tests", ()=>{


        it("should be false, when the details are empty",()=>{
            detailPanelController.details = {};
            expect(detailPanelController.isSelected()).to.be.false;
        });


        it("should be false, when the nodes are empty",()=>{
            detailPanelController.details = {
                hovered: null,
                selected: null
            };
            expect(detailPanelController.isSelected()).to.be.false;
        });


        it("should be true, when the nodes are named",()=>{
            detailPanelController.details = {
                hovered: {
                    name: "some name"
                },
                selected: {
                    name: "some name"
                }
            };
            expect(detailPanelController.isSelected()).to.be.true;
        });

    });




});