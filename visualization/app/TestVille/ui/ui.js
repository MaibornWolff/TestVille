"use strict";

import "./common/common.js";
import "./fabBar/fabBar.js";
import "./settingsPanel/settingsPanel.js";
import "./revisionChooser/revisionChooser.js";
import "./legendPanel/legendPanel.js";
import "./fileChooser/fileChooser.js";
import "./detailPanel/detailPanel.js";
import "./scenarioButtons/scenarioButtons.js";
import "./search/search.js";

angular.module("app.testVille.ui", ["app.testVille.ui.common", "app.testVille.ui.fabBar", "app.testVille.ui.settingsPanel", "app.testVille.ui.revisionChooser", "app.testVille.ui.legendPanel", "app.testVille.ui.fileChooser", "app.testVille.ui.detailPanel", "app.testVille.ui.scenarioButtons", "app.testVille.ui.search"]);

