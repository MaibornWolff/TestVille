import {CodeMapUtilities} from "./codeMapUtilities";
import * as THREE from "three";
import {Abstract3D} from "./dataTypes/Abstract3D";
import {StaticJsonHandler} from "./dataTypes/StaticJsonHandler";

/**
 * @test {CodeMapUtilities}
 */
describe("app.testVille.codeMap.codeMapService", () => {

    let codeMapService, $scope, sandbox, data, utilities, factors;

    beforeEach(angular.mock.module("app.testVille.codeMap"));

    beforeEach(angular.mock.inject((_codeMapService_, _$rootScope_) => {
        codeMapService = _codeMapService_;
        utilities = new CodeMapUtilities();
        codeMapService.choosedMetrics = ["countTestSteps", "createdAtOrder", "developmentPeriod"];
        codeMapService.colorRange = {from: 130, to: 1200};

        factors = [0.1, 0.2, 0.5];

        $scope = _$rootScope_;
        data = {
            "projectName": "adapter",
            "apiVersion": "1.0",
            "date": "2017-09-28",
            "nodes":
                [
                    {
                        "name": "root",
                        "type": "root",
                        "attributes": {},
                        "children": [
                            {
                                "name": "1-Critical",
                                "id": "1-Critical267",
                                "type": "requirementCategory",
                                "attributes": {},
                                "children": [
                                    {
                                        "name": "Heckklappe von außen manuell öffnen",
                                        "type": "requirement",
                                        "id": "4",
                                        "attributes": {},
                                        "children": [
                                            {
                                                "name": "1-Critical",
                                                "id": "1-Critical257",
                                                "type": "TestCategory",
                                                "attributes": {},
                                                "children": [
                                                    {
                                                        "name": "Heckklappe lässt sich manuell öffnen || id: 22",
                                                        "type": "test",
                                                        "id": "3",
                                                        "attributes": {
                                                            "countTestSteps": 1,
                                                            "designStatus": 1,
                                                            "developmentPeriod": 2,
                                                            "createdAtOrder": 2,
                                                            "testCasePriority": 4
                                                        }
                                                    },
                                                    {
                                                        "name": "Öffnen der  Tür || id: 9191",
                                                        "type": "test",
                                                        "id": "95454",
                                                        "attributes": {
                                                            "countTestSteps": 9,
                                                            "designStatus": 2,
                                                            "developmentPeriod": 4,
                                                            "createdAtOrder": 1,
                                                            "testCasePriority": 2
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "2-High",
                                                "id": "2-High258",
                                                "type": "TestCategory",
                                                "attributes": {},
                                                "children": [
                                                    {
                                                        "name": "Negativtest || id: 88",
                                                        "type": "test",
                                                        "id": "21",
                                                        "attributes": {
                                                            "countTestSteps": 3,
                                                            "designStatus": 1,
                                                            "developmentPeriod": 1,
                                                            "createdAtOrder": 1,
                                                            "testCasePriority": 3
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "3-Medium",
                                                "id": "3-Medium259",
                                                "type": "TestCategory",
                                                "attributes": {},
                                                "children": [
                                                    {
                                                        "name": "01.02. Öffnen per Schlüssel || id: 23",
                                                        "type": "test",
                                                        "id": "7",
                                                        "attributes": {
                                                            "countTestSteps": 1,
                                                            "designStatus": 1,
                                                            "developmentPeriod": 2,
                                                            "createdAtOrder": 2,
                                                            "testCasePriority": 2
                                                        }
                                                    },
                                                    {
                                                        "name": "Öffnen der  Heckklappe || id: 94",
                                                        "type": "test",
                                                        "id": "91",
                                                        "attributes": {
                                                            "countTestSteps": 3,
                                                            "designStatus": 1,
                                                            "developmentPeriod": 1,
                                                            "createdAtOrder": 1,
                                                            "testCasePriority": 2
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                "name": "4-Low",
                                                "id": "4-Low260",
                                                "type": "TestCategory",
                                                "attributes": {},
                                                "children": [
                                                    {
                                                        "name": "Heckklappe öffnen per Kommando || id: 84",
                                                        "type": "test",
                                                        "id": "29",
                                                        "attributes": {
                                                            "countTestSteps": 3,
                                                            "designStatus": 1,
                                                            "developmentPeriod": 1,
                                                            "createdAtOrder": 1,
                                                            "testCasePriority": 1
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
        };




    }));

    beforeEach(() => {
        sandbox = sinon.sandbox.create();
    });

    it("should create an building as an Object3D-Object", () => {

        const leaf    = data.nodes[0].children[0].children[0].children[0].children[0];

        const columnW = 1 + (Math.random() * 120);
        const actual = codeMapService.createBuilding(leaf, factors, columnW);

        const bWidth  = columnW;//0.1*utilities.maxBuildingWidth + utilities.minBuildingWidth;
        const bDepth  = utilities.minBuildingDepth + (0.1 * utilities.maxBuildingArea / bWidth);
        const bHeight = utilities.minBuildingHeight + (2 * 0.2 * utilities.maxBuildingHeight);

        const abstract = new Abstract3D(0, "3", bWidth, bHeight, 0x00ff00, bDepth);
        const expected = new THREE.Object3D();

        abstract.initInformationToDisplay("test", "Heckklappe lässt sich manuell öffnen || id: 22", 1, 2, 2);
        abstract.transparent = false;
        expected.myContent = abstract;
        expect(angular.equals(actual.myContent, expected.myContent)).to.be.true;
    });

    it("it should create an column and Buildings with correct matrices",  () => {

        const leafs = data.nodes[0].children[0].children[0].children[0].children;
        let actual  = codeMapService.createColumn(leafs, 0xffffff, factors, 0, 2);

        let bWidth1  = utilities.minBuildingWidth + (factors [0] * utilities.maxBuildingWidth);
        let bDepth1  = utilities.minBuildingDepth + (factors [0] * utilities.maxBuildingArea / bWidth1);
        let bHeight1 = utilities.minBuildingHeight + (2 * factors [1] * utilities.maxBuildingHeight);

        let expected1 = new Abstract3D(0, "3", bWidth1, bHeight1, 0x00ff00, bDepth1);
        expected1.model = new THREE.Matrix4().makeTranslation((bWidth1 + utilities.distanceBetweenBuildings)/2,
            bHeight1/2,
            -(bDepth1/2) - utilities.distanceBetweenBuildings)
        ;
        expected1.transparent = false;
        expected1.initInformationToDisplay("test", "Heckklappe lässt sich manuell öffnen || id: 22", 1, 2, 2);


        let bDepth2  = utilities.minBuildingDepth  + (9 * factors[0] * utilities.maxBuildingArea / bWidth1);
        let bHeight2 = utilities.minBuildingHeight + (1 * factors[1] * utilities.maxBuildingHeight);

        let expected2 = new Abstract3D(0, "95454", bWidth1, bHeight2, 0x00ff00, bDepth2);
        expected2.model = new THREE.Matrix4().makeTranslation((bWidth1 + utilities.distanceBetweenBuildings)/2,
            bHeight2/2,
            -(bDepth2/2) - (2*utilities.distanceBetweenBuildings) - bDepth1)
        ;
        expected2.initInformationToDisplay("test", "Öffnen der  Tür || id: 9191", 9, 1, 4);
        expected2.transparent = false;

        const generalExpected = angular.equals(actual.children[0].myContent, expected1) &&
            angular.equals(actual.children[1].myContent, expected2)
        ;
        expect(generalExpected).to.be.true;
    });

    it("should create Block with correct columns(matrices)", () => {
        const leafs = data.nodes[0].children[0].children[0].children[2].children;
        codeMapService.metricSet = ["createdAtOrder", "developmentPeriod", "countTestSteps"];
        codeMapService.maxMetricValues = [2, 4, 9];

        // this block is generated from the code
        const block = codeMapService.createBlock(leafs, 0xffaabb);

        // We check now if the column in the block have an right matrix. it's not necessary to check to content of the
        // column then the correctness of the content has been already tested. see 2nd ,,it(...)'' in this file.
        const width1 = utilities.minBuildingWidth + ((1/9) * utilities.maxBuildingWidth) + utilities.distanceBetweenBuildings;
        let matrix = new THREE.Matrix4().makeTranslation(width1, 0, 0);

        let expected = angular.equals(block.children[0].matrix, new THREE.Matrix4()) // matrix of the first Column
            &&
            angular.equals(block.children[1].matrix, matrix) // matrix of the 2nd Column!
        ;

        expect(expected).to.be.true;
    });



});