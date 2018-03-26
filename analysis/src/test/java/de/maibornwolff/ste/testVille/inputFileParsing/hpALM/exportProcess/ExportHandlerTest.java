package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.exportProcess;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportHandlerTest {

    @Test
    void writeTest () throws Exception{

        // Arrange
        ExportHandler  exH;

        exH = new ExportHandler("./src/test/resources/TestS.xls","./src/test/resources/TestSConfig.xml") ;
        exH.makeVisualisationFile("./src/test/resources/TestSIstErg.json");

        String strActual   = extractFileContent("./src/test/resources/TestSIstErg.json");
        String strExpected = extractFileContent("./src/test/resources/TestSSollErg.json");

        strExpected = strExpected.replaceAll("\\s+", "").toLowerCase();
        strActual   = strActual.replaceAll("\\s+", "").toLowerCase();

        // Assert
        assertEquals(strExpected, strActual);
    }

    private String extractFileContent(String fileName) throws IOException {
        BufferedReader buffR = new BufferedReader(new FileReader(fileName));
        StringBuilder strBuilder = new StringBuilder();
        String str;
        boolean b = true;

        while(b) {
            str = buffR.readLine();
            if(str != null) {
                strBuilder.append(str);
            }else {
                b = false;
            }
        }
        return strBuilder.toString();
    }

}