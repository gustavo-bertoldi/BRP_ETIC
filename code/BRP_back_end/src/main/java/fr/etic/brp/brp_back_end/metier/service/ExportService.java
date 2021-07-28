package fr.etic.brp.brp_back_end.metier.service;

import fr.etic.brp.brp_back_end.dao.DomUtil;
import fr.etic.brp.brp_back_end.dao.JpaUtil;
import fr.etic.brp.brp_back_end.dao.ProjetDao;
import fr.etic.brp.brp_back_end.dao.ProjetXMLDao;
import fr.etic.brp.brp_back_end.metier.modele.Projet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.apache.poi.xwpf.usermodel.UnderlinePatterns.DASH;
import static org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Node;

/**
 *
 * @author Quentin MARC & Louis ROB
 */
public class ExportService {
    
    protected ProjetXMLDao projetXMLDao = new ProjetXMLDao();
    protected ProjetDao projetDao = new ProjetDao();
    
    //protected String rootXMLFiles = "../../../../code/BRP_front_end/src/main/webapp/XMLfiles/";
    //protected String rootXMLFiles = "C:\\Users\\brplyon\\Documents\\Gustavo - Debug\\BRP_ETIC\\code\\BRP_front_end\\target\\BRP_front_end-1.0-SNAPSHOT\\XMLfiles"; 
    protected String rootXMLFiles = "/usr/local/tomcat/webapps/BRP_editeur_rapports/XMLfiles/";
    //protected String rootXMLFiles = "/usr/local/Cellar/tomcat/9.0.41/libexec/webapps/BRP_front_end-1.0-SNAPSHOT/XMLfiles/";

