package de.maibornwolff.ste.testVille.inputFileParsing.hpALM;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMapBuilder;
import de.maibornwolff.ste.testVille.configurationFileHandling.ExcelExtractionProtocol;
import de.maibornwolff.ste.testVille.configurationFileHandling.ConfigurationFileValidator;
import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.domainModell.hpALM.Requirement;
import de.maibornwolff.ste.testVille.inputFileParsing.Parser;
import de.maibornwolff.ste.testVille.inputFileParsing.common.IDGenerator;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class HpAlmParser implements Parser {

    private ExcelExtractionProtocol extractionProtocol;
    private Map<String, String>  onRowAvailableData;
    private Set<Requirement>     requirements;        // Bank of Requirement.
    private IDGenerator          localIDGenerator = new IDGenerator();
    int testCaseNumber = 0;


    private int generateNextLocalId() {
        return this.localIDGenerator.generateNextUniqueKey();
    }

    public HpAlmParser() {
        this.requirements       = new TreeSet<>();
        this.onRowAvailableData = new HashMap<>();
    }

    private void initExtractionProtocolAndPriorityRanking(String configFilePath) throws Exception {
        ConfigurationFileValidator.validateConfigurationFile(configFilePath, ManagementTool.HP_ALM);
        this.extractionProtocol = ExcelExtractionProtocol.buildFrom(configFilePath);
        TranslationMapBuilder tmb = new TranslationMapBuilder(configFilePath, ManagementTool.HP_ALM);
        this.showItemsTheirPriorityRanking(tmb);
        this.showTestCaseTheirTranslationMap(tmb);
    }

    private void showTestCaseTheirTranslationMap(TranslationMapBuilder tmb) {
        TestCase.translationMap = tmb.getTranslationMap();
    }

    private void showItemsTheirPriorityRanking(TranslationMapBuilder tmb) {
        Item.priorityRanking = tmb.getPriorityRanking();
    }

    private static String extractCellContent (Row currentRow, int cellNum) {
        Cell currentCell      = currentRow.getCell(cellNum);
        String cellValueAsStr = getCellValueAsStr(currentCell);
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

    private void  exploreExportFileAndExtractItems(String exportFilePath) throws Exception {
        Iterator<Row> rowIteratorOnExport = readExportFileAndExtractAllRows(exportFilePath);
        rowIteratorOnExport.next();

        while(rowIteratorOnExport.hasNext()) {
            Row         currentRow = rowIteratorOnExport.next();
            this.saveDataFromCurrentRow(currentRow);
            TestCase    onRowAvailableTest        = this.buildTestCaseFromAvailableData();
            Requirement onRowAvailableRequirement = this.buildRequirementFromAvailableData();
            saveExtractedRequirementAndTestCase(onRowAvailableRequirement, onRowAvailableTest);
            this.reinitializeOnRowAvailableData();
        }
    }

    private void saveDataFromCurrentRow(Row currentRow) {
        this.extractionProtocol.getProtocol()
                .forEach((k, v) ->{
                    String cellValueAsString = extractCellContent(currentRow, v);
                    this.onRowAvailableData.putIfAbsent(k, cellValueAsString);
                });
    }

    private TestCase buildTestCaseFromAvailableData() {
        TestCase tc = new TestCase(this.generateNextLocalId());
        this.setTestCaseSettings(tc);
        this.setTestCaseProperties(tc);
        return tc;
    }

    private Requirement buildRequirementFromAvailableData() {
        Requirement req = new Requirement(this.generateNextLocalId());
        this.setRequirementSettings(req);
        return req;
    }

    private void setTestCaseSettings(TestCase testCase) {
        testCase.setKey(this.onRowAvailableData.get("testCaseId"));
        testCase.setName(this.onRowAvailableData.get("testCaseName"));
        testCase.setPriority(this.onRowAvailableData.get("testCasePriority"));
    }

    private void setRequirementSettings(Requirement requirement) {
        requirement.setKey(this.onRowAvailableData.get("requirementId"));
        requirement.setName(this.onRowAvailableData.get("requirementName"));
        requirement.setPriority(this.onRowAvailableData.get("requirementPriority"));
    }

    private void setTestCaseProperties(TestCase testCase) {
        this.onRowAvailableData.forEach((x, y) -> {if(!isSettingName(x)) testCase.addNewProperty(x, y);});
    }

    private void reinitializeOnRowAvailableData() {
        this.onRowAvailableData = new HashMap<>();
    }

    private static boolean isSettingName(String name) {
        return name.equals("requirementId")
                || name.equals("requirementName")
                || name.equals("requirementPriority")
                || name.equals("testCaseId")
                || name.equals("testCaseName");
    }

    private void addRequirementIfAbsent(Requirement maybeNewRequirement) {
        this.requirements.add(maybeNewRequirement);
    }

    private void saveExtractedRequirementAndTestCase(Requirement requirement, TestCase testCase) {
        Requirement alreadyContainedRequirement = findRequirementEquals(requirement);
        if(alreadyContainedRequirement != null) {
            alreadyContainedRequirement.addAssociatedItems(testCase);
            return;
        }
        requirement.addAssociatedItems(testCase);
        this.addRequirementIfAbsent(requirement);
    }

    private Requirement findRequirementEquals(Requirement requirement) {
        List<Requirement> likeR = this.requirements.stream().filter(x -> x.equals(requirement)).collect(Collectors.toList());
        if(likeR.isEmpty()) return null;
        return likeR.get(0);
    }

    private static String getCellValueAsStr(Cell cell) {
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
    public ComposedItem parse(String fileToParse, String configurationFilePath) throws Exception {
        this.initExtractionProtocolAndPriorityRanking(configurationFilePath);
        this.exploreExportFileAndExtractItems(fileToParse);
        return this.buildParsedTree();
    }

    private ComposedItem buildParsedTree() {
        ComposedItem parsedTree = new ComposedItem(0);
        parsedTree.addAssociatedItems(this.requirements);
        parsedTree.setKey(""+parsedTree.getLocalId());
        parsedTree.setName("root");
        parsedTree.setPriority("root");
        return parsedTree;
    }
}