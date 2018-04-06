import {TreeMapService} from "./treeMapService";

require("./treemap.js");

const createData = function(){
    const data = {
        "t": "root",
        "color": "transparent",
        "children": [
            {
                "t": "big leaf",
                "stuff": 10,
                "color": "red",
            },
            {
                "t": "01",
                "color": "transparent",
                "children": [
                    {
                        "t": "small leaf",
                        "color": "blue",
                        "stuff": 5
                    },
                    {
                        "t": "other small leaf",
                        "color": "purple",
                        "stuff": 5
                    }
                ]
            }
        ]
    };
    return data;
};

/**
 * @test {TreeMapService}
 */
describe("app.testVille.core.treemap.treemapService", function() {

    var treeMapService, data;

    beforeEach(angular.mock.module("app.testVille.core.treemap"));
    beforeEach(()=>{data = createData();});
    beforeEach(angular.mock.inject(function (_treeMapService_) {treeMapService = _treeMapService_;}));

    describe("service.constructor.tests",()=>{

        it("should retrieve instance", ()=>{
            expect(treeMapService).to.not.equal(undefined);
        });
    });


    describe("service.createTreemapNodes(data, w, l, p, areaKey, heightKey).tests", function() {

        it("should filter invisible nodes", ()=> {

            var data = {
                name:"root",
                children: [
                    {
                        name:"leaf1",
                        children: [],
                        attributes: {"Statements": 0, "Functions": 0, "Average Complexity*": 100}
                    },
                    {
                        name:"leaf2",
                        children: [],
                        attributes: {"Statements": 0, "Functions": 0, "Average Complexity*": 100}
                    },
                    {
                        name:"parent",
                        children: [
                            {
                                name:"leaf3",
                                children: [],
                                attributes: {"Statements": 0, "Functions": 0, "Average Complexity*": 100}
                            }
                        ]
                    }
                ]
            };


            var result = treeMapService.createTreemapNodes(data, 100, 100, 1, "Functions", "Statements");


            expect(result).to.be.empty;

        });

        it("should mark leaf notes with isLeaf = true", ()=> {

            var data = {
                name:"root",
                children: [
                    {
                        name:"leaf1",
                        children: [],
                        attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100}
                    },
                    {
                        name:"leaf2",
                        children: [],
                        attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100}
                    },
                    {
                        name:"parent",
                        children: [
                            {
                                name:"leaf3",
                                children: [],
                                attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100}
                            }
                        ]
                    }
                ]
            };

            var result = treeMapService.createTreemapNodes(data, 100, 100, 1, "Functions", "Statements");

            result.forEach((node)=>{

                switch(node.name) {
                    case "parent":
                    case "root":
                        expect(node.isLeaf).to.be.false;
                        break;
                    default:
                        expect(node.isLeaf).to.be.true;
                }

            });

        });

        /* the dataValidatorService filters invalid Data, so we don't need to test with invalid data*/



    });

    describe("Service.transformNode(node, heightKey, p).tests", ()=>{

        it("should transform the Node correct when heightKey is valid, it is a leaf node an p is positive", ()=> {

            let node = {
                data: {
                    name: "some name",
                    attributes: {"somekey":20},
                    deltas: {"somedeltakey": -2},
                    link: "www.some-page.something"
                },
                x1: 10,
                x0: 5,
                y1: 15,
                y0: 5,
                isLeaf: true,
                depth: 2
            };

            let heightScale = 0.5;

            treeMapService.transformNode(node, "somekey", 15, heightScale);

            // expect measures
            expect(node.width).to.equal(5);
            expect(node.height).to.equal(10);
            expect(node.length).to.equal(10);

            // expect new z values
            expect(node.z0).to.equal(30);
            expect(node.z1).to.equal(40);

            // expect node.data properties to be pushed to node
            expect(node.attributes["somekey"]).to.equal(20);
            expect(node.name).to.equal("some name");
            expect(node.deltas["somedeltakey"]).to.equal(-2);
            expect(node.link).to.equal("www.some-page.something");

            // expect node.data to be deleted
            expect(node.data).to.be.undefined;
        });


        it("valid heightKey, is not a leaf node, p positive", ()=> {

            let node = {
                data: {
                    name: "some name",
                    attributes: {"somekey":20},
                    deltas: {"somedeltakey": -42},
                    link: "www.some-page.something"
                },
                x1: 10,
                x0: 5,
                y1: 15,
                y0: 5,
                isLeaf: false,
                depth: 2
            };


            treeMapService.transformNode(node, "somekey", 15, 1);


            expect(node.height).to.equal(15)

        });


        /* it is impossible for p to be negative and it is impossible for the heightkey to be invalid
        * so we don't need to test it  */

    });

    describe("Service.getMaxNodeHeightInAllRevisions.tests",()=>{

        it("", ()=>{

            treeMapService.dataService.data.revisions = [{
                name: "root",
                attributes:{},
                children:[
                    {
                        "name": "small leaf",
                        attributes: {"Statements": 30, "Functions": 100, "Average Complexity*": 100},
                        children: []
                    },
                    {
                        "name": "other small leaf",
                        attributes: {"Statements": 70, "Functions": 1000, "Average Complexity*": 10},
                        children: []
                    },
                    {
                        "name": "third small leaf",
                        attributes: {"Statements": 40, "Functions": 10, "Average Complexity*": 15},
                        children: []
                    },                    {
                        "name": "lasr small leaf",
                        attributes: {"Statements": 90, "Functions": 1, "Average Complexity*": 7},
                        children: []
                    },
                ]
            }]


            var statementsMaximum = treeMapService.getMaxNodeHeightInAllRevisions("Statements");
            var functionsMaximum= treeMapService.getMaxNodeHeightInAllRevisions("Functions");
            var averageComplexityMaximum= treeMapService.getMaxNodeHeightInAllRevisions("Average Complexity*");


            expect(statementsMaximum).to.equal(90);
            expect(functionsMaximum).to.equal(1000);
            expect(averageComplexityMaximum).to.equal(100);

        });
    });


    describe("Service.getArea(node, areaKey).tests", function() {

        it("should extract area by areaKey from node with no attributes", ()=> {

            let node = {};


            let area = treeMapService.getArea(node, "some key");


            expect(area).to.equal(0);
        });

        it("should return 0 when the node has empty attributes", ()=> {

            let node = {attributes: {}};


            let area = treeMapService.getArea(node, "some key");


            expect(area).to.equal(0);

        });

        it("should return 0 when the attributes and children array are empty", ()=> {

            let node = {attributes: {}, children:[]};


            let area = treeMapService.getArea(node, "some key");


            expect(area).to.equal(0);
        });

        it("should return node with matching attribute and empty children array", ()=> {

            let node = {attributes: {"somekey":"somevalue"}, children:[]};


            let area = treeMapService.getArea(node, "somekey");


            expect(area).to.equal("somevalue");
        });

        it("should only return the value of the matching attribute when using a node with other attributes and empty children array", ()=> {

            let node = {attributes: {"somekey":"somevalue", "somekey2":"somevalue2"}, children:[]};


            let area = treeMapService.getArea(node, "somekey");


            expect(area).to.equal("somevalue");
        });

        it("should return the value of the matching attribute when using a node with matching attribute and no children array", ()=> {

            let node = {attributes: {"somekey":"somevalue"}};


            let area = treeMapService.getArea(node, "somekey");


            expect(area).to.equal("somevalue");
        });

    });




});