    //protected String rootExportFiles = "/usr/local/Cellar/tomcat/9.0.41/libexec/webapps/BRP_front_end-1.0-SNAPSHOT/export_files/";
    //protected String rootExportFiles = "C:\\Users\\brplyon\\Documents\\Gustavo - Debug\\BRP_ETIC\\code\\BRP_front_end\\src\\main\\webapp\\export_files\\";
    protected String rootExportFiles = "/usr/local/tomcat/webapps/BRP_editeur_rapports/export_files/";

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();
    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }
    //Permet la conversion en chiffres romains
    public final static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }
    
    public ArrayList<String> ExporterProjet(Long idProjet, int choixTemplate, String uriXML) {
       
        DomUtil.init();
        ArrayList<String> resultat = new ArrayList<>();

        Map<String, String> template1 = new HashMap<>();
        template1.put("titre1", "Titre1");
        template1.put("titre2", "Titre2");
        template1.put("titre3", "Titre3");
        template1.put("titre4", "Titre4");
        template1.put("titre5", "Titre5");
        Map<String, String> template1Immutable = Collections.unmodifiableMap(template1);
        //Coder ici les différentes templates de style WORD suivant le modèle ci-dessus
        
        //Utile quand on veut RM le dossier si erreur
        String nomProjet = null;
        Boolean dossierCree = false;

        try {
            //Obtention du document XML
            Document xml = projetXMLDao.ObtenirDocument(uriXML);
            
            //On insère les infos projets dans le XML
            JpaUtil.creerContextePersistance();
            
            Projet projet = projetDao.ChercherParId(idProjet);
            
            JpaUtil.fermerContextePersistance();
            
            Element baliseNomprojet = xml.createElement("nomProjet");
            baliseNomprojet.setTextContent(projet.getNomProjet());
            Element baliseTypeMarche = xml.createElement("typeMarche");
            if(projet.getTypeMarche() != null)
                baliseTypeMarche.setTextContent(projet.getTypeMarche().toString());
            Element baliseTypeConstruction = xml.createElement("typeConstruction");
            if(projet.getTypeConstruction() != null)
                baliseTypeConstruction.setTextContent(projet.getTypeConstruction().toString());
            Element baliseTypeLot = xml.createElement("typeLot");
            if(projet.getTypeLot() != null)
                baliseTypeLot.setTextContent(projet.getTypeLot().toString());
            Element baliseSite = xml.createElement("site");
            if(projet.getSite() != null)
                baliseSite.setTextContent(projet.getSite().toString());
            Element baliseDatePrixRef = xml.createElement("datePrixRef");
            if(projet.getDatePrixRef() != null)
                baliseDatePrixRef.setTextContent(projet.getDatePrixRef().toString());
            Element baliseCoeffAdapt = xml.createElement("coeffAdapt");
            if(projet.getCoeffAdapt() != null)
                baliseCoeffAdapt.setTextContent(projet.getCoeffAdapt().toString());
            Element baliseCoeffRaccordement = xml.createElement("coeffRaccordement");
            Element baliseLocalisationCoeffRaccordement = xml.createElement("localisationCoeffRaccordement");
            if(projet.getCoeffRaccordement() != null)
                baliseLocalisationCoeffRaccordement.setTextContent(projet.getCoeffRaccordement().getLocalisation());
            Element baliseValeurCoeffRaccordement = xml.createElement("valeurCoeffRaccordement");
            if(projet.getCoeffRaccordement() != null)
                baliseValeurCoeffRaccordement.setTextContent(projet.getCoeffRaccordement().getValeur().toString());
            baliseCoeffRaccordement.appendChild(baliseLocalisationCoeffRaccordement);
            baliseCoeffRaccordement.appendChild(baliseValeurCoeffRaccordement);
            Element baliseCategorieConstruction = xml.createElement("categorieConstruction");
            Element baliseCategorieConstructionIntitule = xml.createElement("intituleCategorieConstruction");
            if(projet.getCategorieConstruction() != null)
                baliseCategorieConstructionIntitule.setTextContent(projet.getCategorieConstruction().getIntituleCategorieConstruction());
            Element baliseCategorieConstructionCode = xml.createElement("codeCategorieConstruction");
            if(projet.getCategorieConstruction() != null)
                baliseCategorieConstructionCode.setTextContent(projet.getCategorieConstruction().getCodeCategorieConstruction());
            baliseCategorieConstruction.appendChild(baliseCategorieConstructionIntitule);
            baliseCategorieConstruction.appendChild(baliseCategorieConstructionCode);
            Element baliseSousCategorieConstruction = xml.createElement("sousCategorieConstruction");
            if(projet.getSousCategorieConstructionSelection() != null)
                baliseSousCategorieConstruction.setTextContent(projet.getSousCategorieConstructionSelection().getIntituleSousCategorieConstruction());
            Element baliseCaractDim = xml.createElement("caractDim");
            Element baliseCaractDimCode = xml.createElement("codeCaractDim");
            if(projet.getCaractDimSelection() != null)
                baliseCaractDim.setTextContent(projet.getCaractDimSelection().getCodeCaractDim());
            Element baliseCaractDimValeur = xml.createElement("valeurCaractDim");
            if(projet.getCaractDimSelection() != null)
                baliseCaractDimValeur.setTextContent(projet.getCaractDimSelection().getValeur().toString());
            baliseCaractDim.appendChild(baliseCaractDimCode);
            baliseCaractDim.appendChild(baliseCaractDimValeur);
            
            Node baliseLotInfosProjet = xml.getElementsByTagName("lot").item(0);
            Element root = xml.getDocumentElement();
            root.insertBefore(baliseNomprojet, baliseLotInfosProjet);
            root.insertBefore(baliseTypeMarche, baliseLotInfosProjet);
            root.insertBefore(baliseTypeConstruction, baliseLotInfosProjet);
            root.insertBefore(baliseTypeLot, baliseLotInfosProjet);
            root.insertBefore(baliseSite, baliseLotInfosProjet);
            root.insertBefore(baliseDatePrixRef, baliseLotInfosProjet);
            root.insertBefore(baliseCoeffAdapt, baliseLotInfosProjet);
            root.insertBefore(baliseCoeffRaccordement, baliseLotInfosProjet);
            root.insertBefore(baliseCategorieConstruction, baliseLotInfosProjet);
            root.insertBefore(baliseSousCategorieConstruction, baliseLotInfosProjet);
            root.insertBefore(baliseCaractDim, baliseLotInfosProjet);
            
            //On écrit par dessus l'ancien XML
            projetXMLDao.saveXMLContent(xml, uriXML);
            
            //on supprime si existe avant
            nomProjet = projet.getNomProjet();
            
            File index = new File(rootExportFiles+"Exports/"+ nomProjet + "_" + idProjet);
            if(index.exists()){
                String[] entries = index.list();
                for(String s: entries){
                    File currentFile = new File(index.getPath(),s);
                    currentFile.delete();
                }
                index.delete();
            }

            //On créer le dossier d'export du Projet
            final String cheminNouveauDossier = rootExportFiles+"Exports/"+ nomProjet + "_" + idProjet;
            Boolean succesCreationDossier = (new File(cheminNouveauDossier)).mkdirs();
            if (!succesCreationDossier) {
                throw new Exception("ExporterProjet - Echec lors de la création du dossier "+cheminNouveauDossier);
            }

            // Boolean succesCreationDossier = false;
            // File folder = new File(rootExportFiles+"Exports/"+ nomProjet + "_" + idProjet);
            // if (!folder.exists()) {
            //     succesCreationDossier = folder.mkdirs();
            // } else {
            //     folder.delete();
            //     if (!folder.exists()) {
            //         succesCreationDossier = folder.mkdirs();
            //     }
            // }

            // if (!succesCreationDossier) {
            //     throw new Exception();
            // }
            
            //TRAITEMENT WORD
            //Pour chaque lot
            NodeList listeLot = xml.getElementsByTagName("lot");
            for(int h = 0; h < listeLot.getLength(); h++){
                //Choix de la template et création du document WORD en fonction
                Map<String, String> template = template1Immutable;
                XWPFDocument word;
                switch(choixTemplate) {
                    case 1:
                        template = template1Immutable;
                        word = new XWPFDocument(new FileInputStream(rootExportFiles+"TemplatesWord/Template1/Template1_CCTP.docx"));
                        break;
                    default :
                        throw new Exception(); //Template non reconnue
                }
                
                Element baliseLot = (Element)listeLot.item(h);  //on choppe le lot
                
                //on met le nom du lot dans la page de garde
                String nomLot = baliseLot.getAttribute("intitule");      //on détermine le nom à mettre
                //on extrait le paragrpahe ou sera positionné le nom
                List<XWPFParagraph> listPara = word.getParagraphs();
                for(int i = 0; i < listPara.size(); i++){
                    if(listPara.get(i).getText().equals("Rapport XXX")){
                        XWPFParagraph paraNomLot = listPara.get(i);
                        List<XWPFRun> listRun = paraNomLot.getRuns();
                        //on vide le paragraphe
                        for(int j = 0; j < listRun.size(); j++){
                            if (j == 0)
                                //on insère le nom
                                listRun.get(j).setText(nomLot, 0);
                            else
                                listRun.get(j).setText("", 0);
                        }
                    }

                    if(listPara.get(i).getText().equals("TITRE MISSION")){
                        XWPFParagraph paraNomProjet = listPara.get(i);
                        List<XWPFRun> listRun = paraNomProjet.getRuns();
                        //on vide le paragraphe
                        for(int j = 0; j < listRun.size(); j++){
                            if (j == 0)
                                //on insère le nom
                                listRun.get(j).setText("Lot_"+ h +": "+projet.getNomProjet(), 0);  
                            else
                                listRun.get(j).setText("", 0);
                        }
                    }
                }
                
                //Pour chaque titre1
                NodeList listeTitre1 = baliseLot.getElementsByTagName("titre1");
                for(int i = 0; i < listeTitre1.getLength(); i++){
                    //On insère le titre1 
                    XWPFParagraph paragraphTitre1 = word.createParagraph();
                    paragraphTitre1.setStyle(template.get("titre1"));
                    XWPFRun runTitre1 = paragraphTitre1.createRun();
                    Element titre1 = (Element)listeTitre1.item(i);
                    runTitre1.setText(titre1.getAttribute("intitule"));

                    //Pour chaque enfant d'un titre1
                    NodeList enfantsTitre1 = listeTitre1.item(i).getChildNodes();
                    for(int j = 0; j < enfantsTitre1.getLength(); j++) {
                        if(enfantsTitre1.item(j).getNodeName().equals("titre2")) {
                            //On insère le titre2
                            XWPFParagraph paragraphTitre2 = word.createParagraph();
                            paragraphTitre2.setStyle(template.get("titre2"));
                            XWPFRun runTitre2 = paragraphTitre2.createRun();
                            Element titre2 = (Element)enfantsTitre1.item(j);
                            runTitre2.setText(titre2.getAttribute("intitule"));

                            //Pour chaque enfant d'un titre2
                            NodeList enfantsTitre2 = enfantsTitre1.item(j).getChildNodes();
                            for(int k = 0; k < enfantsTitre2.getLength(); k++) {
                                if(enfantsTitre2.item(k).getNodeName().equals("titre3")) {
                                    //On insère le titre3
                                    XWPFParagraph paragraphTitre3 = word.createParagraph();
                                    paragraphTitre3.setStyle(template.get("titre3"));
                                    XWPFRun runTitre3 = paragraphTitre3.createRun();
                                    Element titre3 = (Element)enfantsTitre2.item(k);
                                    runTitre3.setText(titre3.getAttribute("intitule"));
                                    
                                    //Pour chaque enfant d'un titre3
                                    NodeList enfantsTitre3 = enfantsTitre2.item(k).getChildNodes();
                                    for(int l = 0; l < enfantsTitre3.getLength(); l++) {
                                        if(enfantsTitre3.item(l).getNodeName().equals("titre4")) {
                                            //On insère le titre4
                                            XWPFParagraph paragraphTitre4 = word.createParagraph();
                                            paragraphTitre4.setStyle(template.get("titre4"));
                                            XWPFRun runTitre4 = paragraphTitre4.createRun();
                                            Element titre4 = (Element)enfantsTitre3.item(l);
                                            runTitre4.setText(titre4.getAttribute("intitule"));

                                            //Pour chaque enfant d'un titre4
                                            NodeList enfantsTitre4 = enfantsTitre3.item(l).getChildNodes();
                                            for(int m = 0; m < enfantsTitre4.getLength(); m++) {
                                                //descriptif
                                                if(enfantsTitre4.item(m).getNodeName().equals("descriptif"))
                                                    word = ExtractionDescriptif((Element)enfantsTitre4.item(m), template.get("titre5"), word);
                                            }
                                        } 
                                        if(enfantsTitre3.item(l).getNodeName().equals("descriptif")){
                                            //descriptif
                                            word = ExtractionDescriptif((Element)enfantsTitre3.item(l), template.get("titre4"), word);
                                        }
                                    }
                                } 
                                if(enfantsTitre2.item(k).getNodeName().equals("descriptif")){
                                    //descriptif
                                    word = ExtractionDescriptif((Element)enfantsTitre2.item(k), template.get("titre3"), word);
                                }
                            }
                        } 
                        if(enfantsTitre1.item(j).getNodeName().equals("descriptif")){
                            //descriptif
                            word = ExtractionDescriptif((Element)enfantsTitre1.item(j), template.get("titre2"), word);
                        }
                    }
                }
                //On nomme la CCTP
                String outputCCTP = rootExportFiles+"Exports/" + projet.getNomProjet() + "_" + projet.getIdProjet() + "/" + projet.getNomProjet() + "_LOT_" + h + "_" + baliseLot.getAttribute("intitule") + ".docx"; //Surement à changer lors de l'installation client
                //On écrit en sortie le document WORD
                FileOutputStream out = new FileOutputStream(outputCCTP);
                word.write(out);
                out.close();
                word.close();
                resultat.add(outputCCTP);
            }
            
            //TRAITEMENT EXCEL
            //Création du document EXCEL
            Workbook excel = new XSSFWorkbook(new FileInputStream(rootExportFiles+"TemplatesExcel/Template1/Template1_DPGF.xlsx"));
            CreationHelper createHelper = excel.getCreationHelper(); //Permet de créer le document "plus simplement"
            
            //Pour chaque lot
            for(int i = 0; i < listeLot.getLength(); i++){
                //S'il existe un ouvrage/prestation dans le lot
                Element lotBalise = (Element)listeLot.item(i);
                if(ContientOuvrageOuPrestation(lotBalise)) {
                    //On créer un nouveau lot (format nom : LOT_1_intitule)
                    Sheet sheet = excel.createSheet("Lot_"+ (i+1) + "_" + lotBalise.getAttribute("intitule"));
                    //On créer l'en-tête lot (ligne grise)
                    Row enTeteLotLigneGrise = sheet.createRow(0);
                    CellStyle styleEnTeteLotLigneGrise = excel.createCellStyle();
                    styleEnTeteLotLigneGrise.setAlignment(HorizontalAlignment.CENTER);
                    ((XSSFCellStyle)styleEnTeteLotLigneGrise).setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 217, 217)));
                    styleEnTeteLotLigneGrise.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    styleEnTeteLotLigneGrise = createBorderedStyle(styleEnTeteLotLigneGrise);
                    Font fontEnTeteLigneGrise = excel.createFont();
                    fontEnTeteLigneGrise.setBold(true);
                    styleEnTeteLotLigneGrise.setFont(fontEnTeteLigneGrise);
                    enTeteLotLigneGrise.createCell(0).setCellValue(createHelper.createRichTextString("N° art. CCTP"));
                    enTeteLotLigneGrise.getCell(0).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(1).setCellValue(createHelper.createRichTextString("DESIGNATION"));
                    enTeteLotLigneGrise.getCell(1).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(2).setCellValue(createHelper.createRichTextString("DESCRIPTION SOMMAIRE \n" + "(se référer à l'article du CCTP)"));
                    enTeteLotLigneGrise.getCell(2).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(3).setCellValue(createHelper.createRichTextString("U"));
                    enTeteLotLigneGrise.getCell(3).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(4).setCellValue(createHelper.createRichTextString("Q"));
                    enTeteLotLigneGrise.getCell(4).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(5).setCellValue(createHelper.createRichTextString("PU"));
                    enTeteLotLigneGrise.getCell(5).setCellStyle(styleEnTeteLotLigneGrise);
                    enTeteLotLigneGrise.createCell(6).setCellValue(createHelper.createRichTextString("Montant HT"));
                    enTeteLotLigneGrise.getCell(6).setCellStyle(styleEnTeteLotLigneGrise);
                    
                    //Pour chaque titre1
                    NodeList listeTitre1 = lotBalise.getChildNodes();
                    for(int j = 0; j < listeTitre1.getLength(); j++) {
                        //Créer l'en-tête titre1 (ligne bleue)
                        Row enTeteTitre1LigneBleue = sheet.createRow(sheet.getLastRowNum()+1);
                        Font fontEnTeteTitre1LigneBleue = excel.createFont();
                        fontEnTeteTitre1LigneBleue.setBold(true);
                        fontEnTeteTitre1LigneBleue.setFontHeightInPoints((short)14);
                        CellStyle styleCellTitre1 = excel.createCellStyle();
                        ((XSSFCellStyle)styleCellTitre1).setFillForegroundColor(new XSSFColor(new java.awt.Color(141, 180, 226)));
                        styleCellTitre1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleCellTitre1.setAlignment(HorizontalAlignment.LEFT);
                        styleCellTitre1.setFont(fontEnTeteTitre1LigneBleue);
                        styleCellTitre1 = createBorderedStyle(styleCellTitre1);
                        Element titre1Balise = (Element)listeTitre1.item(j);
                        enTeteTitre1LigneBleue.createCell(0).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(j+1)));                        
                        enTeteTitre1LigneBleue.createCell(1).setCellValue(createHelper.createRichTextString(titre1Balise.getAttribute("intitule")));
                        enTeteTitre1LigneBleue.getCell(1).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.createCell(2).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.createCell(3).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.createCell(4).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.createCell(5).setCellStyle(styleCellTitre1);
                        enTeteTitre1LigneBleue.createCell(6).setCellStyle(styleCellTitre1);
                        sheet.addMergedRegion(new CellRangeAddress(
                                sheet.getLastRowNum(), //first row (0-based)
                                sheet.getLastRowNum(), //last row  (0-based)
                                1, //first column (0-based)
                                6  //last column  (0-based)
                        ));
                        //Pour chaque enfants titre1
                        NodeList listeEnfantsTitre1 = titre1Balise.getChildNodes();
                        int nbLigneEffectiveTitre2 = 0;
                        for(int k = 0; k < listeEnfantsTitre1.getLength(); k++) {
                            //Si Ouvrage ou Prestation
                            if(listeEnfantsTitre1.item(k).getNodeName().equals("descriptif")) {
                                Element descriptifBalise = (Element)listeEnfantsTitre1.item(k);
                                if(descriptifBalise.getAttribute("type").equals("ouvrage") || descriptifBalise.getAttribute("type").equals("prestation")) {
                                    nbLigneEffectiveTitre2++;
                                    excel = DPGFDescriptif(excel, i+1, descriptifBalise, createHelper, j, nbLigneEffectiveTitre2, -1, -1);
                                }
                            //Sinon si titre2
                            } else if(listeEnfantsTitre1.item(k).getNodeName().equals("titre2")) {
                                nbLigneEffectiveTitre2++;
                                //Créer l'en-tête titre2 (ligne blanche, ligne grise prononcée)
                                Row ligneVideTitre2 = sheet.createRow(sheet.getLastRowNum()+1);
                                CellStyle styleBorderLR = createBorderedStyleLR(excel.createCellStyle());
                                ligneVideTitre2.createCell(0).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(1).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(2).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(3).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(4).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(5).setCellStyle(styleBorderLR);
                                ligneVideTitre2.createCell(6).setCellStyle(styleBorderLR);
                                Row ligneGriseTitre2 = sheet.createRow(sheet.getLastRowNum()+1);
                                CellStyle styleCellTitre2 = createBorderedStyleLR(excel.createCellStyle());
                                Font bold12 = excel.createFont();
                                bold12.setBold(true);
                                styleCellTitre2.setFont(bold12);
                                ((XSSFCellStyle)styleCellTitre2).setFillForegroundColor(new XSSFColor(new java.awt.Color(191, 191, 191)));
                                styleCellTitre2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                styleCellTitre2.setAlignment(HorizontalAlignment.LEFT);
                                ligneGriseTitre2.createCell(0).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(1).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(2).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(3).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(4).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(5).setCellStyle(styleCellTitre2);
                                ligneGriseTitre2.createCell(6).setCellStyle(styleCellTitre2);
                                Element titre2Balise = (Element)listeEnfantsTitre1.item(k);
                                ligneGriseTitre2.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(j+1) + "." + (nbLigneEffectiveTitre2)));
                                ligneGriseTitre2.getCell(1).setCellValue(createHelper.createRichTextString(titre2Balise.getAttribute("intitule")));
                                //Pour chaque enfants titre2
                                NodeList listeEnfantsTitre2 = titre2Balise.getChildNodes();
                                int nbLigneEffectiveTitre3 = 0;
                                for(int l = 0; l < listeEnfantsTitre2.getLength(); l++) {
                                    //Si Ouvrage ou Prestation
                                    if(listeEnfantsTitre2.item(l).getNodeName().equals("descriptif")) {
                                        nbLigneEffectiveTitre3++;
                                        Element descriptifBalise = (Element)listeEnfantsTitre2.item(l);
                                        if(descriptifBalise.getAttribute("type").equals("ouvrage") || descriptifBalise.getAttribute("type").equals("prestation")) {
                                            excel = DPGFDescriptif(excel, i+1, descriptifBalise, createHelper, j, nbLigneEffectiveTitre2, nbLigneEffectiveTitre3, -1); 
                                        }
                                    //Sinon si titre3
                                    } else if(listeEnfantsTitre2.item(l).getNodeName().equals("titre3")) {
                                        nbLigneEffectiveTitre3++;
                                        //Créer l'en-tête titre3 (ligne grise moins prononcée)
                                        Row ligneGriseTitre3 = sheet.createRow(sheet.getLastRowNum()+1);
                                        CellStyle styleCellTitre3 = createBorderedStyleLR(excel.createCellStyle());
                                        Font bold11 = excel.createFont();
                                        bold11.setBold(true);
                                        styleCellTitre3.setFont(bold11);
                                        ((XSSFCellStyle)styleCellTitre3).setFillForegroundColor(new XSSFColor(new java.awt.Color(242, 242, 242)));
                                        styleCellTitre3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                                        styleCellTitre3.setAlignment(HorizontalAlignment.LEFT);
                                        ligneGriseTitre3.createCell(0).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(1).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(2).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(3).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(4).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(5).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.createCell(6).setCellStyle(styleCellTitre3);
                                        ligneGriseTitre3.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(j+1) + "." + nbLigneEffectiveTitre2 + "." + nbLigneEffectiveTitre3));
                                        Element titre3Balise = (Element)listeEnfantsTitre2.item(l);
                                        ligneGriseTitre3.getCell(1).setCellValue(createHelper.createRichTextString(titre3Balise.getAttribute("intitule")));
                                        //Pour chaque enfants titre3
                                        NodeList listeEnfantsTitre3 = titre3Balise.getChildNodes();
                                        int nbLigneEffectiveTitre4 = 0;
                                        for(int m = 0; m < listeEnfantsTitre3.getLength(); m++) {
                                            //Si Ouvrage ou Prestation
                                            if(listeEnfantsTitre3.item(m).getNodeName().equals("descriptif")) {
                                                nbLigneEffectiveTitre4++;
                                                Element descriptifBalise = (Element)listeEnfantsTitre3.item(m);
                                                if(descriptifBalise.getAttribute("type").equals("ouvrage") || descriptifBalise.getAttribute("type").equals("prestation")) {
                                                    excel = DPGFDescriptif(excel, i+1, descriptifBalise, createHelper, j, nbLigneEffectiveTitre2, nbLigneEffectiveTitre3, nbLigneEffectiveTitre4);
                                                }
                                            }
                                        }
                                        //Créer tuple récap titre3 ("SOUS-TOTAL [intitulé tite3]", prix)
                                        excel = RecapTitre(excel, i+1, titre3Balise, styleCellTitre3, createHelper);
                                    }
                                }
                                //Créer ligne blanche
                                Row ligneVide = sheet.createRow(sheet.getLastRowNum()+1);
                                ligneVide.createCell(0).setCellStyle(styleBorderLR);
                                ligneVide.createCell(1).setCellStyle(styleBorderLR);
                                ligneVide.createCell(2).setCellStyle(styleBorderLR);
                                ligneVide.createCell(3).setCellStyle(styleBorderLR);
                                ligneVide.createCell(4).setCellStyle(styleBorderLR);
                                ligneVide.createCell(5).setCellStyle(styleBorderLR);
                                ligneVide.createCell(6).setCellStyle(styleBorderLR);
                                //Créer tuple récap titre2 ("SOUS-TOTAL [intitulé tite2]", prix)
                                excel = RecapTitre(excel, i+1, titre2Balise, styleCellTitre2, createHelper);
                            }
                        }
                        //Créer tuple récap titre1 ("SOUS-TOTAL [intitulé tite1]", prix)
                        excel = RecapTitre(excel, i+1, titre1Balise, styleCellTitre1, createHelper);
                        //Créer ligne blanche
                        sheet.createRow(sheet.getLastRowNum()+1);
                    }
                    //Créer 2 lignes vides
                    for(int q = 0; q < 1; q++)
                       sheet.createRow(sheet.getLastRowNum()+1);
                    //Créer RECAPITULATIF [n°LOT]
                    Row ligneRecapEnTete = sheet.createRow(sheet.getLastRowNum()+1);
                    Font fontEnTeteRecap = excel.createFont();
                    fontEnTeteRecap.setBold(true);
                    fontEnTeteRecap.setFontHeightInPoints((short)12);
                    CellStyle styleEnTeteRecap = excel.createCellStyle();
                    styleEnTeteRecap = createBoldBorderedStyle(styleEnTeteRecap);
                    styleEnTeteRecap.setFont(fontEnTeteRecap);
                    ligneRecapEnTete.createCell(0).setCellValue(createHelper.createRichTextString("RECAPITULATIF " + lotBalise.getAttribute("intitule")));
                    ligneRecapEnTete.getCell(0).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(1).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(2).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(3).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(4).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(5).setCellStyle(styleEnTeteRecap);
                    ligneRecapEnTete.createCell(6).setCellStyle(styleEnTeteRecap);
                    sheet.addMergedRegion(new CellRangeAddress(
                        sheet.getLastRowNum(), //first row (0-based)
                        sheet.getLastRowNum(), //last row  (0-based)
                        0, //first column (0-based)
                        6  //last column  (0-based)
                    ));
                    //Pour chaque titre1
                    for(int n = 0; n < listeTitre1.getLength(); n++) {
                        //Créer ligne bleue avec [intitulé titre1]
                        Row ligneRecapTitre1EnTete = sheet.createRow(sheet.getLastRowNum()+1);
                        CellStyle styleEnTeteRecapTitre1 = excel.createCellStyle();
                        styleEnTeteRecapTitre1 = createBoldBorderedStyle(styleEnTeteRecapTitre1);
                        ((XSSFCellStyle)styleEnTeteRecapTitre1).setFillForegroundColor(new XSSFColor(new java.awt.Color(141, 180, 226)));
                        styleEnTeteRecapTitre1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        styleEnTeteRecapTitre1.setAlignment(HorizontalAlignment.CENTER);
                        styleEnTeteRecapTitre1.setFont(fontEnTeteRecap);
                        Element titre1Balise = (Element)listeTitre1.item(n);
                        ligneRecapTitre1EnTete.createCell(0).setCellValue(createHelper.createRichTextString(titre1Balise.getAttribute("intitule")));
                        ligneRecapTitre1EnTete.getCell(0).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(1).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(2).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(3).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(4).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(5).setCellStyle(styleEnTeteRecapTitre1);
                        ligneRecapTitre1EnTete.createCell(6).setCellStyle(styleEnTeteRecapTitre1);
                        sheet.addMergedRegion(new CellRangeAddress(
                            sheet.getLastRowNum(), //first row (0-based)
                            sheet.getLastRowNum(), //last row  (0-based)
                            0, //first column (0-based)
                            6  //last column  (0-based)
                        ));
                        //Créer ligne (N°, DESIGNATION, PRIX TOTAL)
                        Row ligneEnTeteRecapTitre2 = sheet.createRow(sheet.getLastRowNum()+1);
                        CellStyle styleEnTeteRecaptitre2 = excel.createCellStyle();
                        styleEnTeteRecaptitre2.setFont(fontEnTeteRecap);
                        styleEnTeteRecaptitre2 = createBorderedStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(1).setCellValue(createHelper.createRichTextString("DESIGNATION"));
                        ligneEnTeteRecapTitre2.getCell(1).setCellStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(2).setCellStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(3).setCellStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(4).setCellStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(5).setCellStyle(styleEnTeteRecaptitre2);
                        sheet.addMergedRegion(new CellRangeAddress(
                            sheet.getLastRowNum(), //first row (0-based)
                            sheet.getLastRowNum(), //last row  (0-based)
                            1, //first column (0-based)
                            5  //last column  (0-based)
                        ));
                        styleEnTeteRecaptitre2.setAlignment(HorizontalAlignment.CENTER);
                        ligneEnTeteRecapTitre2.createCell(0).setCellValue(createHelper.createRichTextString("N°"));
                        ligneEnTeteRecapTitre2.getCell(0).setCellStyle(styleEnTeteRecaptitre2);
                        ligneEnTeteRecapTitre2.createCell(6).setCellValue(createHelper.createRichTextString("PRIX TOTAL"));
                        ligneEnTeteRecapTitre2.getCell(6).setCellStyle(styleEnTeteRecaptitre2);
                        //Pour chaque enfants titre2
                        NodeList enfantsTitre2 = titre1Balise.getChildNodes();
                        int nbTitre2Effectif = 0; //Sinon p compte aussi les \n
                        for(int p = 0; p < enfantsTitre2.getLength(); p++) {
                            if(enfantsTitre2.item(p).getNodeName().equals("titre2") || (enfantsTitre2.item(p).getNodeName().equals("descriptif"))) {
                                Element titre2Balise = (Element)enfantsTitre2.item(p);
                                if(enfantsTitre2.item(p).getNodeName().equals("titre2") || (enfantsTitre2.item(p).getNodeName().equals("descriptif") && !titre2Balise.getAttribute("type").equals("generique"))) {
                                    nbTitre2Effectif++;
                                    //Tuple correspondant au sous-total titre2
                                    Row ligneSousTotalTitre2 = sheet.createRow(sheet.getLastRowNum()+1);
                                    CellStyle styleSousTotalTitre2 = excel.createCellStyle();
                                    Font fontSousTotalTitre2 = excel.createFont();
                                    //styleSousTotalTitre2.setFont(fontEnTeteRecap);
                                    fontSousTotalTitre2.setFontHeightInPoints((short)11);
                                    styleSousTotalTitre2.setFont(fontSousTotalTitre2);
                                    styleSousTotalTitre2 = createBorderedStyle(styleSousTotalTitre2);

                                    DataFormat format = excel.createDataFormat();
                                    CellStyle styleSousTotalTitre2Price = styleSousTotalTitre2;     
                                    styleSousTotalTitre2Price.setDataFormat(format.getFormat("#,##0.00€")); 

                                    if(titre2Balise.hasAttribute("intitule"))
                                        ligneSousTotalTitre2.createCell(1).setCellValue(createHelper.createRichTextString(titre2Balise.getAttribute("intitule")));
                                    else {
                                        Element nomDescriptifBalise = (Element) titre2Balise.getChildNodes().item(0);
                                        ligneSousTotalTitre2.createCell(1).setCellValue(createHelper.createRichTextString(nomDescriptifBalise.getTextContent()));
                                    }
                                    ligneSousTotalTitre2.getCell(1).setCellStyle(styleSousTotalTitre2);
                                    ligneSousTotalTitre2.createCell(2).setCellStyle(styleSousTotalTitre2);
                                    ligneSousTotalTitre2.createCell(3).setCellStyle(styleSousTotalTitre2);
                                    ligneSousTotalTitre2.createCell(4).setCellStyle(styleSousTotalTitre2);
                                    ligneSousTotalTitre2.createCell(5).setCellStyle(styleSousTotalTitre2);
                                    sheet.addMergedRegion(new CellRangeAddress(
                                        sheet.getLastRowNum(), //first row (0-based)
                                        sheet.getLastRowNum(), //last row  (0-based)
                                        1, //first column (0-based)
                                        5  //last column  (0-based)
                                    ));
                                    styleSousTotalTitre2.setAlignment(HorizontalAlignment.CENTER);
                                    ligneSousTotalTitre2.createCell(0).setCellValue(createHelper.createRichTextString(toRoman(n+1) + "." + (nbTitre2Effectif)));
                                    ligneSousTotalTitre2.getCell(0).setCellStyle(styleSousTotalTitre2);
                                    ligneSousTotalTitre2.createCell(6).setCellValue(CalculSousTotal(titre2Balise));
                                    ligneSousTotalTitre2.getCell(6).setCellStyle(styleSousTotalTitre2Price);
                                }
                            }
                        }
                        //Créer ligne MONTANT TOTAL H.T [intitulé Titre1]
                        Row ligneMontantTotalHT = sheet.createRow(sheet.getLastRowNum()+1);
                        Font bold11 = excel.createFont();
                        bold11.setBold(true);
                        CellStyle styleMontantTotalHT = excel.createCellStyle();
                        styleMontantTotalHT.setFont(bold11);
                        
                        DataFormat format = excel.createDataFormat();
                        CellStyle styleMontantTotalHTPrice = styleMontantTotalHT;
                        styleMontantTotalHTPrice.setDataFormat(format.getFormat("#,##0.00€")); 

                        styleMontantTotalHT.setAlignment(HorizontalAlignment.RIGHT);
                        styleMontantTotalHT = createBorderedStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(0).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(1).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(2).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(3).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(4).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(5).setCellStyle(styleMontantTotalHT);
                        ligneMontantTotalHT.createCell(6).setCellStyle(styleMontantTotalHTPrice);
                        sheet.addMergedRegion(new CellRangeAddress(
                                sheet.getLastRowNum(), //first row (0-based)
                                sheet.getLastRowNum(), //last row  (0-based)
                                0, //first column (0-based)
                                5  //last column  (0-based)
                        ));
                        ligneMontantTotalHT.getCell(0).setCellValue(createHelper.createRichTextString("MONTANT TOTAL H.T " + titre1Balise.getAttribute("intitule")));
                        ligneMontantTotalHT.getCell(6).setCellValue(CalculSousTotal(titre1Balise));
                        //créer ligne blanche
                        sheet.createRow(sheet.getLastRowNum()+1);
                    }
                    //Créer ligne grise bien prononcée MONTANT TOTAL H.T [LOT 1]
                    Row ligneMontantTotalHTLot = sheet.createRow(sheet.getLastRowNum()+1);
                    Font bold11 = excel.createFont();
                    bold11.setBold(true);
                    CellStyle styleMontantTotalHTLot = excel.createCellStyle();
                    styleMontantTotalHTLot.setFont(bold11);

                    DataFormat format = excel.createDataFormat();
                    CellStyle styleMontantTotalHTLotPrice = styleMontantTotalHTLot;
                    styleMontantTotalHTLotPrice.setDataFormat(format.getFormat("#,##0.00€")); 

                    styleMontantTotalHTLot.setAlignment(HorizontalAlignment.RIGHT);
                    ((XSSFCellStyle)styleMontantTotalHTLot).setFillForegroundColor(new XSSFColor(new java.awt.Color(166, 166, 166)));
                    styleMontantTotalHTLot.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    styleMontantTotalHTLot = createBorderedStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(0).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(1).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(2).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(3).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(4).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(5).setCellStyle(styleMontantTotalHTLot);
                    ligneMontantTotalHTLot.createCell(6).setCellStyle(styleMontantTotalHTLotPrice);
                    sheet.addMergedRegion(new CellRangeAddress(
                            sheet.getLastRowNum(), //first row (0-based)
                            sheet.getLastRowNum(), //last row  (0-based)
                            0, //first column (0-based)
                            5  //last column  (0-based)
                    ));
                    ligneMontantTotalHTLot.getCell(0).setCellValue(createHelper.createRichTextString("MONTANT TOTAL H.T " + lotBalise.getAttribute("intitule")));
                    ligneMontantTotalHTLot.getCell(6).setCellValue(CalculSousTotal(lotBalise));
                    //Créer une ligne blanche
                    sheet.createRow(sheet.getLastRowNum()+1);
                    //AutoSize
                    sheet.autoSizeColumn(0);
                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                }
            }
            //FIN //NB : (PAS après "Fait à" inclus)
            
            //On nomme la DPGF
            String outputDPGF = rootExportFiles+"Exports/" + projet.getNomProjet() + "_" + projet.getIdProjet() + "/" + projet.getNomProjet() + "_DPGF.xlsx"; //Surement à changer lors de l'installation client

            //On écrit en sortie le document EXCEL
            OutputStream fileOut = new FileOutputStream(outputDPGF);
            excel.write(fileOut);
            
            resultat.add(outputDPGF);
            
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, ex.toString());
            resultat.clear();
            //RM le dossier crée
            if(dossierCree) {
                try {
                    FileUtils.deleteDirectory(new File(rootExportFiles+"Exports/" + nomProjet + "_" + idProjet));
                } catch (Exception e) {
                    Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service ExporterProjet(Long idProjet, int choixTemplate, String uriXML). Vous devez supprimer à la main le dossier d'export crée", e);
                }
            }
        } finally {
            try {
                //On supprime les infos projets dans le XML 
                XPathFactory xpf = XPathFactory.newInstance();
                XPath xpath = xpf.newXPath();
                Document xml = projetXMLDao.ObtenirDocument(uriXML);
                
                XPathExpression expression = xpath.compile("//nomProjet");
                Node nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//typeMarche");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//typeConstruction");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//typeLot");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//site");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//datePrixRef");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//coeffAdapt");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//coeffRaccordement");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//categorieConstruction");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//sousCategorieConstruction");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);
                expression = xpath.compile("//caractDim");
                nodeToDelete = (Node) expression.evaluate(xml, XPathConstants.NODE);
                nodeToDelete.getParentNode().removeChild(nodeToDelete);

                //Ecriture du XML
                projetXMLDao.saveXMLContent(xml, uriXML);
                
            } catch (Exception ex) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service ExporterProjet(Long idProjet, int choixTemplate, String uriXML)", ex);
            }
            DomUtil.destroy();
        }
        System.out.println("Resultat : "+resultat.toString());
        return resultat;
    }
    
    public Boolean ContientOuvrageOuPrestation(Element lot) {
        NodeList listeDescriptif = lot.getElementsByTagName("descriptif");
        for(int i = 0; i < listeDescriptif.getLength(); i++) {
            Element descriptifBalise = (Element)listeDescriptif.item(i);
            if(descriptifBalise.getAttribute("type").equals("ouvrage") || descriptifBalise.getAttribute("type").equals("prestation"))
                return true;
        }
        return false;
    }
    
    public Double CalculSousTotal(Element baliseTitre) {
        Double sousTotal = 0.0;
        
        NodeList listeLigneChiffrage = baliseTitre.getElementsByTagName("ligneChiffrage");
        for(int i = 0; i < listeLigneChiffrage.getLength(); i++) {
            Element baliseLigneChiffrage = (Element)listeLigneChiffrage.item(i);
            String quantite = baliseLigneChiffrage.getElementsByTagName("quantite").item(0).getTextContent();
            String prixUnitaire = baliseLigneChiffrage.getElementsByTagName("prixUnitaire").item(0).getTextContent();
            if(!quantite.equals("") && !prixUnitaire.equals("")) {
                Double montantHT = Double.parseDouble(prixUnitaire)*Double.parseDouble(quantite);
                sousTotal+=montantHT;
            }
        }
        
        return sousTotal;
    }
    
    public Workbook RecapTitre(Workbook excel, int nbSheet, Element titreBalise, CellStyle cellStyle, CreationHelper createHelper) {
        Sheet sheet = excel.getSheetAt(nbSheet);
        
        Row ligneRecap = sheet.createRow(sheet.getLastRowNum()+1);
        CellStyle styleTemporaire = excel.createCellStyle(); //Permet de ne modifier l'alignement que pour le récap
        styleTemporaire.cloneStyleFrom(cellStyle);
        styleTemporaire.setAlignment(HorizontalAlignment.RIGHT);
        styleTemporaire = createBorderedStyle(styleTemporaire);

        DataFormat format = excel.createDataFormat();
        CellStyle styleTemporairePrice = styleTemporaire;
        styleTemporairePrice.setDataFormat(format.getFormat("#,##0.00€")); 

        ligneRecap.createCell(0).setCellStyle(styleTemporaire);
        ligneRecap.createCell(1).setCellStyle(styleTemporaire);
        ligneRecap.createCell(2).setCellStyle(styleTemporaire);
        ligneRecap.createCell(3).setCellStyle(styleTemporaire);
        ligneRecap.createCell(4).setCellStyle(styleTemporaire);
        ligneRecap.createCell(5).setCellStyle(styleTemporaire);
        ligneRecap.createCell(6).setCellStyle(styleTemporairePrice);
        sheet.addMergedRegion(new CellRangeAddress(
                sheet.getLastRowNum(), //first row (0-based)
                sheet.getLastRowNum(), //last row  (0-based)
                0, //first column (0-based)
                5  //last column  (0-based)
        ));
        ligneRecap.getCell(0).setCellValue(createHelper.createRichTextString("SOUS-TOTAL " + titreBalise.getAttribute("intitule")));
        ligneRecap.getCell(6).setCellValue(CalculSousTotal(titreBalise));
        
        return excel;
    }
    
    //Procédure DPGFDescriptif(...)
        //On créer le tuple (n°, nom, courte description)
        //Si une seule ligne chiffrage
            //on continue ce tuple (unite, quantité, prixUnitaire, montant HT)
        //Sinon
            //Pour chaque ligneChiffrage
                //On créer un tuple (n°, localisation, unite, quantité, prixUnitaire, montant HT)
    //FIN
    public Workbook DPGFDescriptif(Workbook excel, int nbSheet, Element descriptif, CreationHelper createHelper, int nbTitre1, int nbTitre2, int nbTitre3, int nbTitre4) {
        //On créer le tuple (n°, nom, courte description)
        //On créer les styles
        CellStyle styleBorderLRBold = createBorderedStyleLR(excel.createCellStyle());
        Font bold11 = excel.createFont();
        bold11.setBold(true);
        styleBorderLRBold.setFont(bold11);
        CellStyle styleBorderLR = createBorderedStyleLR(excel.createCellStyle());
        
        //On créer la ligne du descriptif
        Sheet sheet = excel.getSheetAt(nbSheet);
        Row ligneDescriptif = sheet.createRow(sheet.getLastRowNum()+1);
        ligneDescriptif.createCell(0).setCellStyle(styleBorderLRBold);
        ligneDescriptif.createCell(1).setCellStyle(styleBorderLRBold);
        ligneDescriptif.createCell(2).setCellStyle(styleBorderLR);
        ligneDescriptif.createCell(3).setCellStyle(styleBorderLR);
        ligneDescriptif.createCell(4).setCellStyle(styleBorderLR);
        ligneDescriptif.createCell(5).setCellStyle(styleBorderLR);
        ligneDescriptif.createCell(6).setCellStyle(styleBorderLR);
        //On le rempli des infos
        if(nbTitre3 == -1)
            ligneDescriptif.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(nbTitre1+1) + "." + (nbTitre2)));
        else if(nbTitre4 == -1)
            ligneDescriptif.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(nbTitre1+1) + "." + (nbTitre2) + "." + (nbTitre3)));
        else
            ligneDescriptif.getCell(0).setCellValue(createHelper.createRichTextString(toRoman(nbTitre1+1) + "." + (nbTitre2) + "." + (nbTitre3) + "." + (nbTitre4)));
        ligneDescriptif.getCell(1).setCellValue(createHelper.createRichTextString(descriptif.getElementsByTagName("nomDescriptif").item(0).getTextContent()));
        ligneDescriptif.getCell(2).setCellValue(createHelper.createRichTextString(descriptif.getElementsByTagName("courteDescription").item(0).getTextContent()));
        NodeList listeLigneChiffrage = descriptif.getElementsByTagName("ligneChiffrage");
        //Si plusieurs lignes chiffrages
        if(listeLigneChiffrage.getLength() > 1) {
           //Laisser le tuple débuté
           //Pour chaque ligneChiffrage
            for(int l = 0; l < listeLigneChiffrage.getLength(); l++) {
                //Créer tuple (n°article, localisation à la place du nom surligné en jaune, pas de description, unité, PU, Q et montant HT)
                Row ligneChiffrage = sheet.createRow(sheet.getLastRowNum()+1);
                //On créer le style pour les différentes localisations
                CellStyle styleBorderLRRight = createBorderedStyleLR(excel.createCellStyle());
                styleBorderLRRight.setAlignment(HorizontalAlignment.RIGHT);

                DataFormat format = excel.createDataFormat();
                CellStyle styleBorderLRNumber = createBorderedStyleLR(excel.createCellStyle());
                styleBorderLRNumber.setDataFormat(format.getFormat("#,##0.00")); 
                CellStyle styleBorderLRPrice = createBorderedStyleLR(excel.createCellStyle());
                styleBorderLRPrice.setDataFormat(format.getFormat("#,##0.00€")); 

                //On applique les styles
                ligneChiffrage.createCell(0).setCellStyle(styleBorderLR);
                ligneChiffrage.createCell(1).setCellStyle(styleBorderLRRight);
                ligneChiffrage.createCell(2).setCellStyle(styleBorderLR);
                ligneChiffrage.createCell(3).setCellStyle(styleBorderLR);
                ligneChiffrage.createCell(4).setCellStyle(styleBorderLRNumber);
                ligneChiffrage.createCell(5).setCellStyle(styleBorderLRPrice);
                ligneChiffrage.createCell(6).setCellStyle(styleBorderLRPrice);
                //On rempli la ligne
                Element baliseLigneChiffrage = (Element)listeLigneChiffrage.item(l);
                String localisation = baliseLigneChiffrage.getElementsByTagName("localisation").item(0).getTextContent();
                String unite = descriptif.getElementsByTagName("unite").item(0).getTextContent();
                String quantite = baliseLigneChiffrage.getElementsByTagName("quantite").item(0).getTextContent();
                String prixUnitaire = baliseLigneChiffrage.getElementsByTagName("prixUnitaire").item(0).getTextContent();
                if(!quantite.equals("") && !prixUnitaire.equals("")) {
                    Double montantHT = Double.parseDouble(prixUnitaire)*Double.parseDouble(quantite);
                    ligneChiffrage.getCell(1).setCellValue(createHelper.createRichTextString(localisation));
                    ligneChiffrage.getCell(3).setCellValue(createHelper.createRichTextString(unite));
                    ligneChiffrage.getCell(4).setCellValue(Float.parseFloat(quantite));
                    ligneChiffrage.getCell(5).setCellValue(Float.parseFloat(prixUnitaire));
                    ligneChiffrage.getCell(6).setCellValue(montantHT); 
                }
            }            
        } else {
            //Sinon continuer le tuple (avec unité, quantité, prix unitaire et montant HT)
            Element baliseLigneChiffrage = (Element)listeLigneChiffrage.item(0);
            String unite = descriptif.getElementsByTagName("unite").item(0).getTextContent();
            String quantite = baliseLigneChiffrage.getElementsByTagName("quantite").item(0).getTextContent();
            String prixUnitaire = baliseLigneChiffrage.getElementsByTagName("prixUnitaire").item(0).getTextContent();

            DataFormat format = excel.createDataFormat();
            CellStyle styleBorderLRNumber = createBorderedStyleLR(excel.createCellStyle());
            styleBorderLRNumber.setDataFormat(format.getFormat("#,##0.00")); 
            CellStyle styleBorderLRPrice = createBorderedStyleLR(excel.createCellStyle());
            styleBorderLRPrice.setDataFormat(format.getFormat("#,##0.00€")); 

            ligneDescriptif.createCell(4).setCellStyle(styleBorderLRNumber);
            ligneDescriptif.createCell(5).setCellStyle(styleBorderLRPrice);
            ligneDescriptif.createCell(6).setCellStyle(styleBorderLRPrice);

            if(!quantite.equals("") && !prixUnitaire.equals("")) {
                Double montantHT = Double.parseDouble(prixUnitaire)*Double.parseDouble(quantite);
                ligneDescriptif.getCell(3).setCellValue(createHelper.createRichTextString(unite));
                ligneDescriptif.getCell(4).setCellValue(Float.parseFloat(quantite));
                ligneDescriptif.getCell(5).setCellValue(Float.parseFloat(prixUnitaire));
                ligneDescriptif.getCell(6).setCellValue(montantHT);
            }
        }
        return excel;
    }
    
    public XWPFDocument ExtractionDescriptif(Element descriptif, String style, XWPFDocument word) {
        try {
            //On extrait nomDescriptif et on le met dans un p
            XWPFParagraph pNomDescriptif = word.createParagraph();
            pNomDescriptif.setStyle(style);
            XWPFRun rNomDescriptif = pNomDescriptif.createRun();
            rNomDescriptif.setText(descriptif.getElementsByTagName("nomDescriptif").item(0).getTextContent());

            word.createParagraph(); //Espace entre les paragraphes
            
            //Si ça n'est pas un générique
            String unite;
            if(!descriptif.getAttribute("type").equals("generique")) {
            //On extrait l'unité
                unite = descriptif.getElementsByTagName("unite").item(0).getTextContent();
            
                //Paragraphe unité
                XWPFParagraph pUnite = word.createParagraph();
                XWPFRun rUnite = pUnite.createRun();
                rUnite.setText("Métré = " + unite);
                rUnite.setUnderline(SINGLE);

                word.createParagraph(); //Espace entre les paragraphes
            }

            //pour chaque balise (p ou ul): on selectionne uniquement les balises enfants et on les parcours
            Element description = (Element) descriptif.getElementsByTagName("description").item(0);
            NodeList enfantsDescription = description.getChildNodes();
            for(int i = 0; i<enfantsDescription.getLength(); i++) {
              if(enfantsDescription.item(i).getNodeType() == Node.ELEMENT_NODE) {        //on traite les balises p
                XWPFParagraph pDescription = word.createParagraph();
                if(enfantsDescription.item(i).getNodeName().equals("li")){ 
                    pDescription.setStyle("Listepuces");
                    pDescription.setIndentationLeft(700);   //valeur numérique correspondant à l'indentation de base d'une puce
                }
                Element p = (Element) enfantsDescription.item(i);
                //on boucle sur les enfants du paragraphe
                NodeList enfantsP = p.getChildNodes();
                for(int j = 0; j<enfantsP.getLength(); j++) {
                    if(enfantsP.item(j).getNodeType() == Node.ELEMENT_NODE){
                        XWPFRun rDescritpion = pDescription.createRun();
                        rDescritpion.setText(enfantsP.item(j).getTextContent());
                        if(enfantsP.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            switch(enfantsP.item(j).getNodeName()){
                                case "u":
                                    rDescritpion.setUnderline(SINGLE);
                                    break;
                                case "underlinedash":
                                    rDescritpion.setUnderline(DASH);
                                    break;
                                case "i":
                                    rDescritpion.setItalic(true);
                                    break;
                                case "italic_underline":
                                    rDescritpion.setItalic(true);
                                    rDescritpion.setUnderline(SINGLE);
                                    break;
                                case "b":
                                    rDescritpion.setBold(true);
                                    break;
                                case "bold_underline":
                                    rDescritpion.setBold(true);
                                    rDescritpion.setUnderline(SINGLE);
                                    break;
                                case "bold_italic":
                                    rDescritpion.setItalic(true);
                                    rDescritpion.setBold(true);
                                    break;
                                case "bold_underline_italic":
                                    rDescritpion.setBold(true);
                                    rDescritpion.setItalic(true);
                                    rDescritpion.setUnderline(SINGLE);
                                    break;
                                case "colorred":
                                    rDescritpion.setColor("FF0000");
                                    break;
                                case "colororange":
                                    rDescritpion.setColor("E36C0A");
                                    break;
                                case "colorgreen":
                                    rDescritpion.setColor("00B050");
                                    break;
                                case "colorblue":
                                    rDescritpion.setColor("0070C0");
                                    break;
                                case "highlightyellow":
                                    rDescritpion.setTextHighlightColor("yellow");
                                    break;
                                case "highlightcyan":
                                    rDescritpion.setTextHighlightColor("cyan");
                                    break;
                                case "highlightred":
                                    rDescritpion.setTextHighlightColor("red");
                                    break;
                                case "highlightgreen":
                                    rDescritpion.setTextHighlightColor("green");
                                    break;
                                case "highlightmagenta":
                                    rDescritpion.setTextHighlightColor("magenta");
                                    break;
                                case "highlightgrey":
                                    rDescritpion.setTextHighlightColor("lightGray");
                                    break;  
                            }
                        }
                    }
                }
              }				
            }
            
            //Si ça n'est pas un générique
            if(!descriptif.getAttribute("type").equals("generique")) {
                word.createParagraph(); //Espace entre les paragraphes

                //On extrait les différents élements de ligneChiffrage et on les mets dans des p
                NodeList listeLigneChiffrage = descriptif.getElementsByTagName("ligneChiffrage");
                if(listeLigneChiffrage.getLength() > 1) {
                    //Paragraphe localisation
                    XWPFParagraph pLocalisation = word.createParagraph();
                    XWPFRun rLocalisation = pLocalisation.createRun();
                    rLocalisation.setText("Localisation : ");
                    rLocalisation.setColor("7030A0");                 
                    rLocalisation.setItalic(true);
                    rLocalisation.setBold(true);
                    //Puces différentes localisations
                    for(int i = 0; i < listeLigneChiffrage.getLength(); i++) {
                        Element ligneChiffrage = (Element) listeLigneChiffrage.item(i);
                        String localisation = ligneChiffrage.getElementsByTagName("localisation").item(0).getTextContent();
                        XWPFParagraph pPuces = word.createParagraph();
                        pPuces.setStyle("Listepuces");
                        pPuces.setIndentationLeft(700);   //valeur numérique correspondant à l'indentation de base d'une puce
                        XWPFRun rLocalisationPuces = pPuces.createRun();
                        rLocalisationPuces.setText(localisation);
                        rLocalisationPuces.setColor("7030A0");
                        rLocalisationPuces.setItalic(true);
                        rLocalisationPuces.setBold(true);
                    }
                } else {
                    Element ligneChiffrage = (Element) listeLigneChiffrage.item(0);
                    String localisation = ligneChiffrage.getElementsByTagName("localisation").item(0).getTextContent();
                    //Paragraphe localisation
                    XWPFParagraph pLocalisation = word.createParagraph();
                    XWPFRun rLocalisation = pLocalisation.createRun();
                    rLocalisation.setText("Localisation : " + localisation);
                    rLocalisation.setColor("7030A0");
                    rLocalisation.setItalic(true);
                    rLocalisation.setBold(true);
                }
            }
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service ExporterProjet(Long idProjet, int choixTemplate)", ex);
        }
        return word;
    }
    
    private static CellStyle createBorderedStyle(CellStyle style) {
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
    
    private static CellStyle createBoldBorderedStyle(CellStyle style) {
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
    
    private static CellStyle createBorderedStyleLR(CellStyle style) {
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}