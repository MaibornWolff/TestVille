"use strict";

import "../../ui/ui.js";
import "../../core/core.js";
import "../../codeMap/codeMap.js";
import "angularjs-slider";

import {settingsPanelComponent, SettingsPanelController} from "./settingsPanelController";

angular.module("app.testVille.ui.settingsPanel", ["app.testVille.ui", "app.testVille.core", "rzModule"])
    .component(settingsPanelComponent.selector, settingsPanelComponent);


