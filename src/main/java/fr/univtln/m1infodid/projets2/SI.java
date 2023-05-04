package fr.univtln.m1infodid.projets2;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SI {

    private static String imgPath = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/";

    private static String getImgUrl(String id, String imgNumber) {
        return  imgPath + id + '/' + imgNumber + ".jpg";
    }

    public static ArrayList<String> extractTextAndImageFromXml(String id, String xmlUrl) {
        ArrayList<String> contentList = new ArrayList<String>();
        try {
            // la récupération du fichier XML à partir de l'URL
            URL url = new URL(xmlUrl);
            InputStream inputStream = url.openStream();

            // la création du Document XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Extraction des contenus des balises image et texte
            NodeList nodeList = doc.getElementsByTagName("*");
            for (int a = 0; a < nodeList.getLength(); a++) {
                Node node = nodeList.item(a);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getTagName().equals("text")) {
                        Element txtElement = (Element) nodeList.item(a+3);
                        // l'emplacement de texte sur le fichier xml
                        contentList.add(txtElement.getTextContent());
                    }

                    if (element.hasAttribute("type") && element.getAttribute("type").equals("translation")) {
                        contentList.add(element.getTextContent());
                    }

                    if (element.getTagName().equals("facsimile")) {
                        Node child = nodeList.item(a+1);
                        Element firstElement = (Element) child;
                        if (firstElement.getTagName().equals("graphic"))
                            contentList.add( firstElement.getAttribute("url") );
                        else if (firstElement.getTagName().equals("desc")) {
                            String imgNum = firstElement.getTextContent();
                            contentList.add( getImgUrl(id, imgNum) );
                        }
                    }
                }
            }

            inputStream.close();

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return contentList;
    }
}
