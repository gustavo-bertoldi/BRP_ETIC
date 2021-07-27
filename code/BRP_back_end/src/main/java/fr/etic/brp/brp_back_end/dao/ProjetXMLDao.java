package fr.etic.brp.brp_back_end.dao;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.ErrorHandler;

/**
 *
 * @author louisrob
 */
public class ProjetXMLDao {
    
    public Document Creer() {
        Document xml = null;
        try {
            DocumentBuilder builder = DomUtil.obtenirBuilder();
            xml = builder.newDocument();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel à la méthode Creer(uri)", ex);
            xml = null;
        }
        return xml;
    }
    
    public Document ObtenirDocument(String uri){
        Document xml = null;
        try {
            DocumentBuilder builder = DomUtil.obtenirBuilder();
            //création de notre objet d'erreurs pour le XML
            ErrorHandler errHandler = new SimpleErrorHandler();
            builder.setErrorHandler(errHandler);
            xml = builder.parse(uri);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel à la méthode ObtenirDocument(uri)", ex);
            xml = null;
        }
        return xml;
    }
    
    public void saveXMLContent(Document xml, String uri){
        System.out.println("saveXMLContent - uri : "+uri);
        
        try {
            Transformer transformer = DomUtil.obtenirTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMImplementation domImpl = xml.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype", "-//Oberon//YOUR PUBLIC DOCTYPE//EN", "reglesProjet.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
            DOMSource domSource = new DOMSource(xml);
            StreamResult streamResult = new StreamResult(uri);
            transformer.transform(domSource, streamResult);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel de la méthode Update", ex);
        }
    }
}