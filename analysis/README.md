# TestVille analysis

> TestVille by MaibornWolff

#### Requirements
* Bash or similar
* JDK 9 (Oracle OpenJDK)

#### Installation 
* Download / build package
* Unzip / untar package in desired destination folder

#### Build (CLI-Commands)
* On Unix systems (Mac, Linux) use:
> ./gradlew build

* On DOS systems use:
> gradlew.bat build

#### Test
* On Unix systems (Mac, Linux) use:
> ./gradlew clean test

* On DOS systems use:
> gradlew.bat clean test

#### Create a visualization file
The current version supports `Jira-Xray` and `hp-alm`.

##### Jira-Xray
* Export the desired test data to a .xml-file preferably with all available fields.

###### On Unix Systems (Mac, Linux)
* > ***./gradlew analyse -PARGS="-xray -i*** exportFilePath.xml ***-o*** visualizationFilePath.json ***"***
* > ***./gradlew analyse -PARGS="-xray -i*** exportFilePath.xml ***-c*** configFilePath.xml ***-o*** visualizationFilePath.json ***"***

###### On Dos Systems
* > ***gradlew.bat analyse -PARGS="-xray -i*** exportFilePath.xml ***-o*** visualizationFilePath.json ***"***
* > ***gradlew.bat analyse -PARGS="-xray -i*** exportFilePath.xml ***-c*** configFilePath.xml ***-o*** visualizationFilePath.json ***"***


##### hp-alm
* Export your test data with this [script](./exportScripts/exportHpAlm_v02.sql).
###### On Unix Systems (Mac, Linux)
* > ***./gradlew analyse -PARGS="-alm -i*** exportFilePath.xml ***-o*** visualizationFilePath.json ***"***
* > ***./gradlew analyse -PARGS="-alm -i*** exportFilePath.xml ***-c*** configFilePath.xml ***-o*** visualizationFilePath.json ***"***

###### On Dos Systems
* > ***gradlew.bat analyse -PARGS="-alm -i*** exportFilePath.xml ***-o*** visualizationFilePath.json ***"***
* > ***gradlew.bat analyse -PARGS="-alm -i*** exportFilePath.xml ***-c*** configFilePath.xml ***-o*** visualizationFilePath.json ***"***


The output file can be loaded in the visualization module.
