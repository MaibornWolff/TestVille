package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.exportProcess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.component.*;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics.*;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.configFile.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;


/**
 *
 * @author jessyn
 * This class read a excel-export file and extract data conform to the given config-file
 */
public class ExportHandler {

    private final String         exportFileName;            // Name of the export file.
    private List<Characteristic> characteristics;          // List of info(Characteristic), to be extract.
    private Set<String>          requirementCategories;   // Categories of Requirement.
    private List<Requirement>    requirementList;        // Bank of Requirement.
    private static int           idComplement = 1;      // For localID's.
    
    /**
     * To construct a new ExportHandler
     * @param exportFileName the name of the export file
     * @param configFileName the name of the config file
     */
    public ExportHandler(String exportFileName, String configFileName) throws Exception{
        this.extractCharacteristicsFromConfigFile(configFileName);
        this.exportFileName          = exportFileName;
        this.requirementCategories   = new  TreeSet<>();
        this.requirementList         = new ArrayList<>();
    }

    private void extractCharacteristicsFromConfigFile(String configFileName) throws Exception{
        try {
            this.characteristics = new ConfigurationHandler(configFileName).buildCharacteristicsList();
        }catch (Exception e){
            System.err.println("Error: can't find \""+configFileName +"\"");
            throw new Exception("!");
        }
    }

    /**
     * Getter for exportFileName.
     * @return the name of the export file.
     */
    private String getExportFileName() {
        return exportFileName;
    }

    /**
     * @return the Setting list of Requirements.
     */
    private List<Characteristic> getSettingForRequirement() {
        List<Characteristic> result = ConfigurationHandler.splitList(this.characteristics,
                x -> (x instanceof Setting) && x.getCharacteristicName().startsWith("requirement")
        );

        ConfigurationHandler.sortByColumn(result);
        return result;

    }

    /**
     * @return the Settings list of TestCases.
     */
    private List<Characteristic> getSettingForTestCase(){
        List<Characteristic> result = ConfigurationHandler.splitList(this.characteristics,
                x -> (x instanceof Setting) && x.getCharacteristicName().startsWith("testCase"))
        ;

        ConfigurationHandler.sortByColumn(result);
        return result;
    }

    /**
     * @return return properties list of TestCases.
     */
    private List<Characteristic> getPropertiesOfTestCase(){
        List<Characteristic> result = ConfigurationHandler.splitList(this.characteristics, x -> x instanceof Property);
        ConfigurationHandler.sortByColumn(result);
        return result;

    }

    private void setComponentSettings(Component comp, List<Characteristic> toBeSet, Row currentRow) {
        Cell currentCell;
        String cellValueAsStr;

        for(Characteristic ch: toBeSet) {
            currentCell    = currentRow.getCell(ch.getColumn());
            cellValueAsStr = setAndGetCellValueAsStr(currentCell);
            comp.setComponentAttributes(ch.getCharacteristicName(), cellValueAsStr);
        }
    }
    
    private void setTestCaseProperties(TestCase test, List<Characteristic> propertiesToSet, Row currentRow) {
        for(Characteristic p: propertiesToSet){
            String fromExportExtractedValue = extractCellContent(currentRow, p.getColumn());
            setTestCaseProperty(test, (Property)p, fromExportExtractedValue);
        }
    }

    private static void setTestCaseProperty(TestCase test, Property propertyToSet, String fromExportExtractedValue) {
        if(propertyToSet.getMappingList().isEmpty()){
            test.addNewProperty(fromExportExtractedValue);
            return;
        }
        String translatedValue = translatePropertyValue(propertyToSet, fromExportExtractedValue);
        test.addNewProperty(translatedValue);
    }

    private static String translatePropertyValue(Property propertyToTranslate, String valueAsString) {
        Pair<String, Integer> translationPair = propertyToTranslate.getMappingList()
                .stream()
                .filter(x -> x.getFirst().toLowerCase().equals(valueAsString.toLowerCase()))
                .reduce(null, (x, y) -> x == null ? y : x)
        ;
        return translationPair == null ? propertyToTranslate.getDefaultValue() : "" + translationPair.getSecond();
    }

    private static String extractCellContent (Row currentRow, int cellNum) {
        Cell currentCell      = currentRow.getCell(cellNum);
        String cellValueAsStr = setAndGetCellValueAsStr(currentCell);
        return cellValueAsStr.trim();
    }


    private Iterator<Row> readExportFileAndExtractAllRows() throws Exception {

        FileInputStream fileStream  = null;

        try {
            fileStream = new FileInputStream(new File(this.getExportFileName()));
            return this.fileStreamToRowIterator(fileStream);
        } catch (Exception e) {
            throw new Exception("Export file could not be opened");
        }

        finally {
            if(fileStream != null) {
                fileStream.close();
            }
        }
    }

    private Iterator<Row>  fileStreamToRowIterator(FileInputStream fileStream) throws Exception {

        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;

        try{
            workbook = new HSSFWorkbook(fileStream);
            fileStream.close();
        }catch (Exception e) {
            throw new Exception("Export file could not be evaluated!");
        }

        finally {
            if (workbook != null) {
                sheet = workbook.getSheetAt(0);
                workbook.close();
            }

        }

        return sheet.iterator();
    }

