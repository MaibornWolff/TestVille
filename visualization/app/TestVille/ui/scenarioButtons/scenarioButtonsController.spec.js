require("./scenarioButtonsController.js");

/**
 * @test
 */
describe("app.testVille.ui.scenarioButtons.scenarioButtonsController", function() {

    var scenarioButtonsController,scenarioService, tooltipService, $rootScope,$scope,$controller ;

    beforeEach(angular.mock.module("app.testVille"));

    beforeEach(angular.mock.inject((_scenarioService_, _tooltipService_, _$rootScope_,_$controller_)=>{
        tooltipService = _tooltipService_;
        scenarioService = _scenarioService_;
        $rootScope = _$rootScope_;
        $scope = _$rootScope_;
        $controller = _$controller_;
        scenarioButtonsController = $controller("scenarioButtonsController", {scenarioService:scenarioService, tooltipService:tooltipService, $rootScope:$rootScope, $scope:$scope});

    }));

    describe("getScenarioTooltipTextByKey tests", function() {

        it("should notify tooltip when getScenarioTooltipTextByKey is called", ()=>{

            tooltipService.getTooltipTextByKey = sinon.spy();

            scenarioButtonsController.getScenarioTooltipTextByKey();

            expect(tooltipService.getTooltipTextByKey.calledOnce ).to.be.true;

        });
    });

    describe("onclick tests", function() {

        it("should call ScenarioService.applyScenario", ()=>{

            scenarioService.applyScenario = sinon.spy();

            scenarioButtonsController.onclick();

            expect(scenarioService.applyScenario.calledOnce).to.be.true;


        });
    });
});