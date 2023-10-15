package fr.univtln.m1infodid.projet_s2.backend;

import fr.univtln.m1infodid.projet_s2.backend.exceptions.ListeVide;
import fr.univtln.m1infodid.projet_s2.backend.model.Epigraphe;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe SI ...
 */
@Slf4j
public class SI {
    public static final String URL_EPICHERCHELL = "http://ccj-epicherchel.huma-num.fr/interface/fiche_xml2.php?id=";

    private SI () {
    }

    /**
     * @param id        l'id de la fiche
     * @param imgNumber le numéro de l'image, pour gérer les épigraphies à plusieurs images
     * @return l'url de l'image qui sera égale au chemin imgPath + id + imgNumber
     */
    public static String getImgUrl ( String id, String imgNumber ) {
        String imgPath = "http://ccj-epicherchel.huma-num.fr/interface/phototheque/"; //NOSONAR
        return imgPath + id + '/' + imgNumber + ".jpg";
    }

    /**
     * Fonction qui retourne le contenu du fichier xml rechercher à l'aide de son
     * url
     *
     * @param xmlUrl url du fichier XML
     * @return contenu du fichier XML
     */
    public static InputStream getXMLFromUrl ( String xmlUrl ) throws IOException {
        URL url = new URL(xmlUrl);
        return url.openStream();
    }

    /**
     * Fonction qui permet de créer le document xml
     *
     * @param inputStream contenu du fichier XML
     * @return document XML
     */
    public static Document createXMLDoc ( InputStream inputStream )
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(inputStream);
    }

    /**
     * Fonction qui extrait l'image et l'ajoute à la contentList
     *
     * @param contentList liste à laquelle on rajoute le contenu
     * @param nodeList    liste de tous les nœuds du doc XML
     * @param id          id de la fiche
     * @param a           indice de l'élément 'facsimile' dans le nodeList
     */
    public static void extractImage ( List<List<String>> contentList, NodeList nodeList, String id, int a ) {
        Node child = nodeList.item(a + 1);
        Element firstElement = (Element) child;
        // si une seule image existe
        if (firstElement.getTagName().equals("graphic"))
            contentList.add(List.of(firstElement.getAttribute("url")));
        else if (firstElement.getTagName().equals("desc")) {
            String imgNum = firstElement.getTextContent();
            contentList.add(List.of(getImgUrl(id, imgNum)));
        }
    }

    /**
     * Fonction qui permet d'extraire des elements d'un fichier xml et les stocke
     * dans la contentList
     *
     * @param contentList liste pour stocker le contenu extrait
     * @param element     element XML duquel extraire le contenu
     * @param id          id de la fiche
     * @param nodeList    liste de tous les nœuds du doc XML
     * @param a           indice de l'élément dans le nodeList
     */
    public static void extraction ( List<List<String>> contentList, List<String> transcriptionList, Element element,
                                    String id, NodeList nodeList, int a ) {
        switch (element.getTagName()) {
            case "lb" -> transcriptionList.add(nodeList.item(a).getNextSibling().getTextContent());
            case "persName" -> contentList.add(List.of(nodeList.item(a).getTextContent()));
            case "date" -> contentList.add(List.of(((Element) nodeList.item(a)).getAttribute("when")));
            case "facsimile" -> extractImage(contentList, nodeList, id, a);
            default -> { // IGNORE
            }
        }
    }

    /**
     * Fonction qui permet d'extraire le contenu des balises d'un document xml et le
     * stocke dans la contentList
     *
     * @param contentList liste pour stocker le contenu extrait
     * @param doc         document XML
     * @param id          id de la fiche
     * @return liste qui contient le résultat de l'extraction
     */
    public static List<List<String>> extractFromBalise ( List<List<String>> contentList,
                                                         List<String> transcriptionList, Document doc, String id ) {
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("*");
        contentList.add(List.of(id));
        for (int a = 0; a < nodeList.getLength(); a++) {
            Node node = nodeList.item(a);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                extraction(contentList, transcriptionList, element, id, nodeList, a);

                // recupere le contenu de traduction
                if (element.hasAttribute("type")
                        && element.getAttribute("type").equals("translation")) {
                    contentList.add(List.of(element.getTextContent()));
                }
                if (element.hasAttribute("when") && element.getAttribute("when").equals("date")) {
                    contentList.add(List.of(element.getTextContent()));
                }
            }
        }
        contentList.add(transcriptionList);
        return contentList;
    }

    /**
     * Fonction qui permet d'extraire le contenu d'un document xml à partir d'une
     * url et utilise les fonctions précédentes
     *
     * @param id     id de la fiche
     * @param xmlUrl url du document XML
     * @return liste qui contient le contenu des balises
     */
    public static List<List<String>> extractContentFromXML ( String id, String xmlUrl ) {
        List<List<String>> contentList = new ArrayList<>();
        List<String> transcriptionList = new ArrayList<>();
        try {
            InputStream inputStream = getXMLFromUrl(xmlUrl);
            Document doc = createXMLDoc(inputStream);
            extractFromBalise(contentList, transcriptionList, doc, id);
            inputStream.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Err: parsing error");
        }
        return contentList;
    }

    /**
     * Fonction qui permet de renvoyer un object 'epigraphe' en remplissant ses
     * attributs à l'aide de la liste contenant contenu du doc xml
     *
     * @param contentList liste contenant le contenu extrait d'un doc xml
     */
    public static Epigraphe createEpigraphie ( List<List<String>> contentList ) throws ListeVide, ParseException {
        Epigraphe epigraphe = new Epigraphe();
        try {
            if (contentList == null || contentList.isEmpty()) {
                throw new ListeVide();
            }
            parseValue(contentList, epigraphe);
        } catch (IndexOutOfBoundsException e) {
            log.error("Err: erreur dans le parsing de l'epigraphe");
        }
        epigraphe.setFetchDate(LocalDate.now());
        return epigraphe;
    }

    /**
     * Fonction qui permet d'extraire la date de l'epigraphe et la stocke dans
     * l'objet 'Epigraphe'
     *
     * @param contentList liste contenant le contenu d'un doc xml
     * @param epigraphe   object qu'on veut remplir avec le donnees extraites
     */
    public static void parseDate ( List<List<String>> contentList, Epigraphe epigraphe ) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (contentList.get(2).get(0).isEmpty() || contentList.get(2).get(0).isBlank())
            epigraphe.setDate(null);
        else
            epigraphe.setDate(format.parse(contentList.get(2).get(0)));
    }

    /**
     * Fonction qui permet d'extraire les infos de l'epigraphe et les affecte aux
     * attributs de l'objet 'epigraphe'
     *
     * @param contentList liste contenant le contenu d'un doc xml
     * @param epigraphe   object qu'on veut remplir avec le donnees extraites
     */
    public static void parseValue ( List<List<String>> contentList, Epigraphe epigraphe ) throws ParseException {
        epigraphe.setId(Integer.parseInt(contentList.get(0).get(0)));
        epigraphe.setName(contentList.get(1).get(0));
        parseDate(contentList, epigraphe);
        epigraphe.setImgUrl(contentList.get(3).get(0));
        epigraphe.setTranslation(contentList.get(4).get(0));
        epigraphe.setText(contentList.get(5));
    }

    public static Epigraphe createEpigraphie ( int id ) {
        try {
            return SI.createEpigraphie(
                    SI.extractContentFromXML(String.valueOf(id), URL_EPICHERCHELL + id));
        } catch (ListeVide | ParseException e) {
            log.error("Err: creation epigraphe");
            return null;
        }
    }


}
