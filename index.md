# Quicklinks

[Visualization Demo](visualization/app/index.html?file=codecharta.cc.json&neutralColorRange.from=20&neutralColorRange.to=40&neutralColorRange.flipped=false&areaMetric=unary&heightMetric=mcc&colorMetric=mcc&deltas=false&amountOfTopLabels=1&scaling.x=1&scaling.y=1&scaling.z=1&camera.x=3354.0273251533754&camera.y=1651.2940622342821&camera.z=3840.682801465516&margin=100&operation=NOTHING&deltaColorFlipped=false&showDependencies=false)

[Downloads](https://github.com/MaibornWolff/TestVille/releases)

# What is CodeCharta ?

TestVille allows the visualization of tests using a city-layout. It consists of two parts:

visualization: GUI for visualising data
analysis:      Tools for generating visualisation data

# Quickstart


## How to install CodeCharta from github

- Download the [latest release](https://github.com/MaibornWolff/TestVille/releases/latest) of TestVille (TestVille-analysis and TestVille-visualization)
- you should now have the analysis and visualization package 
- unpack both packages
- enter the TestVille directory with your favorite console

- > Dos  systems: gradlew.bat build then gradlew.bat buildVisualization
- > Unix systems: ./gradlew   build then ./gradlew   buildVisualization

## Generate a visualization file from test data

Export your test Project preferably with all available fields to a `.xml-file` 
Choose a sonar analysed project of your choice and enter the file, url and project-id. If necessary, generate a User Token in the sonar remote instance and include it in the following command:
  - > Dos  systems: gradlew.bat makeVisFile -PARGS="[options]"
  - > Unix systems: ./gradlew   makeVisFile -PARGS="[options]"
```markdown
Options:
-demo
      Generate demo visualization file
-xray
      Jira-Xray as data origin
-alm
      Hp-alm as data origin
-i, -inputFile
      Input file
-o, --outputFile
      Output file
-c, --config
      Configuration file for customized handling of test fields
```
 
An output file has been created.

## Visualize the analyzed test data

Open the visualization directory and run the executable
A new window should open now. 
![Image](images/screenshot_visu.PNG)

- In the right lower corner, click on the folder icon. 
- Choose the generated .json file from the previous step

Now you can see the analysis of the test data visualized in coloured streets and blocks. 

Navigation:
- left click and drag -> rotate 
- right click and drag -> move the blocks
- mousewheel -> zoom in and out
Tip: If your device has a touch display, you can quickly navigate with your fingers. Use multiple fingers to drag and move the blocks. 

Change the visualized content:
- click on the second button for the settings
![Image](images/screenshot_visu2.PNG)

**Metrics:**

You can choose up to 3 metrics at once to be visualized. One defines the color of each block, one the height and one the area.
Each metric has an own value range, for Example the metric "lines of code" is the exact amount of lines in the file, that is visualized in one block. Some other metrics, like "coverage" use percantages. 

**Scaling:**

If your map is too flat to compare the blocks, or too high to even fit the window, you can scale each axis with the slider.

**Display:**

If you want to compare multiple versions of a project, use "Delta cubes". A red or green block is added on top of those blocks, that changed. This allowes you to observe changes.

**Color Settings:**

Depending on the visualized color-giving metric, you may have different accaptance values. Blocks are either coloured red, green or yellow, which is the neutral color. 
Example: Your color-metric is test-coverage, which has a range from 0 to 100 and is a percantage. 
If you want to colorize everything that has values under 20% red, and higher then 70% green, set "Neutral color start" to 20 and "Neutral color end" to 70. 
Use the "Invert Color" option, to declare a high value to be better then a low value. (A low test-coverage should be red, a low complexity should be green)

# Demo

[Browser Demo](visualization/dist/app/)

# Further information

## Visualization

[Readme](https://github.com/MaibornWolff/TestVille/tree/master/visualization)

[Browser Demo](visualization/app/index.html?file=codecharta.cc.json)

[Coverage](visualization/dist/coverage/lcov-report)

[Reference](visualization/docs/)

## Analysis

[Readme](https://github.com/MaibornWolff/TestVille/tree/master/analysis)

# Contributing

[Contributing](https://github.com/MaibornWolff/codecharta/blob/master/CONTRIBUTE.md)

[Code of Conduct](https://github.com/MaibornWolff/codecharta/blob/master/CODE_OF_CONDUCT.md)

# Releases and Changelog

[Releases](https://github.com/MaibornWolff/TestCharta/releases)

[Changelog](https://github.com/MaibornWolff/codecharta/blob/master/CHANGELOG.md)
