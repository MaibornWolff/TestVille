import {CodeMapUtilities} from "./codeMapUtilities";
import * as THREE          from "three";
import {Abstract3D} from "./dataTypes/Abstract3D";

/**
 * @test {CodeMapUtilities}
 */
describe("app.testVille.codeMap.codeMapUtilities", () => {

    let codeMapUtilities;

    beforeEach(() => {
        codeMapUtilities = new CodeMapUtilities();
    });

    it("should check the correctness of the jsonNode depth", () => {
       const node =  {children:
           [
               {children: []},
               {children:
                   [
                       {children: []}
                   ]
               }
           ]
       };

       const result = CodeMapUtilities.depthCheck(node, 2);
       expect(result).to.equals(true);
    });

    it("should compute an correct matrix to place a street on the right side of an mainStreet", () => {
        const depth          = Math.floor(200*Math.random());

        const result         = CodeMapUtilities.createMatrixToPlaceStreetRight(depth);
        const expectedResult = new THREE.Matrix4().makeTranslation(0, 0, -depth);

        expect(angular.equals(result, expectedResult)).to.equal(true);
    });

    it("should compute an correct matrix to place a street on the left side of an mainStreet",  () => {
        const depth = Math.floor(36*Math.random());
        const width = Math.floor(90*Math.random());

        const actual   = CodeMapUtilities.createMatrixToPlaceStreetLeft(depth, width);
        const expected = new THREE.Matrix4().makeRotationY(Math.PI);
        expected.premultiply(new THREE.Matrix4().makeTranslation(-width, 0, -depth));

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should compute an correct matrix to place a mainStreet under the finalStreet", () => {
       const length = Math.floor(1000*Math.random());
       const marge  = Math.floor(69*  Math.random());

       const actual   = CodeMapUtilities.createMatrixToPlaceMainStreetUnder(length, marge);
       const expected = new THREE.Matrix4().makeRotationY(Math.PI);
       expected.premultiply(new THREE.Matrix4().makeTranslation(length, 0 , marge));

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should compute an correct matrix to place a mainStreet over the finalStreet", () => {
        const length = Math.floor(1000*Math.random());

        const actual   = CodeMapUtilities.createMatrixToPlaceMainStreetOver(length);
        const expected = new THREE.Matrix4().makeTranslation(length, 0, 0);

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should compute an correct matrix to place a block under an given street", () => {
        const block       = {blockProperties: {blockWidth: Math.floor(Math.random()*152)}};
        const length      = Math.floor(5464*Math.random());
        const streetDepth = Math.floor(748*Math.random());

        const actual   = CodeMapUtilities.createMatrixToPlaceBlockUnderStreet(block, length, streetDepth);
        const expected = new THREE.Matrix4().makeRotationY(Math.PI);
        expected.premultiply(new THREE.Matrix4().makeTranslation(block.blockProperties.blockWidth + length, 0, streetDepth));

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should compute an correct matrix to place a block over an given street", () => {
        const length = Math.floor(7474*Math.random());

        const actual   = CodeMapUtilities.createMatrixToPlaceBlockOverStreet(length);
        const expected = new THREE.Matrix4().makeTranslation(length, 0, 0);

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    xit("should create an correct Abstract3D-Object",  () => {
        const id      = 673;
        const geoType = 2;

        const actual   = CodeMapUtilities.createAbstract3D(geoType, id, 0, 0, 0, 0, 0);
        const expected = new Abstract3D(geoType, id, 0, 0, 0, 0);

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should compute the right length for an given metric value",  () => {

        const actual   = CodeMapUtilities.metricValueToLength(12, 0.75, 4, 6);
        const expected = 58;

        expect(actual).to.equal(expected);
    });

    it("should compute an right scale matrix", () => {

        const actual   = CodeMapUtilities.generateScaleMatrix(new Abstract3D(0, -1, 90, 100, 0xffffff, 23));
        const expected = new THREE.Matrix4().makeScale(90, 100, 23);

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should extracts the leafs of the tree",  () => {
        const data = {
            "name": "root",
            "attributes": {},
            "children": [
                {
                    "name": "big leaf",
                    "attributes": {"rloc": 100, "functions": 10, "mcc": 1},
                    "link": "http://www.google.de",
                    "children": []
                },
                {
                    "name": "Parent Leaf",
                    "attributes": {},
                    "children": [
                        {
                            "name": "small leaf",
                            "attributes": {"rloc": 30, "functions": 100, "mcc": 100},
                            "children": []
                        },
                        {
                            "name": "other small leaf",
                            "attributes": {"rloc": 70, "functions": 1000, "mcc": 10},
                            "children": []
                        }
                    ]
                }
            ]
        };

        const actual  = [];
        CodeMapUtilities.extractLeafs(data, actual);
        const expected = [
            {
                "name": "big leaf",
                "attributes": {"rloc": 100, "functions": 10, "mcc": 1},
                "link": "http://www.google.de",
                "children": []
            }, {
                "name": "small leaf",
                "attributes": {"rloc": 30, "functions": 100, "mcc": 100},
                "children": []
            },
            {
                "name": "other small leaf",
                "attributes": {"rloc": 70, "functions": 1000, "mcc": 10},
                "children": []
            }
        ];

        expect(angular.equals(actual, expected)).to.equal(true);
    });

    it("should extract all element with an given depth", () => {

    });

    it("should compute the correct max depth of an given object",  () => {
        const data = {
            "name": "root",
            "attributes": {},
            "children": [
                {
                    "name": "big leaf",
                    "attributes": {"rloc": 100, "functions": 10, "mcc": 1},
                    "link": "http://www.google.de",
                    "children": []
                },
                {
                    "name": "Parent Leaf",
                    "attributes": {},
                    "children": [
                        {
                            "name": "small leaf",
                            "attributes": {"rloc": 30, "functions": 100, "mcc": 100},
                            "children": []
                        },
                        {
                            "name": "other small leaf",
                            "attributes": {"rloc": 70, "functions": 1000, "mcc": 10},
                            "children": []
                        }
                    ]
                }
            ]
        };

        const actual   = CodeMapUtilities.getObjectMaxDepth(data);
        const expected = 2;

        expect(actual).to.equal(expected);
    });

    it("should compute the normal for an given face", () => {
        const face = {a: 0, b: 1, c: 2};
        const vertices = [new THREE.Vector3(1, 0, 0), new THREE.Vector3(0, 1, 0), new THREE.Vector3(0, 0, 1)];

        const actual   = CodeMapUtilities.computeFaceNormal(face, vertices);
        const expected = new THREE.Vector3(1, 1, 1);

        expect(angular.equals(actual, expected)).to.equal(true);

    });

} );