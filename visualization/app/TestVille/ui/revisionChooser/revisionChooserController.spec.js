require("./revisionChooser.js");

/**
 * @test {RevisionChooserController}
 */
describe("app.testVille.ui.revisionChooser.revisionChooserController", function() {

    var revisionChooserController, dataService, scope;

    beforeEach(angular.mock.module("app.testVille.ui.revisionChooser"));

    beforeEach(angular.mock.inject((_dataService_, _$rootScope_, $controller)=>{
        dataService = _dataService_;
        scope = _$rootScope_;
        dataService.revisions = ["a", "b", "c"];
        revisionChooserController = $controller("revisionChooserController", {$scope: scope, dataService: dataService});
    }));

    describe("Constructor tests", function() {

        it("should have correct values in scope", ()=>{


            expect(revisionChooserController.revisions[0]).to.equal("a");
            expect(revisionChooserController.dataService).to.equal(dataService);

        });


        it("should refresh revisions from data-changed event", ()=>{

            scope.$broadcast("data-changed", {map: {}, revisions: ["a","b","c"]});

            expect(revisionChooserController.revisions[0]).to.equal("a");

        });
    })

    describe("loadRevision tests", function() {

        it("should notify dataService when loadRevision is called", ()=>{

            dataService.setCurrentMapFromRevisions = sinon.spy();

            revisionChooserController.loadRevision();

            expect(dataService.setCurrentMapFromRevisions.calledOnce).to.be.true;

        });


    });

});