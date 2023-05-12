package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.exceptions.*;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe SI ...
 */
public class SI {
    private static String imgPath = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/";
    public static final String URL_EPICHERCHELL = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=";

    private SI () {
    }


    /**
     * @param id l'id de la fiche
     * @param imgNumber
     * @return l url de l image qui sera egale au chemin imgPath + id + imgNumber
     */
    public static String getImgUrl ( String id, String imgNumber ) {
        return imgPath + id + '/' + imgNumber + ".jpg";
    }


    /**
     * @param xmlUrl url du fichier XML
     * @return contenu du fichier XML
     */
    private static InputStream getXMLFromUrl(String xmlUrl) throws IOException {
        URL url = new URL(xmlUrl);
        return url.openStream();
    }

    /**
     * @param inputStream  contenu du fichier XML
     * @return document XML
     */
    private static Document createXMLDoc(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(inputStream);
    }


    /**
     * @param contentList  liste à laquelle on rajoute le contenu
     * @param nodeList  liste de tous les noeuds du doc XML
     * @param id  id de la fiche
     * @param a  indice de l'element 'facsimile' dans le nodeList
     */
    public static void extractImage(ArrayList<String> contentList, NodeList nodeList, String id, int a){
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


    /**
     * @param contentList  liste pour stocker le contenu extrait
     * @param element  element XML duquel extraire le contenu
     * @param id  id de la fiche
     * @param nodeList  liste de tous les noeuds du doc XML
     * @param a  indice de l'element dans le nodeList
     */
    public static void extraction(ArrayList<String> contentList, Element element, String id, NodeList nodeList, int a){
        switch (element.getTagName()){
            //l'emplacement de texte sur le fichier xml
            case "text":
                contentList.add(nodeList.item(a + 3).getTextContent());break;
            //recherche nom auteur
            case "persName":
                contentList.add(nodeList.item(a).getTextContent());break;
            //recherche date
            case "date":
                contentList.add(((Element) nodeList.item(a)).getAttribute("when"));break;
            //recupere l image
            case "facsimile":
                extractImage(contentList, nodeList, id, a);
                break;
        }
    }



    /**
     * @param contentList  liste pour stocker le contenu extrait
     * @param doc  document XML
     * @param id  id de la fiche
     * @return liste qui contient le résultat de l'extraction
     */
    private static ArrayList<String> extractBaliseContent(ArrayList<String> contentList, Document doc, String id) {
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("*");
        contentList.add(id);
        for (int a = 0; a < nodeList.getLength(); a++) {
            Node node = nodeList.item(a);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                extraction(contentList, element, id, nodeList, a);

                //recupere le contenu de traduction
                if (element.hasAttribute("type") && element.getAttribute("type").equals("translation")) {
                    contentList.add(element.getTextContent());
                }
                if (element.hasAttribute("when") && element.getAttribute("when").equals("date")) {
                    contentList.add(element.getTextContent());
                }
            }
        }
        return contentList;
    }



    /**
     * @param id  id de la fiche
     * @param xmlUrl  url du document XML
     * @return liste qui contient le contenu des balises
     */
    public static ArrayList<String> extractContentFromXML(String id, String xmlUrl) throws Exception {
        ArrayList<String> contentList = new ArrayList<>();
        try {
            InputStream inputStream = getXMLFromUrl(xmlUrl);
            Document doc = createXMLDoc(inputStream);
            extractBaliseContent(contentList,doc,id);
            inputStream.close();
        } catch (MalformedURLException e) {
            throw new UrlInvalide();
        } catch (IOException e) {
            throw new RecuperationXml();
        } catch (ParserConfigurationException | SAXException e) {
            throw new DomParser();
        } catch (Exception e) {
            throw new ExtractionXml();
        }
        return contentList;
    }


    /**
     * @param contentList une arrayList contenant les valeurs des attributs de l instance d epigraphie qu'on va creer
     * @return une instance de la classe epigraphie apres extractions des valeurs de contentList
     */

    public static Epigraphe CreateEpigraphie ( List<String> contentList ) throws ListeVide {
        Epigraphe epigraphe = new Epigraphe();

        try {
            if (contentList == null || contentList.isEmpty()) {
                throw new ListeVide();
            }
            epigraphe.setId(Integer.parseInt(contentList.get(0)));
            epigraphe.setName(contentList.get(1));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                epigraphe.setDate(format.parse(contentList.get(2)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            epigraphe.setImgUrl(contentList.get(3));
            epigraphe.setText(contentList.get(4));
            epigraphe.setTranslation(contentList.get(5));
        } catch (IndexOutOfBoundsException r) {
            throw new ListeVide();
        }
        epigraphe.setFetchDate(LocalDate.now());
        return epigraphe;
    }

    public static Epigraphe CreateEpigraphie ( int id ) throws Exception {
       return SI.CreateEpigraphie(
                SI.extractContentFromXML(String.valueOf(id), URL_EPICHERCHELL + id));
    }
}