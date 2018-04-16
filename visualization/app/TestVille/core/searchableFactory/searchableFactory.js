
export class SearchableFactory {

    static extractSearchableElementsFrom(jsonNode) {
        let searchableContainer = [];
        SearchableFactory.extractSearchableElements(jsonNode, searchableContainer);
        return searchableContainer;
    }

    static extractSearchableElements(jsonNode, searchableContainer) {
        SearchableFactory.addNewSearchableElementFrom(jsonNode, searchableContainer);

        if (jsonNode.hasOwnProperty("children") && (jsonNode.children.length > 0)) {
            for (let i = 0; i < jsonNode.children.length; i++) {
                SearchableFactory.extractSearchableElements(jsonNode.children[i], searchableContainer);
            }
        }
    }

    static addNewSearchableElementFrom(jsonNode, searchableContainer) {
        if(SearchableFactory.doesNodeContainsSearchableElement(jsonNode)) {
            searchableContainer.push(SearchableFactory.buildSearchableElement(jsonNode));
        }
    }

    static buildSearchableElement(jsonNode) {
        return {
            name:     SearchableFactory.extractInfoFromNode(jsonNode, "name"),
            type:     SearchableFactory.extractInfoFromNode(jsonNode, "type"),
            reporter: SearchableFactory.extractInfoFromNode(jsonNode, "reporter"),
            assignee: SearchableFactory.extractInfoFromNode(jsonNode, "assignee")
        };
    }

    static doesNodeContainsSearchableElement(jsonNode) {
        return (jsonNode.hasOwnProperty("type")) && SearchableFactory.hasAvailableType(jsonNode);
    }

    static hasAvailableType(jsonNode) {
        switch (jsonNode.type.toLowerCase()) {
            case "epic":
            case "test":
            case "testcase":
            case "requirement": return true;
            default: return false;
        }
    }


    static extractInfoFromNode(jsonNode, infoName) {
        if(jsonNode.hasOwnProperty(infoName)) {
            return jsonNode[infoName];
        }
        return "UNKNOWN";
    }
}