import {SearchDirective} from "./searchDirective.js";

describe("app.testVille.ui.search.searchDirective", function() {

  var directive;

    beforeEach(()=>{
        directive = new SearchDirective();
    });

    describe("toggle test", function(){


        it("should toggle visibility", ()=>{

            directive.visible=false;

            directive.toggle();

            expect(directive.visible);

            directive.toggle();

            expect(!directive.visible);


        });




    })


})