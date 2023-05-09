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

	private SI() {
	}

	/**
	 * @param id        l'id de la fiche
	 * @param imgNumber
	 * @return l url de l image qui sera egale au chemin imgPath + id + imgNumber
	 */

	public static String getImgUrl(String id, String imgNumber) {
		return imgPath + id + '/' + imgNumber + ".jpg";
	}

	public static String getFirstImgUrl(String XMLepigraphe) {
		String urlImg = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Document doc = null;
		try {
			doc = builder.parse(new InputSource(new StringReader(XMLepigraphe)));
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("*");
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node node = nodeList.item(a);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				// recupere l image
				if (element.getTagName().equals("facsimile")) {
					Node child = nodeList.item(a + 1);
					Element firstElement = (Element) child;
					// si une seule image existe
					if (firstElement.getTagName().equals("graphic"))
						urlImg = firstElement.getAttribute("url");
					// sinon on fait appel a notre fonction

					else if (firstElement.getTagName().equals("desc")) {
						String imgNum = firstElement.getTextContent();
						Element idNum = (Element) doc.getElementsByTagName("idno").item(0);
						String id = idNum.getTextContent();
						urlImg = getImgUrl(id, imgNum);
					}
				}
			}
		}
		return urlImg;
	}

    /**
     * @param id     l'id de la fiche
     * @param xmlUrl l'url de la fiche
     * @return le contenu des balises image et texte
     */
    public static List<List<String>> extractTextAndImageFromXml ( String id, String xmlUrl ) throws Exception {
        List<List<String>> contentList = new ArrayList<>();
        List<String> transcriptionList = new ArrayList<>();
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
            contentList.add(List.of(id));
            for (int a = 0; a < nodeList.getLength(); a++) {
                Node node = nodeList.item(a);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    //recherche transcription ligne par ligne
                    if (element.getTagName().equals("lb")) {
                        Element lineTranscription = (Element) nodeList.item(a);
                        transcriptionList.add( lineTranscription.getNextSibling().getTextContent() );
                    }
                    //recherche nom auteur
                    if (element.getTagName().equals("persName")) {
                        Element txtElement = (Element) nodeList.item(a);
                        contentList.add(List.of(txtElement.getTextContent()));
                    }
                    //recherche date
                    if (element.getTagName().equals("date")) {
                        Element txtElement = (Element) nodeList.item(a);
                        String whenValue = txtElement.getAttribute("when");
                        contentList.add(List.of(whenValue));
                    }
                    //recupere le contenu de traduction
                    if (element.hasAttribute("type") && element.getAttribute("type").equals("translation")) {
                        contentList.add(List.of(element.getTextContent()));
                    }
                    //recupere le contenu de traduction
                    if (element.hasAttribute("when") && element.getAttribute("when").equals("date")) {
                        contentList.add(List.of(element.getTextContent()));
                    }
                    //recupere l image
                    if (element.getTagName().equals("facsimile")) {
                        Node child = nodeList.item(a + 1);
                        Element firstElement = (Element) child;
                        //si une seule image existe
                        if (firstElement.getTagName().equals("graphic"))
                            contentList.add(List.of(firstElement.getAttribute("url")));
                            //sinon on fait appel a notre fonction
                        else if (firstElement.getTagName().equals("desc")) {
                            String imgNum = firstElement.getTextContent();
                            contentList.add(List.of(getImgUrl(id, imgNum)));
                        }
                    }
                }
            }
            contentList.add(transcriptionList);
            inputStream.close();
        } catch (MalformedURLException e) {
            throw new UrlInvalide();
        } catch (IOException e) {
            throw new RecuperationXml();
        } catch (ParserConfigurationException e) {
            throw new DomParser();
        } catch (SAXException e) {

			throw new SaxErreur();
		} catch (Exception e) {
			throw new ExtractionXml();
		}
		return contentList;
	}

	/**
	 * @param contentList une arrayList contenant les valeurs des attributs de l
	 *                    instance d epigraphie qu'on va creer
	 * @return une instance de la classe epigraphie apres extractions des valeurs de
	 *         contentList
	 */

    public static Epigraphe CreateEpigraphie ( List<List<String>> contentList ) throws ListeVide {
        Epigraphe epigraphe = new Epigraphe();

        try {
            if (contentList == null || contentList.isEmpty()) {
                throw new ListeVide();
            }
            epigraphe.setId(Integer.parseInt(contentList.get(0).get(0)));
            epigraphe.setName(contentList.get(1).get(0));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                if (contentList.get(2).get(0).isEmpty() || contentList.get(2).get(0).isBlank())
                    epigraphe.setDate(null);
                else
                    epigraphe.setDate(format.parse(contentList.get(2).get(0)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            epigraphe.setImgUrl(contentList.get(3).get(0));
            epigraphe.setTranslation(contentList.get(4).get(0));
            epigraphe.setText(contentList.get(5));
        } catch (IndexOutOfBoundsException r) {
            throw new ListeVide();
        }
        epigraphe.setFetchDate(LocalDate.now());
        return epigraphe;
    }

	public static Epigraphe CreateEpigraphie(int id) throws Exception {
		return SI.CreateEpigraphie(
				SI.extractTextAndImageFromXml(String.valueOf(id), URL_EPICHERCHELL + id));
	}
}
