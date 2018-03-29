package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.configFile;

import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics.Characteristic;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics.Property;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics.Setting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ConfigurationHandlerTest {

    private ConfigurationHandler handler1;
    private ConfigurationHandler handler2 = new ConfigurationHandler("handler2.xml");
    private Node node = null;

    @BeforeEach
    void setUp() throws Exception {
        handler1      = new ConfigurationHandler("./src/test/resources/TestSetConfig.xml");
        String fileName = "./src/test/resources/TestSetConfig.xml";
        try {
            node = DocumentBuilderFactory.
                    newInstance().
                    newDocumentBuilder().
                    parse(fileName);
        } catch (Exception e) {
            System.err.println("The ConfigurationFile could not be parsed!");
            System.err.println(e.getMessage());
        }
    }

    @Test
    void getConfigName() throws Exception {
        //assert
        assertEquals("./src/test/resources/TestSetConfig.xml",
                handler1.getConfigurationFileName(),
                "The ConfigurationHandler has an incorrect fileName");
    }



    @TestFactory
    Collection<DynamicTest> getNodeWithProperty() throws Exception {
        // arrange
        List<Node> result = new ArrayList<>();

        //act
        handler1.getNodeWithProperty(node, x -> x.getNodeType() == Node.ELEMENT_NODE && x.getNodeName().equals("property"),
                result);
         //assert
        DynamicTest dt1 = DynamicTest.dynamicTest("the size of the properties == 13",
                () -> assertEquals(13, result.size()))
        ;

        Node n = result.get(2);
        final NamedNodeMap attrNode1 = n.getAttributes();

        DynamicTest dt2 = DynamicTest.dynamicTest("the property must have 3 attributes",
                () -> assertEquals(3, attrNode1.getLength()));

        DynamicTest dt3 = DynamicTest.dynamicTest("the name of the property must be testCasePriority",
                () -> assertEquals("testCasePriority", attrNode1.getNamedItem("propertyName").getNodeValue()));

        DynamicTest dt4 = DynamicTest.dynamicTest("the property has 10 as columnNum",
                () -> assertEquals("10", attrNode1.getNamedItem("column").getNodeValue()));

        DynamicTest dt5 = DynamicTest.dynamicTest("the default value of the property must be 0",
                () -> assertEquals("0", attrNode1.getNamedItem("default").getNodeValue()));

        List<Node> res = new ArrayList<>();
        for(int i = 0; i < n.getChildNodes().getLength(); i++) {
            if(n.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                res.add(n.getChildNodes().item(i));
            }
        }

        final NamedNodeMap attrNode2 = res.get(1).getAttributes();
        DynamicTest dt6 = DynamicTest.dynamicTest("The numericalValueNode should have 2 attributes",
                () -> assertEquals(2, attrNode2.getLength()));

        DynamicTest dt7 = DynamicTest.dynamicTest("the valueKey must be 3-Medium",
                () -> assertEquals("3-Medium", attrNode2.getNamedItem("valueKey").getNodeValue()));

        DynamicTest dt8 = DynamicTest.dynamicTest("the mapped value for 3-Medium should be 2",
                () -> assertEquals("2", attrNode2.getNamedItem("value").getNodeValue()));

        return List.of(dt1, dt2, dt3, dt4, dt5, dt6, dt7,dt8);
    }


    @TestFactory
    Collection<DynamicTest> xmlToList() throws Exception {
        //arrange
        List<Characteristic> list           = handler1.buildCharacteristicsList();
        int countSetting  = 0;
        int countProperty = 0;
        // act
        for (Characteristic x: list) {
            if(x instanceof Setting)        countSetting  ++;
            else if (x instanceof Property) countProperty ++;
        }
        // act & assert
        DynamicTest dt1 = DynamicTest.dynamicTest("find a certain setting in the list of characteristics",
                () -> assertEquals(new Setting("testCaseId", "", 0), list.get(4)));

        DynamicTest dt2 = DynamicTest.dynamicTest("find a certain property in the list of characteristics",
                () -> assertEquals(new Setting("testCasePriority", "NO_PRIO", 10), list.get(6)));

        final int cs = countSetting;
        final int cp = countProperty;
        DynamicTest dt3 = DynamicTest.dynamicTest("7 founded settings",
                () -> assertEquals(7, cs));

        DynamicTest dt4 = DynamicTest.dynamicTest("13 founded properties",
                () -> assertEquals(13, cp));

        return List.of(dt1, dt2, dt3, dt4);
    }


    @Test
    void getTestsProperties() throws Exception {
        List <Node> ar           = new ArrayList<>();
        handler2.getNodeWithProperty(node, x -> (x.getNodeType() == Node.ELEMENT_NODE) && (x.getNodeName().equals("properties")), ar);
        List <Characteristic> br = handler2.extractTestProperties(ar.get(0));
        assertEquals(new Property("testResult", "5", 20),
                br.get(5),
                "The extracted property is incorrect");
    }


    @Test
    void sortByColumn() throws Exception {
        //arrange
        List<Characteristic> list = handler1.buildCharacteristicsList();
        ConfigurationHandler.sortByColumn(list);
        boolean b = list.get(1).getCharacteristicName().equals("testCaseId");
        b &= list.get(5).getCharacteristicName().equals("developmentPeriod");
        b &= list.get(10).getCharacteristicName().equals("requirementPriority");
        b &= list.get(15).getCharacteristicName().equals("countOpenedDefects");
        assertEquals(true, b, "Wrong Sorting");
    }

    @Test
    void splitList() throws Exception {
        //arrange
        List<Characteristic> list = handler1.buildCharacteristicsList();
        List<Characteristic> li2 = ConfigurationHandler.splitList(list,
                x -> x.getCharacteristicName().toLowerCase().startsWith("requirement"));

        //act
        StringBuilder strBuilder = new StringBuilder();
        for(Characteristic ch: li2) {
            strBuilder.append(ch.getCharacteristicName());
        }
        //assert
        assertEquals("requirementIdrequirementNamerequirementPriority", strBuilder.toString());

    }

    @Test
    void settingNameCorrection() throws Exception {
        String a = ConfigurationHandler.settingNameCorrection("test");
        String b = ConfigurationHandler.settingNameCorrection("testcoLumn");
        assertEquals("test", a);
        assertEquals("test", b);

    }

    @TestFactory
    Collection<DynamicTest> toConformStr() throws Exception {
        DynamicTest dt1 = DynamicTest.dynamicTest("The conformed name is empty",
                () -> assertEquals("",ConfigurationHandler.toConformStr("\\")))
        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("The conformed name is ,,test''",
                () -> assertEquals("test",ConfigurationHandler.toConformStr("te\\st")))
        ;
        return List.of(dt1, dt2);
    }

    @TestFactory
    Collection<DynamicTest> exceptionsTest() {
        DynamicTest dt1 = DynamicTest.dynamicTest("must throw an exception",
                () -> assertEquals("can't parse the configuration file!",
                        ConfigurationMessageOfConfigurationHandler(new ConfigurationHandler("bot.xml")))
                )
        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("must throw an exception",
                () -> assertEquals("There is no settingsNode in the configuration file!",
                        ConfigurationMessageOfConfigurationHandler(new ConfigurationHandler("./src/test/resources/onlyProperties.xml")))
                )
        ;
        DynamicTest dt3 = DynamicTest.dynamicTest("must throw an exception",
                () -> assertEquals("There is no PropertiesNode in the configuration file!",
                        ConfigurationMessageOfConfigurationHandler(new ConfigurationHandler("./src/test/resources/onlySettings.xml")))
                )
        ;
        DynamicTest dt4 = DynamicTest.dynamicTest("must throw an exception",
                () -> assertEquals("There are many settingsNode in the configuration file!",
                        ConfigurationMessageOfConfigurationHandler(new ConfigurationHandler("./src/test/resources/twoSettings.xml")))
                )
        ;

        DynamicTest dt5 = DynamicTest.dynamicTest("must throw an exception",
                () -> assertEquals("There are many PropertiesNodes in the configuration file!",
                        ConfigurationMessageOfConfigurationHandler(new ConfigurationHandler("./src/test/resources/twoProperties.xml")))
                )
        ;
        return List.of(dt1, dt2, dt3, dt4, dt5);
    }


    @Test
    void exceptionByExtractingSettingAndProperties() {
        // arrange
        boolean b;

        // act
        //b =  handler1.extractTestSettings(node)  .isEmpty();
        b = handler1.extractTestProperties(node).isEmpty();

        //assert
        assertEquals(true, b, "");
    }


    private String ConfigurationMessageOfConfigurationHandler(ConfigurationHandler c) {
        try {
            c.buildCharacteristicsList();
            return "";
        }catch (Exception e){
            return e.getMessage();
        }
    }

}