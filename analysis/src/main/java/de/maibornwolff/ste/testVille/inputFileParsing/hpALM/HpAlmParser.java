package de.maibornwolff.ste.testVille.inputFileParsing.hpALM;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.domainModell.hpALM.Requirement;
import de.maibornwolff.ste.testVille.inputFileParsing.Parser;
import de.maibornwolff.ste.testVille.inputFileParsing.common.IDGenerator;
import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
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
public class HpAlmParser implements Parser {

    private List<Characteristic> characteristics;      // List of info(Characteristic), to be extract.
    private Set<Requirement>     requirements;        // Bank of Requirement.
    private IDGenerator localIDGenerator = new IDGenerator();


    private int generateNextLocalId() {
        return this.localIDGenerator.generateNextUniqueKey();
    }

    public HpAlmParser() {
        this.requirements            = new TreeSet<>();
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


    private static void setTestCaseProperties(TestCase test, List<Characteristic> propertiesToSet, Row currentRow) {
        for(Characteristic p: propertiesToSet){
            String fromExportExtractedValue = extractCellContent(currentRow, p.getColumn());
            setTestCaseProperty(test, (Property)p, fromExportExtractedValue);
        }
    }

    private static void setTestCaseProperty(TestCase test, Property propertyToSet, String fromExportExtractedValue) {
        if(propertyToSet.getMappingList().isEmpty()){
            test.addNewProperty(propertyToSet.getCharacteristicName(), fromExportExtractedValue);
            return;
        }
        String translatedValue = translatePropertyValue(propertyToSet, fromExportExtractedValue);
        test.addNewProperty(propertyToSet.getCharacteristicName(), translatedValue);
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


    private Iterator<Row> readExportFileAndExtractAllRows(String exportFilePath) throws Exception {

        FileInputStream fileStream  = null;

        try {
            fileStream = new FileInputStream(new File(exportFilePath));
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
        HSSFSheet sheet       = null;

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
    private void  exploreExportFileAndExtractItems(String exportFilePath) throws Exception {
        Iterator<Row> rowIteratorOnExport = readExportFileAndExtractAllRows(exportFilePath);
        rowIteratorOnExport.next();

        System.out.println("--\nStart handling...");
        while(rowIteratorOnExport.hasNext()) {
            Row         currentRow                = rowIteratorOnExport.next();
            TestCase    onRowAvailableTest        = extractTestCaseFromRow(currentRow);
            Requirement onRowAvailableRequirement = extractRequirementFromRow(currentRow);
            saveExtractedRequirementAndTestCase(onRowAvailableRequirement, onRowAvailableTest);
        }
        this.traverseDocumentSummary();
    }

    private Requirement extractRequirementFromRow(Row currentRow) {
        Requirement onRowAvailableRequirement = new Requirement(this.generateNextLocalId());
        setRequirementSettings(onRowAvailableRequirement, currentRow, this.getSettingForRequirement());
        return onRowAvailableRequirement;
    }

    private static void setRequirementSettings(Requirement requirement, Row currentRow,  List<Characteristic> characteristicsToExtractFromRow) {
        setItemSettings(requirement, currentRow, characteristicsToExtractFromRow);
    }

    private TestCase extractTestCaseFromRow(Row currentRow) {
        TestCase onRowAvailableTest = new TestCase (this.generateNextLocalId());
        setTestCaseSettings  (onRowAvailableTest, currentRow, this.getSettingForTestCase());
        setTestCaseProperties(onRowAvailableTest, this.getPropertiesOfTestCase(),   currentRow);
        return onRowAvailableTest;
    }

    private static void setTestCaseSettings(TestCase testCase, Row currentRow,  List<Characteristic> characteristicsToExtractFromRow) {
        setItemSettings(testCase, currentRow, characteristicsToExtractFromRow);
    }

    private static void setItemSettings(Item item, Row currentRow,  List<Characteristic> characteristicsToExtractFromRow) {
        Cell currentCell;
        String cellValueAsString;

        for(Characteristic ch: characteristicsToExtractFromRow) {
            currentCell       = currentRow.getCell(ch.getColumn());
            cellValueAsString = setAndGetCellValueAsStr(currentCell);
            setItemSetting(item, ch.getCharacteristicName(), cellValueAsString);
        }
    }

    private static void setItemSetting(Item item, String settingName, String settingValue) {
        String conformValue = ConfigurationHandler.toConformStr(settingValue);

        if(settingName.contains("Id")) {
            item.setKey(conformValue);
        }else if(settingName.contains("Name")) {
            item.setName(conformValue);
        }else if(settingName.contains("Priority")) {
            item.setPriority(conformValue);
        }
    }

    private void addRequirementIfAbsent(Requirement maybeNewRequirement) {
        this.requirements.add(maybeNewRequirement);
    }

    private void saveExtractedRequirementAndTestCase(Requirement requirement, TestCase testCase) {
        Requirement alreadyContainedRequirement = findRequirementEquals(requirement);
        if(alreadyContainedRequirement != null) {
            alreadyContainedRequirement.addAllAssociatedItems(testCase);
            return;
        }
        requirement.addAllAssociatedItems(testCase);
        this.addRequirementIfAbsent(requirement);
    }

    private Requirement findRequirementEquals(Requirement requirement) {
        List<Requirement> likeR = this.requirements.stream().filter(x -> x.equals(requirement)).collect(Collectors.toList());
        if(likeR.isEmpty()) return null;
        return likeR.get(0);
    }

    private void traverseDocumentSummary() {

        int testCaseSize = this.requirements.stream()
                .map(x -> x.getAssociatedItems().size())
                .reduce(0, (x, y) -> x + y)
        ;
        System.out.println("--");
        System.out.println("Extracted TestCases:    " + testCaseSize);
        System.out.println("Extracted Requirements: " + this.requirements.size());
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

    @Override
    public Collection<Item> parse(String fileToParse, String configurationFile) throws Exception {
        this.extractCharacteristicsFromConfigFile(configurationFile);
        this.exploreExportFileAndExtractItems(fileToParse);
        return this.castToItems(this.requirements);
    }
}