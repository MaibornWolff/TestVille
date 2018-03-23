require("./dropdown.js");
/**
 * @test {DropdownController}
 */
describe("app.testVille.ui.common.dropdown.dropdownController", function () {

    var dropdownController, tooltipService, scope, sandbox;

    beforeEach(angular.mock.module("app.testVille.ui.common.dropdown"));

    beforeEach(angular.mock.inject((_$rootScope_, $controller, _tooltipService_) => {
        scope = _$rootScope_;
        tooltipService = _tooltipService_;
        dropdownController = $controller("dropdownController", {
            tooltipService: tooltipService,
            $rootScope: scope,
            $scope: scope
        });
    }));

    beforeEach(() => {
        sandbox = sinon.sandbox.create();
    });

    afterEach(() => {
        sandbox.restore();
    });


    describe("controller.getTooltiptextByKey.test", ()=>{


        it("expect tooltipService#getTooltipTextByKey called with the given key", () => {
            tooltipService.getTooltipTextByKey = sinon.spy();
            dropdownController.getTooltipTextByKey("SomeKey");
            sinon.assert.calledWith(tooltipService.getTooltipTextByKey, sinon.match("SomeKey"));
        });

    });


});