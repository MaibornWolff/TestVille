# TestVille analysis

> TestVille by MaibornWolff

#### Requirements
* Bash or similar
* JDK 9 (Oracle OpenJDK)

#### Installation 
* Download / build package
* Unzip / untar package in desired destination folder

#### Build (CLI)
* On Unix systems (Mac, Linux)
> ./gradlew build

* On DOS systems
> gradlew.bat build

#### Test
* On Unix systems (Mac, Linux)
> ./gradlew clean test

* On DOS systems
> gradlew.bat clean test

#### Create a visualization file
This current version support `hp-alm` and `Jira-Xray`

##### Jira-Xray
* Export the desired data to a .xml-file preferably with all available fields.
* ./gradlew makeVisFile -PARGS="-xray -i exportFilePath.xml -o VisualizationFilePath.json".


##### hp-alm
* Export data with ...sql.
* ./gradlew makeVisFile -PARGS="-alm -i exportFilePath.[xls|xlsx] -o VisualizationFilePath.json".

The output file can be loaded in the visualization module for visualizing.