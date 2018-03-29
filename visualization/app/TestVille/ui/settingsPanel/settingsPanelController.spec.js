import {SettingsPanelController} from "./settingsPanelController";

/**
 * @test {SettingsPanelController}
 */
describe("app.testVille.ui.settingsPanel.settingsPanelController.Test", ()=>{

    let settingsPanelController, settingsService, dataService, scope;

    beforeEach(()=>{

        //mock module under test
        angular.mock.module("app.testVille.ui.settingsPanel");

        //build a module dependent on the module under test and the specific controller under test
        angular.module("sut", ["app.testVille.ui.settingsPanel"])
            .controller("settingsPanelController", SettingsPanelController);

        //mock it
        angular.mock.module("sut");

    });

    beforeEach(angular.mock.inject((_settingsService_, _dataService_, _$rootScope_, $controller)=>{
        settingsService = _settingsService_;
        dataService = _dataService_;
        scope = _$rootScope_;
        settingsPanelController = $controller("settingsPanelController", {$scope: scope, settingsService: settingsService, dataService: dataService});
    }));

    describe("Constructor tests", ()=>{

        it("should sort array alphabetically on start", angular.mock.inject(($controller)=>{

            settingsService.settings.map = {name:"aMap"};
            dataService.data.metrics[0] = "z";
            dataService.data.metrics[1] = "g";
            dataService.data.metrics[2] = "a";

            settingsPanelController = $controller("settingsPanelController", {$scope: scope, settingsService: settingsService, dataService: dataService});

            expect(settingsPanelController.metrics[0]).to.equal("a");
            expect(settingsPanelController.metrics[1]).to.equal("g");
            expect(settingsPanelController.metrics[2]).to.equal("z");

        }));


        it("should have correct values in scope", ()=>{

            settingsService.settings.map = {name:"specialMap"};
            dataService.data.metrics[0] = "specialMetric";

            expect(settingsPanelController.settings.map.name).to.equal("specialMap");
            expect(settingsPanelController.metrics[0]).to.equal("specialMetric");
            expect(settingsPanelController.settingsService).to.equal(settingsService);

        });


        it("should refresh metrics from data-changed event", ()=>{

            settingsService.onDataChanged = () => {};

            scope.$broadcast("data-changed", {currentmap: {}, metrics: ["a","b","c"]});

            expect(settingsPanelController.metrics[0]).to.equal("a");
            expect(settingsPanelController.metrics[1]).to.equal("b");
            expect(settingsPanelController.metrics[2]).to.equal("c");

        });
    });

    describe("notify tests", ()=>{

        it("should notify settingsService when notify() is called", ()=>{

            settingsService.onSettingsChanged = sinon.spy();


            settingsPanelController.notify();


            expect(settingsService.onSettingsChanged.calledOnce).to.be.true;

        });
    });

    describe("showUrlParams tests", ()=>{

        it("should prompt the user when showUrlParams() is called", ()=>{

            const tmp = window.prompt;
            window.prompt = sinon.spy();


            settingsPanelController.showUrlParams();


            expect(window.prompt.calledOnce).to.be.true;
            window.prompt = tmp;

        });
    });

    describe("sortStringArrayAlphabetically", ()=>{

        it("should sort array alphabetically", ()=>{

            const arr = ["b", "g", "a", "z", "y"];


            const result = settingsPanelController.sortStringArrayAlphabetically(arr);


            expect(result).to.deep.equal(["a", "b", "g", "y", "z"]);
        });

        it("should not sort a sorted array", ()=>{

            const arr = ["a", "b", "c", "c", "c"];


            const result = settingsPanelController.sortStringArrayAlphabetically(arr);


            expect(result).to.deep.equal(["a", "b", "c", "c", "c"]);
        });

        it("should not throw errors", ()=>{

            const arr = ["a", ":", ",", "c", "/"];


            const result = settingsPanelController.sortStringArrayAlphabetically(arr);


            expect(result).to.deep.equal([",", "/", ":", "a", "c"]);
        });




    });


});