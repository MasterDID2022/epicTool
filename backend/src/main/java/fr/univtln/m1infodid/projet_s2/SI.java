package fr.univtln.m1infodid.projets2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Cette classe SI ...
 */
public class SI {
    private static String imgPath = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/";

    /**
     * @param id l'id de la fiche
     * @param imgNumber
     * @return l url de l image qui sera egale au chemin imgPath + id + imgNumber
     */
    public static String getImgUrl(String id, String imgNumber) {
        return  imgPath + id + '/' + imgNumber + ".jpg";
    }
    /**
     * @param id l'id de la fiche
     * @param xmlUrl l'url de la fiche
     * @return le contenu des balises image et texte
     */
    public static ArrayList<String> extractTextAndImageFromXml(String id, String xmlUrl){
        ArrayList<String> contentList = new ArrayList<String>();
        try {
            // la récupération du fichier XML à partir de l'URL
            URL url = new URL(xmlUrl);
            InputStream inputStream = url.openStream();

            // la création du Document XML

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Extraction du contenus des balises image et texte
            NodeList nodeList = doc.getElementsByTagName("*");
            contentList.add(id);
            for (int a = 0; a < nodeList.getLength(); a++) {
                Node node = nodeList.item(a);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    if (element.getTagName().equals("text")) {
                        Element txtElement = (Element) nodeList.item(a + 3);
                        // l'emplacement de texte sur le fichier xml
                        contentList.add(txtElement.getTextContent());
                    }
                    //recherche nom auteur 
                    if (element.getTagName().equals("persName")) {
                        Element txtElement = (Element) nodeList.item(a);
                        contentList.add(txtElement.getTextContent());
                    }
                    //recherche date
                    if (element.getTagName().equals("date")) {
                        Element txtElement = (Element) nodeList.item(a);
                        String whenValue = txtElement.getAttribute("when");
                        contentList.add(whenValue);
                    }
                    //recupere le contenu de traduction
                    if (element.hasAttribute("type") && element.getAttribute("type").equals("translation")) {
                        contentList.add(element.getTextContent());
                    }
                    //recupere le contenu de traduction
                    if (element.hasAttribute("when") && element.getAttribute("when").equals("date")) {
                        contentList.add(element.getTextContent());
                    }
                    //recupere l image
                    if (element.getTagName().equals("facsimile")) {
                        Node child = nodeList.item(a + 1);
                        Element firstElement = (Element) child;
                        //si une seule image existe
                        if (firstElement.getTagName().equals("graphic"))
                            contentList.add(firstElement.getAttribute("url"));
                            //sinon on fait appel a notre fonction
                        else if (firstElement.getTagName().equals("desc")) {
                            String imgNum = firstElement.getTextContent();
                            contentList.add(getImgUrl(id, imgNum));
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
    /**
     * @param contentList une arrayList contenant les valeurs des attributs de l instance d epigraphie qu'on va creer
     * @return une instance de la classe epigraphie apres extractions des valeurs de contentList
     */
    public static Epigraphe CreateEpigraphie(ArrayList<String> contentList)
    {
        Epigraphe epigraphe = Epigraphe.of();
        epigraphe.setId(Integer.parseInt(contentList.get(0)));
        epigraphe.setNom(contentList.get(1));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            epigraphe.setDate(format.parse(contentList.get(2)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        epigraphe.setImgUrl(contentList.get(3));
        epigraphe.setTexte(contentList.get(4));
        epigraphe.setTraduction(contentList.get(5));
        return epigraphe;
    }
}