    /**
     * This method travers the excelExport with the APACHE-POI api and extract Information.
     * the extracted Information are stored in the correspondent structure. Example: Each Row in the
     * excelExport contains a Testcase. Each Testcase is stored in a TestCase-Object and each TestCase is stored in
     * the TestCaseBank of this Object.
     * @throws IOException by read
     */
    private void  traverseDocumentAndExtractedAllNeededInformations() throws Exception {
        Iterator<Row> rowIteratorOnExport           = readExportFileAndExtractAllRows();
        List<Characteristic> testCasePropertyList   = this.getPropertiesOfTestCase();
        List<Characteristic> testCaseSettingList    = this.getSettingForTestCase();
        List<Characteristic> requirementSettingList = this.getSettingForRequirement();

        rowIteratorOnExport.next();

        System.out.println("--\nStart handling...");
        while(rowIteratorOnExport.hasNext()) {
            Row         currentRow                = rowIteratorOnExport.next();
            TestCase    onRowAvailableTest        = new TestCase();
            Requirement onRowAvailableRequirement = new Requirement();

            onRowAvailableTest        .setLocalID(idComplement++);
            onRowAvailableRequirement .setLocalID(idComplement++);

            this.setComponentSettings (onRowAvailableRequirement,  requirementSettingList, currentRow);
            this.setComponentSettings (onRowAvailableTest,         testCaseSettingList,    currentRow);
            this.setTestCaseProperties(onRowAvailableTest,         testCasePropertyList,   currentRow);
            this.addRequirementIfAbsentOrAddTestCase(onRowAvailableRequirement, onRowAvailableTest);
            this.requirementCategories.add(onRowAvailableRequirement.getPriority());
        }
        this.traverseDocumentSummary();
    }

    private void addRequirementIfAbsentOrAddTestCase(Requirement maybeNewRequirement, TestCase newTestCase) {
        boolean newRequirementIsAbsent = true;

        for(Requirement r: this.requirementList) {
            if (r.equals(maybeNewRequirement)) {
                r.addNewTestCase(newTestCase);
                newRequirementIsAbsent = false;
                break;
            }
        }

        if (newRequirementIsAbsent) {
            maybeNewRequirement.addNewTestCase(newTestCase);
            this.requirementList.add(maybeNewRequirement);
        }
    }


    private void traverseDocumentSummary() {

        int testCaseSize = this.requirementList.stream()
                .map(x -> x.getMyTests().size())
                .reduce(0, (x, y) -> x + y)
        ;
        System.out.println("--");
        System.out.println("Extracted TestCases:    " + testCaseSize);
        System.out.println("Extracted Requirements: " + this.requirementList.size());
        System.out.println("--");


    }

    /**
     * this method change the data type of a Cell to String before she return
     * the correspondent value has String.
     * @param cell the current cell.
     * @return the Content of a Cell has String.
     */
    private static String setAndGetCellValueAsStr(Cell cell) {
        String cellValue;

        try {
            cell.setCellType(CellType.STRING);
            cellValue = cell.getStringCellValue().trim();
        }catch(Exception e) {
            cellValue = "";
        }

        return cellValue;
    }

    /**
     * This Method write the Data for the Visualization in TestVille
     * @param fileName: Name of the Output-file
     * @throws IOException by write
     */
    private void writeVisualizationFile(String fileName) throws Exception {
        this.traverseDocumentAndExtractedAllNeededInformations();
        OutputStreamWriter writer = generateWriterForIOOperation(fileName);
        System.out.println("Write json-file for TestVille...");
        writer.write(getVisualizationFileHeader());

        this.writeAllExtractedRequirements(writer);

        writer.write("]}]}");
        writer.close();
        System.out.println("--\nOperation success!\n--");
    }

    private void writeAllExtractedRequirements(OutputStreamWriter writer) throws Exception{
        boolean firstWriteAction = true;

        for(String categoryName : this.requirementCategories) {
            StringBuilder groupedRequirementsString = this.produceWritableRepresentationForRequirementsOfCategory(categoryName, idComplement++);

            if(groupedRequirementsString.length() != 0){
                writeNextElement(firstWriteAction, writer, groupedRequirementsString);
                firstWriteAction = false;
            }
        }
    }

    private static void writeNextElement(boolean firstWriteAction, OutputStreamWriter writer, StringBuilder toWrite) throws IOException {
        if(firstWriteAction) {
            writer.write(toWrite.toString());
            return;
        }
        writer.write(", ");
        writer.write(toWrite.toString());
    }

    private static OutputStreamWriter generateWriterForIOOperation(String targetFileName) throws Exception {
        File toWrite  = new File(targetFileName);
        try {
            return new OutputStreamWriter(new FileOutputStream(toWrite), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Exception("The visualization file can't be wrote");
        }
    }

    private StringBuilder produceWritableRepresentationForRequirementsOfCategory(String categoryName, int categoryId) {
        final List<String> testPropertyNames = new ArrayList<>();
        this.getPropertiesOfTestCase().forEach(x -> testPropertyNames.add(x.getCharacteristicName()));
        StringBuilder result = this.requirementList
                .stream()
                .filter(x -> x.getPriority().equals(categoryName))
                .map(y -> y.requirementAsStringForIO(testPropertyNames))
                .reduce(new StringBuilder(), (e, f) -> e.length() == 0 ? e.append(f) : e.append(", ").append(f))
        ;
        return (result.length() == 0) ? result
                : Requirement.produceRequirementCategoryHead(categoryName, categoryId).append(result).append("]}\n")
        ;
    }

    /**
     *
     * @param visualisationFileName name of the visualization file.
     */
    public void makeVisualisationFile(String visualisationFileName) throws Exception{
        this.writeVisualizationFile(visualisationFileName);
    }

    private static String getVisualizationFileHeader() {
        return "{\"projectName\":\"___________\"," +
                "\"_comment\": \"Visualization file generated by TestVille_1.0.0_\"" +
                ", \"nodes\":\n[{\"name\":\"root\", \"type\":\"root\",\"attributes\":{}, \"children\":["
        ;
    }

}