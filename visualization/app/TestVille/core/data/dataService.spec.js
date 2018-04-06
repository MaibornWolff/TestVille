require("./data.js");

/**
 * @test {DataService}
 */
describe("app.testVille.core.data.dataService", function() {

    var rootNode;
    var firstFile;
    var twoRevFile;
    var dataService;
    var $httpBackend;

    beforeEach(angular.mock.module("app.testVille.core.data"));
    beforeEach(angular.mock.inject(function (_dataService_, _$httpBackend_) {
        dataService = _dataService_;
        $httpBackend = _$httpBackend_;

    }));

    beforeEach(function(){

        rootNode = {
            children: [],
            attributes: {}
        };

        firstFile = {
            children: [],
            attributes: {anAttribute: "value"}
        };

        twoRevFile = {
            "revisions": [
                //First Revision
                {
                    "name": "root",
                    attributes: {},
                    "children": [
                        {
                            "name": "big leaf",
                            attributes: {"Statements": 100, "Functions": 10, "Average Complexity*": 1},
                            "link": "http://www.google.de"
                        },
                        {
                            "name": "Parent Leaf",
                            attributes: {},
                            "children": [
                                {
                                    "name": "small leaf",
                                    attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100},
                                    children: []
                                },
                                {
                                    "name": "other small leaf",
                                    attributes: {"Statements": 70, "Functions": 1000, "Average Complexity*": 10},
                                    children: []
                                }
                            ]
                        }
                    ]
                },

                //Second Revision
                {
                    "name": "root",
                    attributes: {},
                    "children": [
                        {
                            "name": "big leaf",
                            attributes: {"Statements": 90, "Functions": 11, "Average Complexity*": 1},
                            "link": "http://www.google.de"
                        },
                        {
                            "name": "Parent Leaf",
                            attributes: {},
                            "children": [
                                {
                                    "name": "small leaf",
                                    attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100},
                                    children: []
                                },
                                {
                                    "name": "other small leaf",
                                    attributes: {"Statements": 70, "Functions": 1000, "Average Complexity*": 10},
                                    children: []
                                }
                            ]
                        }
                    ]
                }
            ]
        };

    });


    describe("Service.constructor.test",()=>{

        it("metrics should be empty when no file is loaded", () => {

            let sut = dataService;


            expect(sut.data.metrics.length).to.equal(0);

        });

        it("should retrieve instance", ()=>{
            expect(dataService).to.not.equal(undefined);
        });


    });


    describe("Service.setFileData.tests", ()=>{

        it("should find all metrics, even in child nodes", (done) => {

            let sut = dataService;
            rootNode.children.push(firstFile);
            sut.validator.validate = sinon.stub().returns(
                new Promise((resolve)=>{ resolve("validData")})
            );


            sut.setFileData(rootNode).then(
                ()=>{
                    expect(sut.data.metrics.length).to.equal(1);
                    done();
                },
                ()=>{
                    done("failure");
                }
            );

        });

        it("there should be two revisions when json has two revisions", (done) => {

            let sut = dataService;

            $httpBackend
                .when("GET", "./schema.json")
                .respond(200, {});
            dataService.validator.uniqueName = sinon.stub().returns(true);


            sut.setFileData(twoRevFile).then(
                ()=>{

                    expect(sut.data.revisions.length).to.equal(2);

                    done();

                }, ()=>{

                    done("should not happen");

                }
            );

            $httpBackend.flush();


        });

    });

    describe("Service.setMetrics.tests", (done)=>{

        it("should change the metric", ()=>{

            var beforeMetrics= dataService.data.metrics;

            $httpBackend
                .when("GET", "./schema.json")
                .respond(200, {});
            dataService.validator.uniqueName = sinon.stub().returns(true);



            dataService.setFileData(twoRevFile).then(
                ()=>{
                    dataService.setMetrics(0);

                    expect(dataService.data.metrics).to.not.deep.equal(beforeMetrics);

                    done();

                }, ()=>{

                    done("should not happen");

                }
            );

            $httpBackend.flush();

        });

    });

    describe("Service.setCurrentMapFromRevisions.tests", ()=>{

        it("should change the currentmap", ()=>{

            dataService.setFileData =sinon.stub();
            dataService.data.revisions= [{}];

            dataService.setCurrentMapFromRevisions(0);

            expect(dataService.data.currentmap).to.deep.equal({});

        })
    });

    describe("Service.calculateAttributeListDelte.test", ()=>{



        it("checking delta calculation between two attribute lists", () => {

            let a = {"a":100,"b":10,"c":1};
            let b = {"a":110,"b":11,"c":0};
            let c = {"a":110,"b":11,"c":0, "d":10};
            let d = {"a":110,"b":11};
            let e = {"d":110,"e":11};


            let sut = dataService;


            let ab = sut.calculateAttributeListDelta(a,b);
            expect(ab.a).to.equal(b.a-a.a);
            expect(ab.b).to.equal(b.b-a.b);
            expect(ab.c).to.equal(b.c-a.c);

            let ac = sut.calculateAttributeListDelta(a,c);
            expect(ac.a).to.equal(c.a-a.a);
            expect(ac.b).to.equal(c.b-a.b);
            expect(ac.c).to.equal(c.c-a.c);
            expect(ac.d).to.equal(c.d);

            let ad = sut.calculateAttributeListDelta(a,d);
            expect(ad.a).to.equal(d.a-a.a);
            expect(ad.b).to.equal(d.b-a.b);
            expect(ad.c).to.equal(undefined);

            let ae = sut.calculateAttributeListDelta(a,e);
            expect(ae.a).to.equal(undefined);
            expect(ae.b).to.equal(undefined);
            expect(ae.c).to.equal(undefined);
            expect(ae.d).to.equal(e.d);
            expect(ae.e).to.equal(e.e);

        });

    });

});

