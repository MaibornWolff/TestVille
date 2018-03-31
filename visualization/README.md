# TestVille visualization

> MaibornWolff TestVille

## Jump to Section

* [Installation](#installation)
* [Gradle Tasks](#grunt-tasks)
* [JSON structure](#json-structure)
* [License](LICENSE.md)

## Installation
[[Back To Top]](#jump-to-section)

#### Install application for desktop usage

* Download or build [latest version](https://github.com/MaibornWolff/TestVille/releases/latest). 
* Doubleclick the system specific Runnable. You may be required to give it executable rights. 
* run project: see **Gradle Tasks**

Once you have installed the project, you can use all grunt tasks described in the next section.

## Gradle Tasks
[[Back To Top]](#jump-to-section)
* > On Unix Systems (Mac, Linux) use: ./gradlew
* > On Dos Systems use: gradlew.bat

#### Install dependencies

`[./gradlew | gradlew.bat] installDependencies` the first time you use this command it takes a bit longer

#### Build
 
`[./gradlew | gradlew.bat] buildProject` builds the project in dist/app. This artifact is ready to be served as a web application.

`[./gradlew | gradlew.bat] docProject` generates the esdoc documentation in dist/doc/


#### Test

`[./gradlew | gradlew.bat] testProject` runs all unit tests on the source files in app/ and generates a coverage report in dist/coverage/.

#### Run

`[./gradlew | gradlew.bat] serve` starts a simple web server and serves the project on localhost:9000.

#### Watch

`[./gradlew | gradlew.bat] watchApp` watches the app directory and triggers a quick rebuild.

## URL Parameters used by the web application
[[Back To Top]](#jump-to-section)

The web application allows the usage of query parameters in the URL to set 
certain settings. Query params are added by appending a `?` to the url, 
followed by a key value pair `key=value`. Additional parameters can be 
added by appending `&key2=value2`. E.g. `http://yourdomain.com/pathtocc/index.html?file=something.json&scaling.x=2&areaMetric=myMetric`

* The `file` parameter is a special parameter which accepts a file location. The file must be reachable through XHR.
* All other parameters are defined by the [Settings class](/visualization/app/testVille/core/settings/model/settings.js). 
`areaMetric=myMetric` therefore sets the value of settings.areaMetric to `myMetric`. Nested properties like `settings.scale.x` can be 
set by the query parameter `scaling.x=42`
* The `map` parameter is disabled since it would be too much for the URL bar of your browser.
* The URL in your browser gets automatically updated when you change settings through the UI. 
It provides a simple way to customize your links with query parameters.

## JSON structure
[[Back To Top]](#jump-to-section)

[Example Data](/visualization/app/testVille/sample.json)

[JSON Schema](/visualization/app/testVille/core/data/schema.json)
