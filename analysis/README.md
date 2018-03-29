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
* Export the desired data to a .xml-file preferably with all available fields.
* > ***./gradlew makeVisFile -PARGS="-xray -i*** exportFilePath.xml ***-o*** visualizationFilePath.json ***"***
* > ***./gradlew makeVisFile -PARGS="-xray -i*** exportFilePath.xml ***-c*** configurattionFilePath ***-o*** visualizationFilePath.json ***"***


##### hp-alm
* Export data with ...sql.
* > ./gradlew makeVisFile -PARGS="-alm -i exportFilePath.[xls|xlsx] -o VisualizationFilePath.json".

The output file can be loaded in the visualization module for visualizing.
