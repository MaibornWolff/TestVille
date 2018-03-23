import {RevisionChooserDirective} from "./revisionChooserDirective.js";

/**
 * @test {RevisionChooserDirective}
 */
describe("app.testVille.ui.revisionChooser.revisionChooserDirective", function() {

    var directive;

    beforeEach(()=>{
        directive = new RevisionChooserDirective();
    });

    describe("toggle tests", function() {

        it("should toggle visibility", ()=>{
            directive.visible = false;

            directive.toggle();

            expect(directive.visible).to.be.true;

            directive.toggle();

            expect(directive.visible).to.be.false;
        });
    });

});
