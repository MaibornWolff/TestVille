require("./data.js");

import * as Ajv from "ajv";

/**
 * @test {DataValidatorService}
 */
describe("app.testVille.core.data.dataValidatorService", function() {

    var dataValidatorService, $httpBackend;

    beforeEach(angular.mock.module("app.testVille.core.data"));
    beforeEach(angular.mock.inject(function (_dataValidatorService_, _$httpBackend_) {
        dataValidatorService = _dataValidatorService_;
        $httpBackend = _$httpBackend_;


        //bind ressource to httpBackend
        $httpBackend
            .when("GET", "./schema.json")
            .respond(200, {});
    }));

    /**
     * @test {DataValidatorService#validate}
     * @test {DataValidatorService#uniqueName}
     * @test {DataValidatorService#uniqueArray}
     */
    describe("Service.validate.tests", ()=> {

        /**
         * @test {DataValidatorService#validate}
         * @test {DataValidatorService#uniqueName}
         * @test {DataValidatorService#uniqueArray}
         */
        it("should validate the data", function (done) {


            let correct = {
                "revisions": [
                    {
                        "name": "some name"
                    }
                ],

            };
            dataValidatorService.uniqueName = sinon.stub().returns(true);


            dataValidatorService.validate(correct).then(
                (result) => {

                    expect(result).to.not.be.undefined;
                    expect(result.valid).to.be.true;
                    done();

                }, () => {
                    done("failed");
                }
            ).catch((e) => {
                done(err);
            });

            $httpBackend.flush();

        });


        it("should not validate the data, when uniqueName is wrong", (done) => {


            let wrong = {
                "notNodes": {}
            };
            dataValidatorService.uniqueName = sinon.stub().returns(false);


            dataValidatorService.validate(wrong).then(
                (result) => {


                    done("failed");

                }, () => {
                    expect(result).to.not.be.undefined;
                    expect(result.valid).to.be.false;
                    done();
                }
            ).catch(() => {
                done();
            });

            $httpBackend.flush();


        });

        describe("also tests Service.uniqueName and Service.uniqueArray", (done) => {
            it("should validate the data when the data is correct", () => {

                //has valid children
                let correct = {
                    "nodes": [
                        {
                            "name": "I am a parent",
                            "children": [
                                {
                                    "name": "I am a child"
                                }
                            ]
                        }
                    ]
                };


                dataValidatorService.validate(correct).then(
                    (result) => {

                        expect(result).to.not.be.undefined;
                        expect(result.valid).to.be.true;
                        done();

                    }, () => {
                        done("failed");
                    }
                ).catch((e) => {
                    done(err);
                });


                $httpBackend.flush();


            });

            xit("muss not validate the data when the children is initilized wrong", (done) => {

                let wrong = {
                    "nodes": [
                        {
                            "name": "I am a parent",
                            "children": [
                                {
                                    "notMyName": "Wont tell"
                                }
                            ]
                        }
                    ]
                };

                dataValidatorService.validate(wrong).then(
                    (result) => {


                        done("failed");

                    }, () => {
                        expect(result).to.not.be.undefined;
                        expect(result.valid).to.be.false;
                        done();
                    }
                ).catch(() => {
                    done();
                });

                $httpBackend.flush();
            });
        });
    });


    describe("Service.uniqueName.tests", ()=>{

        it("should be true when nodes with same parent have different names",()=>{

            let correct = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            },
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            }
                        ]
                    }
                ]
            };


            expect(dataValidatorService.uniqueName(correct)).to.be.true;
        });

        xit("should be false when nodes with same parent have the same name", ()=> {



            let wrong = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            },
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            }
                        ]
                    }
                ]
            };


            var result = dataValidatorService.uniqueName(wrong);
            expect(result).to.be.false;

        });


        xit("nodes in different level should be allowed to have the same name", ()=> {
            let different = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_1_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    },
                                    {
                                        "name":"node_1_1",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
            let same = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_1_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    },
                                    {
                                        "name":"node_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };


            expect(dataValidatorService.uniqueName(same)).to.be.false;
            expect(dataValidatorService.uniqueName(different)).to.be.true;


        });


        it("nodes in the same level with different parents should be allow to have the same name", ()=>{
            let different = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_0_0",
                                        "attributes": {
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_1_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
            let same = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_0_0",
                                        "attributes": {
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_0_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };



            expect(dataValidatorService.uniqueName(different)).to.be.true;
            expect(dataValidatorService.uniqueName(same)).to.be.true;

        });

        it("Parent and child should be allow to have the same name", ()=>{

            let different = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            }
                        ]
                    }
                ]
            };
            let same = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"root",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            }
                        ]
                    }
                ]
            };



            expect(dataValidatorService.uniqueName(different)).to.be.true;
            expect(dataValidatorService.uniqueName(same)).to.be.true;

        });

        xit("nodes, which have childrens, with the same parent should not have the same name", ()=>{
            let correct = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_0_1",
                                        "attributes": {
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_1_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
            let wrong = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_0_1",
                                        "attributes": {
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            },
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                },
                                "children": [
                                    {
                                        "name":"node_1_0",
                                        "attributes":{
                                            "IamAString": "str",
                                            "IAmAnObject": {},
                                            "IAmAnInteger": 22
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };

            expect(dataValidatorService.uniqueName(correct)).to.be.true;
            expect(dataValidatorService.uniqueName(wrong)).to.be.false;
        });

        /**
         * @test {DataValidatorService#validate}
         * @test {DataValidatorService#uniqueName}
         * @test {DataValidatorService#uniqueArray}
         */
        xit("the first level in trees with revisions should be allow to have the same name", ()=>{
            let tree = {
                "nodes" : [
                    {
                        "name":"root",
                        "attributes": {
                            "IamAString": "str",
                            "IAmAnObject": {},
                            "IAmAnInteger": 22
                        },
                        "children": [
                            {
                                "name":"node_0",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            },
                            {
                                "name":"node_1",
                                "attributes":{
                                    "IamAString": "str",
                                    "IAmAnObject": {},
                                    "IAmAnInteger": 22
                                }
                            }
                        ]
                    }
                ]
            };
            let correct = {
                "revisions" : [tree, tree]
            };
             let wrong = {
                "not revisions": [tree, tree]
            };


            expect(dataValidatorService.uniqueName(correct)).to.be.true;

            try{
                dataValidatorService.uniqueName(wrong)
            }catch(err) {
                expect(err).to.deep.equal("TypeError: Cannot read property 'children' of null");
            }
        })
    });


});

