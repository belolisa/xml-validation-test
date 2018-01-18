package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

public class Main {

    private static final String XML_SCHEMA_FILE_NAME = "schema.xsd";

    public static void main(String[] args) {
        String inputXml;
        String elementToCount;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            inputXml = br.readLine();
            elementToCount = br.readLine();
        } catch (IOException e) {
            System.out.println("Exception while reading input " + e);
            return;
        }

        Schema schema = loadSchema(XML_SCHEMA_FILE_NAME);
        if (schema == null) {
            System.out.println("Can not validate schema because of schema absent");
        } else {
            boolean isXmlSuitesSchema = validateXmlAgainstXSD(inputXml, schema);
            System.out.println(isXmlSuitesSchema ? "Input XML suites schema" : "Input XML does not suite schema");
        }

        int elementNumber;
        try {
            elementNumber = countXmlElements(inputXml, elementToCount);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Could not parse xml");
            return;
        }
        System.out.println("Input xml contains " + elementNumber + " " + elementToCount + " elements");
    }

    private static Schema loadSchema(String fileName) {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        InputStream is = new BufferedInputStream(Main.class.getResourceAsStream(fileName));
        try {
            schema = factory.newSchema(new StreamSource(is));
        } catch (SAXException e1) {
            System.out.println("Can not load schema from fileName " + fileName);
        }
        return schema;
    }

    private static boolean validateXmlAgainstXSD(String xmlToValidate, Schema schema) {
        Source sourceToValidate = new StreamSource(new StringReader(xmlToValidate));
        Validator validator = schema.newValidator();
        try {
            validator.validate(sourceToValidate);
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            System.out.println("Problem with reading xml file");
            return false;
        }
        return true;
    }

    private static int countXmlElements(String xmlToValidate, String element) throws ParserConfigurationException, IOException, SAXException {
        InputSource inputSource = new InputSource(new StringReader(xmlToValidate));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputSource);
        NodeList list = doc.getElementsByTagName(element);
        return list.getLength();
    }

}