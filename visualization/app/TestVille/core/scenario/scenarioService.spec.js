require("./scenario.js");
import {Range} from "../settings/model/range.js";
import {Settings} from "../settings/model/settings.js";
import {Scenario} from "./model/scenario.js";

/**
 * @test {ScenatioService}
 */
describe("app.testVille.core.scenarioService", function() {

    var scenarioService, $scope, sandbox, settingsService;

    beforeEach(angular.mock.module("app.testVille.core.scenario"));

    beforeEach(angular.mock.inject((_scenarioService_, _settingsService_, _$rootScope_)=>{
        scenarioService = _scenarioService_;
        settingsService = _settingsService_;
        $scope = _$rootScope_;
    }));

    beforeEach(()=>{
        sandbox = sinon.sandbox.create();
    });

    afterEach(()=>{
        sandbox.restore();
    });

    describe("Service.applyScenario.tests", ()=>{

        it("should apply the settings from a given scenario", ()=>{

            var range = new Range(1,2,false);
            var settings = new Settings("map", range, "areaMetric", "heightMetric", "colorMetric", false, false);
            var scenario = new Scenario("SomeName", settings);
            settingsService.applySettings = sinon.spy();

            scenarioService.applyScenario(scenario);

            sinon.assert.calledWith(settingsService.applySettings, sinon.match.same(settings));
        });

    });

    describe("Service.getScenarios.tests", ()=>{

        it("should return an array of scenarios", ()=>{
            var scenarios = scenarioService.getScenarios();
            scenarios.forEach((s)=>{
                expect(s.constructor == Scenario).to.be.true;
            });
        });

    });

    describe("Service.getDefaultScenario.test",()=>{

        it("default scenario should be rloc/mcc/mcc", ()=>{
            var scenario = scenarioService.getDefaultScenario();
            expect(scenario.settings.areaMetric).to.equal("rloc");
            expect(scenario.settings.heightMetric).to.equal("mcc");
            expect(scenario.settings.colorMetric).to.equal("mcc");
        });

    });
});