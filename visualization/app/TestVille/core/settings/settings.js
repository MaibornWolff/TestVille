"use strict";

import "../url/url.js";
import "../data/data.js";

import {SettingsService} from "./settingsService.js";

angular.module(
    "app.testVille.core.settings",
    ["app.testVille.core.url", "app.testVille.core.data"]
);

angular.module("app.testVille.core.settings").service(
    "settingsService", SettingsService
);

