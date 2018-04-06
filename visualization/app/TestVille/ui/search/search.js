"use strict";

import "../../core/core.js";
import "../../codeMap/codeMap.js";

import {SearchController} from "./searchController.js";
import {SearchDirective} from"./searchDirective.js";


angular.module("app.testVille.ui.search",["app.testVille.core.data", "app.testVille.codeMap"]);

angular.module("app.testVille.ui.search").controller(
    "searchController",
    SearchController
);

angular.module("app.testVille.ui.search").directive(
    "searchDirective",
    () => new SearchDirective()
);

//prevents pagemoving, when using the arrowkeys in an inputfield
angular.module("app.testVille.ui.search").directive("disableArrows", function() {

                                            function disableArrows(event) {
                                              if (event.keyCode >= 37 || event.keyCode <= 40) {
                                                event.stopImmediatePropagation();

                                              }
                                            }

                                            return {

                                              link: function(scope, element, attrs) {
                                                element.on("keydown", disableArrows);
                                              }

                                            };
                                          });


