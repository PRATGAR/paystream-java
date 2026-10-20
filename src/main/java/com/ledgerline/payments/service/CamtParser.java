package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.StatementLine;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class CamtParser {

    public List<StatementLine> parse(Path file, DocumentBuilderFactory factory) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file.toFile());
            NodeList entries = document.getElementsByTagName("Ntry");
            List<StatementLine> lines = new ArrayList<>();
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);
                lines.add(new StatementLine(
                        text(entry, "NtryRef"),
                        toCents(text(entry, "Amt")),
                        text(entry, "AddtlNtryInf")));
            }
            return lines;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("statement file could not be read", e);
        }
    }

    private static String text(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent().trim();
    }

    private static long toCents(String amount) {
        try {
            return new BigDecimal(amount).movePointRight(2).longValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            return 0L;
        }
    }
}
