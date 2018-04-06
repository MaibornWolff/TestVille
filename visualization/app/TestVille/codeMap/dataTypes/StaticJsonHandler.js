

"use strict";
// d3 hierarchy
export class StaticJsonHandler {

    constructor() {}

    /**
     *
     * @param json
     */
    static getNodes(json) {
        return json.nodes[0];
    }



    /**
     *
     */
    static getMetricsList(json) {

        let result = [];
        let i = 0;
        let elem;

        for(elem in json.attributes) {

            if(elem !== null) {
                result.push(elem);
            }

        }

        return result;
    }



    /**
     *
     */
    static treeDepth(json) {

        if( !json.hasOwnProperty("children")) {
            return 0;
        } else if (json.children.length === 0) {
            return 0;
        }

        let i = 0;
        let compare = 0;
        let a;

        while(i < json.children.length) {

            a = StaticJsonHandler.treeDepth(json.children[i]);

            if(compare < a) {
                compare = a;
            }

            i++;
        }

        return (compare + 1);

    }



    /**
     *
     */
    static getLeafs(json, result) {

        if(!json.hasOwnProperty("children") || (json.children.length === 0)) {
            result.push(json);
            return;
        }

        let len = json.children.length;
        let i = 0;
        let childRes;

        while(i < len) {
            StaticJsonHandler.getLeafs(json.children[i], result);

            i++;
        }

        return result;
    }



    /**
     *
     */
    static leafSize(json) {
        return StaticJsonHandler.getLeafs(json).length;
    }


    /**
     *
     */
     static getAttributeValue(json, attrName) {
         if(!json.attributes.hasOwnProperty(attrName)) {
             return -1;
         }
        return json.attributes[attrName];
     }


    /**
     *
     */
    static getAttribMaxValue(array, attrNames) {

        if((!Array.isArray(array)) && (!Array.isArray(attrNames))) {
            throw "Error:: getAttribMaxValue";
        }

        let i;
        let j = 0;
        let max = [];
        let num;
        let firstTime = true;

        while(j < array.length) {
            i = 0;

            while(i < attrNames.length) {
                num = Number(StaticJsonHandler.getAttributeValue(array[j], attrNames[i]));

                if(firstTime) {
                    max.push(num);
                } else if (max[i] < num) {
                    max[i] = num;
                }
                i++;
            }

            firstTime = false;

            j++;
        }

        return max;
    }


    /**
     * return the index, where the max of "metricName" is stored.
     * @param metricArr: set of metrics.
     * @param metricName: metric of their the max are searched.
     * @returns {number}
     */
    static getMaxMetricIndex(metricArr, metricName) {
        let i = 0;

        while(i < metricArr.length) {

            if(metricArr[i].localeCompare(metricName) === 0) {
                return i;
            }

            i++;
        }
    }

    static getName(json)     { return json.name; }

    static getId(json)       { return json.id; }

    static getType(json)     { return json.type; }

    static inverseOrOne(num) { return (num === 0) ? 1 : (1/num); }

    static getPriority(json) {return json.priority;}
}