require("./search.js");



describe("app.testVille.ui.search.searchController", ()=>{

    let controller, settingsService, dataService, codeMapService, $rootScope;



    beforeEach(angular.mock.module("app.testVille.ui.search"));

    beforeEach(angular.mock.inject((_dataService_, _codeMapService_, _settingsService_, _$rootScope_, $controller)=>{

        dataService= _dataService_;
        codeMapService= _codeMapService_;
        settingsService= _settingsService_;
        $rootScope= _$rootScope_;

        controller= $controller("searchController", {dataService: dataService, codeMapService: codeMapService, settingsService: settingsService, $rootScope: $rootScope});
        controller.codeMapService.searchList = ["Heckklappe lässt sich manuell öffnen || id: 22",
                                                           "01.02. Öffnen per Schlüssel || id: 23",
                                                           "Öffnen der  Heckklappe || id: 94",
                                                           "Heckklappe öffnen per Kommando || id: 84",
                                                           "Korrekte Anzeige und Berechnung des Steuersatzes || id: 70"];




    }))


    describe("controller.keyPressed.tests", ()=>{


        it(" it should just filter the list, when not using the alt key or space", ()=>{


            var onSearchSpy = sinon.spy();
            controller.$rootScope.$on("onsearch", onSearchSpy);
            var offSearchSpy = sinon.spy();
            controller.$rootScope.$on("offsearch", offSearchSpy);




            controller.keyPressed(64 , "he");



            expect(controller.resultsList[0]).to.equal("Heckklappe lässt sich manuell öffnen || id: 22");
            expect(controller.resultsList).to.have.length(3);
            expect(onSearchSpy.calledOnce).to.be.false;
            expect(offSearchSpy.calledOnce).to.be.false;



        })

        it("should throw just the offsearch-event, when using space, while the input equals just one object in the list", ()=>{


            var onSearchSpy = sinon.spy();
            controller.$rootScope.$on("onsearch", onSearchSpy);
            var offSearchSpy = sinon.spy();
            controller.$rootScope.$on("offsearch", offSearchSpy);



            controller.keyPressed(32 , "Heckklappe lässt sich manuell öffnen || id: 22");



            expect(controller.resultsList[0]).to.equal("Heckklappe lässt sich manuell öffnen || id: 22");
            expect(controller.resultsList).to.have.length(1);
            expect(controller.myStyle).to.deep.equal({width: `${225}px`});
            expect(controller.input).to.equals("");
            expect(offSearchSpy.called).to.be.true;
            expect(onSearchSpy.called).to.be.false;







        })

        it("should throw only the onsearch-event, when the input equals just one object in the list, and space is not pressed", ()=>{


            var onSearchSpy = sinon.spy();
            controller.$rootScope.$on("onsearch", onSearchSpy);
            var offSearchSpy = sinon.spy();
            controller.$rootScope.$on("offsearch", offSearchSpy);




            controller.keyPressed(77 , "Heckklappe lässt sich manuell öffnen || id: 22");



            expect(controller.resultsList[0]).to.equal("Heckklappe lässt sich manuell öffnen || id: 22");
            expect(controller.resultsList).to.have.length(1);
            expect(onSearchSpy.called).to.be.true;
            expect(offSearchSpy.called).to.be.false;



        })


        it("should just increase the upperLimit, when the alt key is pressed",()=>{

            var resentInput= controller.input;
            var onSearchSpy = sinon.spy();
            controller.$rootScope.$on("onsearch", onSearchSpy);
            var offSearchSpy = sinon.spy();
            controller.$rootScope.$on("offsearch", offSearchSpy);


            controller.keyPressed(18 , "Heckklappe lässt sich manuell");



            expect(controller.input).to.equal(resentInput);
            expect(controller.upperLimit).to.equal(50);
            expect(onSearchSpy.called).to.be.false;
            expect(offSearchSpy.called).to.be.false;



        })
    })




    describe("controller.changingInput.tests", ()=>{


        it("should reset the width to {} and the upperLimit to 25, when using small input", ()=>{

            var container= document.createElement("span");
            var containertext= document.createTextNode("Testing");
            container.appendChild(containertext);
            controller.container= container;
            //controller.input= "";
            controller.upperLimit= 500;


            controller.changingInput();


            expect(controller.myStyle).to.deep.equal({width: `${225}px`});
            expect(controller.upperLimit).to.equal(25);


        })


    })
})