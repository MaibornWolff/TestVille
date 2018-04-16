import {SearchableFactory} from "./searchableFactory.js";

describe("app.testVille.core.searchableFactory", ()=> {

    let jsonNode;

    beforeEach(() => {
        jsonNode = {
            type:     "epic",
            reporter: "JNiada",
            assignee: "JNiada",
            name:     "it should work fast and well",
            children: [
                {
                    type:     "testCaSe",
                    reporter: "No_Name",
                    name:     "it should be a leaf node",
                },
                {
                    type:     "GroupedItem",
                    reporter: "No_Name",
                    assignee: "No_Name",
                    name:     "it should be a parent node",
                    children: [
                        {
                            type:     "epic",
                            reporter: "No_Name",
                            assignee: "No_Name",
                            name:     "it should be a leaf node",
                            children: []
                        }
                    ]
                }
            ]
        }
    });

    it("should return an empty searchable list", () => {
        // arrange
        let actualList   = SearchableFactory.extractSearchableElementsFrom({});

        // act
        let shouldBeEmpty = (actualList.length === 0);

        // assert
        expect(shouldBeEmpty).to.equals(true);
    });

    it("should generate an conform searchable list", () => {
        // arrange
        let expectedList = [
            {name:"it should work fast and well", type:"epic",     reporter:"JNiada",  assignee:"JNiada"},
            {name:"it should be a leaf node",     type:"testCaSe", reporter:"No_Name", assignee:"UNKNOWN"},
            {name:"it should be a leaf node",     type:"epic",     reporter:"No_Name", assignee:"No_Name"}
        ];

        let actualList = SearchableFactory.extractSearchableElementsFrom(jsonNode);

        // act
        expect(angular.equals(expectedList, actualList)).to.equals(true);

    });
});