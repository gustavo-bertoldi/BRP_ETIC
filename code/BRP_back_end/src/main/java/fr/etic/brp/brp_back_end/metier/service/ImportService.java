package fr.etic.brp.brp_back_end.metier.service;

import fr.etic.brp.brp_back_end.dao.BasePrixRefDao;
import fr.etic.brp.brp_back_end.dao.CategorieDao;
import fr.etic.brp.brp_back_end.dao.ChapitreDao;
import fr.etic.brp.brp_back_end.dao.DescriptifDao;
import fr.etic.brp.brp_back_end.dao.FamilleDao;
import fr.etic.brp.brp_back_end.dao.JpaUtil;
import fr.etic.brp.brp_back_end.dao.PrestationDao;
import fr.etic.brp.brp_back_end.dao.SousFamilleDao;
import fr.etic.brp.brp_back_end.metier.modele.BasePrixRef;
import fr.etic.brp.brp_back_end.metier.modele.Categorie;
import fr.etic.brp.brp_back_end.metier.modele.Chapitre;
import fr.etic.brp.brp_back_end.metier.modele.Descriptif;
import fr.etic.brp.brp_back_end.metier.modele.Famille;
import fr.etic.brp.brp_back_end.metier.modele.Generique;
import fr.etic.brp.brp_back_end.metier.modele.Ouvrage;
import fr.etic.brp.brp_back_end.metier.modele.Prestation;
import fr.etic.brp.brp_back_end.metier.modele.SousFamille;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author quentinmarc
 */
public class ImportService {
    
    protected ChapitreDao chapitreDao = new ChapitreDao();
    protected DescriptifDao descriptifDao = new DescriptifDao();
    protected FamilleDao familleDao = new FamilleDao();
    protected CategorieDao categorieDao = new CategorieDao();
    protected SousFamilleDao sousFamilleDao = new SousFamilleDao();
    protected PrestationDao prestationDao = new PrestationDao();
    protected BasePrixRefDao basePrixRefDao = new BasePrixRefDao();
    
    //protected String rootImportFiles = "../../../../code/BRP_front_end/src/main/webapp/import_files/";
    //protected String rootImportFiles = "../../../../../../../Projets/ETIC/Etude_BRP/code/BRP_front_end/src/main/webapp/import_files/";
    protected String rootImportFiles = "/usr/local/tomcat/webapps/BRP_editeur_rapports/import_files/";
    //protected String rootImportFiles = "/usr/local/Cellar/tomcat/9.0.41/libexec/webapps/BRP_front_end-1.0-SNAPSHOT/import_files/";
    
    public ArrayList<String> ModifBaseDescriptif(String nameWord){
        
        String uriWord = rootImportFiles + nameWord;
        String idActuel = null;
        Boolean erreur = false;
        int countUnderscore = 0;
        int countRows = 0;
        ArrayList<ArrayList<String>> docListe =  new ArrayList<ArrayList<String>> ();       //Création d'un format indicé
        ArrayList<String> returnListe =  new ArrayList<String> ();
        returnListe.add("");
        
       try {
            //Importer le word
            FileInputStream fis = new FileInputStream(uriWord);
            XWPFDocument doc = new XWPFDocument(OPCPackage.open(fis));
            List<XWPFTable> table = doc.getTables();        //on extrait tous les tableaux 
            
            //Extraction des informations dans un format rigoureux et indicé
            for (XWPFTable xwpfTable : table) { 
                //On se trouve dans un tableau particulier. On en extrait les lignes
                countRows = 0;
                String chaineDescription = "";
                String chaineParagraph = "";
                String runStyle = "";
                String testingRunStyle = "";
                Boolean first = true;
                ArrayList<String> tableau = new ArrayList<String>();
                List<XWPFTableRow> row = xwpfTable.getRows(); 
                for (XWPFTableRow xwpfTableRow : row) { 
                    List<XWPFTableCell> cell = xwpfTableRow.getTableCells();
                    //on extrait les cellules (même si on en a qu'une par ligne)
                    for (XWPFTableCell xwpfTableCell : cell) { 
                        if(xwpfTableCell!=null) { 
                            if(countRows != 4){ //on est pas dans une description, pas besoin de traiter les styles
                                tableau.add(xwpfTableCell.getText());
                            }
                            else{
                                //on est dans une description. On extrait les styles
                                for (XWPFParagraph paragraph : xwpfTableCell.getParagraphs()) {
                                        
                                        chaineParagraph = "";
                                        runStyle = "";
                                        
                                        first = true;
                                        for (XWPFRun run : paragraph.getRuns()) {   //on extrait les runs
                                            
                                            testingRunStyle = "normal";
                                            
                                            //on test les textures
                                            if(!run.isBold() && run.getUnderline().toString().equals("SINGLE") && !run.isItalic())
                                                testingRunStyle = "u";
                                            if(!run.isBold() && run.getUnderline().toString().equals("DASH") && !run.isItalic())
                                                testingRunStyle = "underlinedash";
                                            if(!run.isBold() && run.getUnderline().toString().equals("NONE") && run.isItalic())
                                                testingRunStyle = "i";
                                            if(!run.isBold() && run.getUnderline().toString().equals("SINGLE") && run.isItalic())
                                                testingRunStyle = "italic_underline";
                                            if(run.isBold() && run.getUnderline().toString().equals("NONE") && !run.isItalic())
                                                testingRunStyle = "b";
                                            if(run.isBold() && run.getUnderline().toString().equals("SINGLE") && !run.isItalic())
                                                testingRunStyle = "bold_underline";
                                            if(run.isBold() && run.getUnderline().toString().equals("NONE") && run.isItalic())
                                                testingRunStyle = "bold_italic";
                                            if(run.isBold() && run.getUnderline().toString().equals("SINGLE") && run.isItalic())
                                                testingRunStyle = "bold_underline_italic";
                                            
                                            //on test les couleurs
                                            if("FF0000".equals(run.getColor()))
                                                testingRunStyle = "colorred";
                                            if("E36C0A".equals(run.getColor()))
                                                testingRunStyle = "colororange";
                                            if("00B050".equals(run.getColor()))
                                                testingRunStyle = "colorgreen";
                                            if("0070C0".equals(run.getColor()))
                                                testingRunStyle = "colorblue";
                                            
                                            //on test les surlignages
                                            if("yellow".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightyellow";
                                            if("cyan".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightcyan";
                                            if("red".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightred";
                                            if("green".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightgreen";
                                            if("magenta".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightmagenta";
                                            if("lightGray".equals(run.getTextHightlightColor().toString()))
                                                testingRunStyle = "highlightgrey";

                                            
                                            //si le style est différent de celui d'avant, on ferme le style d'avant et on ouvre le suivant
                                            if(!runStyle.equals(testingRunStyle)){
                                                //s'il y avait un style avant, on le ferme
                                                if(!first)
                                                    chaineParagraph += "</"+runStyle+">";
                                                
                                                chaineParagraph += "<"+testingRunStyle+">"; //on ouvre la nouvelle balise de style
                                                chaineParagraph += run.text();
                                                runStyle = testingRunStyle;
                                            }
                                            else{
                                                chaineParagraph+= run.text();
                                            }
                                            first = false;
                                        }              
                                        if(!"".equals(runStyle))
                                            chaineParagraph += "</"+runStyle+">"; //on ferme la dernière balise
               
                                        if("bullet".equals(paragraph.getNumFmt())){
                                            chaineDescription += "<li>"+chaineParagraph+"</li>"; //verif si fermante
                                        }
                                        else{ 
                                            chaineDescription += "<p>"+chaineParagraph+"</p>";
                                        }
                                          //chaineDescription += chaineParagraph;
                                }
                                
                                tableau.add(chaineDescription);             
                            }
                        }
                    }
                    countRows++;
                }
                docListe.add(tableau);
                
            }         
         } catch(IOException | InvalidFormatException ex) {
            erreur = true;
            returnListe.set(0, "Erreur système: l'API POI ne parvient pas à extraire les données");
         }
        
        //si on a réussi à extraire les données du word, on peut démarrer l'exploitation des données
        if(!erreur){
            //on parcourt la liste des objets
            for(int i = 0; i < docListe.size(); i++){
                //on extrait l'identifiant et on déduit le type d'objet
                idActuel = docListe.get(i).get(0);
                //on compte le nombre de "_" dans l'ID
                countUnderscore = 0;
                for (int j = 0; j < idActuel.length(); j++) {
                    if (idActuel.charAt(j) == '_') 
                        countUnderscore++;
                }

                JpaUtil.creerContextePersistance();
                try {
                    if(countUnderscore < 4){
                        if(docListe.get(i).get(1).equals("SUPPR")){ //on traite les suppressions
                            //on ajoute à la liste de sortie les idnetifiant à supprimer
                            returnListe.add(idActuel);
                        }
                        else{       //on procède à l'insertion d'un "titre" dans la BD
                            switch(countUnderscore){
                                case 0:             //on importe un chapitre en BD
                                    Chapitre chapitre = null;
                                    chapitre = chapitreDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(chapitre == null){   //on crée le chapitre
                                        chapitre = new Chapitre(idActuel, docListe.get(i).get(1));
                                        chapitreDao.Creer(chapitre);
                                    }
                                    else{   //on modifie l'intitule du chapitre
                                        chapitre.setIntituleChapitre(docListe.get(i).get(1));
                                        chapitreDao.Update(chapitre);
                                    }  
                                    JpaUtil.validerTransaction();
                                    break;
                                case 1:             //on importe une categorie en BD
                                    Categorie categorie = null;
                                    categorie = categorieDao.ChercherParId(idActuel);       //idActuel est l'identifiant de l'objet que l'on traite
                                    JpaUtil.ouvrirTransaction();
                                    if(categorie == null){   //on crée la categorie
                                        categorie = new Categorie(idActuel, docListe.get(i).get(1));
                                        categorieDao.Creer(categorie);  
                                        //on va chercher le chapitre parent pour update listeCategorie
                                        Chapitre chapitreParent = chapitreDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<Categorie> listeCategorie = chapitreParent.getListCategorie();
                                        listeCategorie.add(categorie);
                                        chapitreParent.setListCategorie(listeCategorie);
                                        chapitreDao.Update(chapitreParent);
                                    } else {   //on modifie l'initule de la categorie
                                        categorie.setIntituleCategorie(docListe.get(i).get(1));
                                        categorieDao.Update(categorie);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break;  

                                case 2:             //on importe une categorie en BD
                                    Famille famille = null;
                                    famille = familleDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(famille == null){   //on crée la famille
                                        famille = new Famille(idActuel, docListe.get(i).get(1));
                                        familleDao.Creer(famille);
                                        //on va chercher la categorie parent pour update listeFamille
                                        Categorie categorieParent = categorieDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<Famille> listeFamille = categorieParent.getListeFamille();
                                        listeFamille.add(famille);
                                        categorieParent.setListeFamille(listeFamille);
                                        categorieDao.Update(categorieParent);
                                    }
                                    else{   //on modifie l'intitule de la famille
                                        famille.setIntituleFamille(docListe.get(i).get(1));
                                        familleDao.Update(famille);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break; 
                                case 3:             //on importe une sousFamille en BD
                                    SousFamille sousFamille = null;
                                    sousFamille = sousFamilleDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(sousFamille == null){   //on crée la sousFamille
                                        sousFamille = new SousFamille(idActuel, docListe.get(i).get(1));
                                        sousFamilleDao.Creer(sousFamille);
                                        //on va chercher la famille parent pour update listeSousFamille
                                        Famille familleParent = familleDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<SousFamille> listeSousFamille = familleParent.getListSousFamille();
                                        listeSousFamille.add(sousFamille);
                                        familleParent.setListSousFamille(listeSousFamille);
                                        familleDao.Update(familleParent);
                                    }
                                    else{   //on modifie l'intitule de la sousFamille
                                        sousFamille.setIntituleSousFamille(docListe.get(i).get(1));
                                        sousFamilleDao.Update(sousFamille);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break;
                            }
                        }
                    }
                    //on procède au traitement d'un descriptif
                    else{
                        //on traite les ajouts
                        if(docListe.get(i).get(1).equals("AJOUT")){
                            switch(docListe.get(i).get(2)){
                                case "OUVRAGE":
                                    Ouvrage ouvrage = null;
                                    ouvrage = (Ouvrage) descriptifDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(ouvrage == null){   //on crée le chapitre
                                        ouvrage = new Ouvrage(idActuel, docListe.get(i).get(3), docListe.get(i).get(4), docListe.get(i).get(5), docListe.get(i).get(6));
                                        descriptifDao.Creer(ouvrage);
                                        //on va chercher la sousFamille parent pour update listeDescriptif
                                        SousFamille sousFamilleParent = sousFamilleDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<Descriptif> listeDescriptif = sousFamilleParent.getListDescriptif();
                                        listeDescriptif.add(ouvrage);
                                        sousFamilleParent.setListDescriptif(listeDescriptif);
                                        sousFamilleDao.Update(sousFamilleParent);
                                    }
                                    else{   //on modifie le titre du chapitre
                                        ouvrage.setNomDescriptif(docListe.get(i).get(3));
                                        ouvrage.setDescription(docListe.get(i).get(4));
                                        ouvrage.setCourteDescription(docListe.get(i).get(5));
                                        descriptifDao.Update(ouvrage);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break;
                                case "GENERIQUE":
                                    Generique generique = null;
                                    generique = (Generique) descriptifDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(generique == null){   //on crée le chapitre
                                        generique = new Generique(idActuel, docListe.get(i).get(3), docListe.get(i).get(4), docListe.get(i).get(5));
                                        descriptifDao.Creer(generique);
                                        //on va chercher la sousFamille parent pour update listeDescriptif
                                        SousFamille sousFamilleParent2 = sousFamilleDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<Descriptif> listeDescriptif2 = sousFamilleParent2.getListDescriptif();
                                        listeDescriptif2.add(generique);
                                        sousFamilleParent2.setListDescriptif(listeDescriptif2);
                                        sousFamilleDao.Update(sousFamilleParent2);
                                    }
                                    else{   //on modifie le titre du chapitre
                                        generique.setNomDescriptif(docListe.get(i).get(3));
                                        generique.setDescription(docListe.get(i).get(4));
                                        generique.setCourteDescription(docListe.get(i).get(5));
                                        descriptifDao.Update(generique);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break;  
                                case "PRESTATION": 
                                    Prestation prestation = null;
                                    prestation = prestationDao.ChercherParId(idActuel);
                                    JpaUtil.ouvrirTransaction();
                                    if(prestation == null){   //on crée la prestation
                                        prestation = new Prestation(idActuel, docListe.get(i).get(3), docListe.get(i).get(4), docListe.get(i).get(5), docListe.get(i).get(6));
                                        prestationDao.Creer(prestation);
                                        //on va chercher l'ouvrage  parent pour update listeprestation
                                        Ouvrage ouvrageParent = (Ouvrage) descriptifDao.ChercherParId(idActuel.substring(0, idActuel.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                                        List<Prestation> listePrestation = ouvrageParent.getListePrestation();
                                        listePrestation.add(prestation);
                                        ouvrageParent.setListePrestation(listePrestation);
                                        descriptifDao.Update(ouvrageParent);
                                    }
                                    else{   //on modifie le titre de la prestation
                                        prestation.setNomDescriptif(docListe.get(i).get(3));
                                        prestation.setDescription(docListe.get(i).get(4));
                                        prestation.setCourteDescription(docListe.get(i).get(5));
                                        prestationDao.Update(prestation);
                                    } 
                                    JpaUtil.validerTransaction();
                                    break;
                            }
                        }
                        //on traite les suppressions
                        else{
                            //on ajoute à la liste de sortie les idnetifiant à supprimer
                            returnListe.add(idActuel);
                        }
                    } 
                } catch(Exception ex){
                    returnListe.set(0, "Problème d'insertion dans la base de donnée (problème de format?). ID: "+idActuel);
                    erreur = true;
                } finally {
                    JpaUtil.fermerContextePersistance();  
                }
            }
        }
        
        if(!erreur){
            returnListe.set(0, "Succes");
        }
        
        return returnListe;
    }
    
    public String CompterEnfants(String idDescriptif){
        List<Descriptif> descriptifsSuppr = null;
        String msgStatement = "";
        
        //on sélectionne tous les descriptifs dont l'id commence par: idDescriptif
        JpaUtil.creerContextePersistance();
        try {
            descriptifsSuppr = descriptifDao.compterDescriptifsSuppr(idDescriptif);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service rechercherDescriptifParId(id)", ex);
            descriptifsSuppr = null;
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        
        if(descriptifsSuppr.size() <= 1){ //lui même
            msgStatement = "suppr ok";
        }
        else{
            msgStatement =  "("+idDescriptif+"/"+descriptifsSuppr.size()+")";
        }
        
        return msgStatement;
    }
    
    
    public String SupprObjet(String idSuppr){
        String msgStatement = "";
        
        //on détermine la nature de l'objet
        int countUnderscore = 0;
        for (int i = 0; i < idSuppr.length(); i++) {
            if (idSuppr.charAt(i) == '_') 
                countUnderscore++;
        }
        
        JpaUtil.creerContextePersistance();
        try {
            switch(countUnderscore){
                case 0:             //on suppr un chapitre
                    Chapitre chapitre = null;
                    chapitre = chapitreDao.ChercherParId(idSuppr);
                    if(chapitre != null){   
                        JpaUtil.ouvrirTransaction();
                        chapitreDao.Remove(chapitre);
                        JpaUtil.validerTransaction();
                    }
                    break;
                case 1:             //on suppr une categorie
                    Categorie categorie = null;
                    categorie = categorieDao.ChercherParId(idSuppr);
                    if(categorie != null){   
                        JpaUtil.ouvrirTransaction();
                        //on va chercher la categorie parent pour update listeFamille
                        Chapitre chapitreParent = chapitreDao.ChercherParId(idSuppr.substring(0, idSuppr.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                        List<Categorie> listCategorie = chapitreParent.getListCategorie();
                        listCategorie.remove(categorie);
                        chapitreParent.setListCategorie(listCategorie);
                        chapitreDao.Update(chapitreParent);
                        categorieDao.Remove(categorie);
                        JpaUtil.validerTransaction();
                    }
                    break;  
                case 2:             //on suppr une famille
                    Famille famille = null;
                    famille = familleDao.ChercherParId(idSuppr);
                    if(famille != null){   
                        JpaUtil.ouvrirTransaction();
                        //on va chercher la categorie parent pour update listeFamille
                        Categorie categorieParent = categorieDao.ChercherParId(idSuppr.substring(0, idSuppr.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                        List<Famille> listeFamille = categorieParent.getListeFamille();
                        listeFamille.remove(famille);
                        categorieParent.setListeFamille(listeFamille);
                        categorieDao.Update(categorieParent);
                        familleDao.Remove(famille);
                        JpaUtil.validerTransaction();
                    }
                    break; 
                case 3:             //on suppr une sousFamille
                    SousFamille sousFamille = null;
                    sousFamille = sousFamilleDao.ChercherParId(idSuppr);
                    if(sousFamille != null){   
                        JpaUtil.ouvrirTransaction();
                        //on va chercher la famille parent pour update listeSousFamille
                        Famille familleParent = familleDao.ChercherParId(idSuppr.substring(0, idSuppr.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                        List<SousFamille> listSousFamille = familleParent.getListSousFamille();
                        listSousFamille.remove(sousFamille);
                        familleParent.setListSousFamille(listSousFamille);
                        familleDao.Update(familleParent);
                        sousFamilleDao.Remove(sousFamille);
                        JpaUtil.validerTransaction();
                    }
                    break;
                case 4:            //on suppr un descrptif
                    Descriptif descriptif = null;
                    descriptif = descriptifDao.ChercherParId(idSuppr);
                    if(descriptif != null){   
                        JpaUtil.ouvrirTransaction();
                          //on va chercher la sousFamille parent pour update listeDescriptif
                        SousFamille sousFamilleParent = sousFamilleDao.ChercherParId(idSuppr.substring(0, idSuppr.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                        List<Descriptif> listeDescriptif = sousFamilleParent.getListDescriptif();
                        listeDescriptif.remove(descriptif);
                        sousFamilleParent.setListDescriptif(listeDescriptif);
                        sousFamilleDao.Update(sousFamilleParent);
                        descriptifDao.Remove(descriptif);
                        JpaUtil.validerTransaction();
                    }
                    break;
                case 5:
                    Prestation prestation = null;
                    prestation = (Prestation) descriptifDao.ChercherParId(idSuppr);
                    if(prestation != null){   
                        JpaUtil.ouvrirTransaction();
                          //on va chercher l'ouvrage parent pour update listePrestation
                        Ouvrage ouvrageParent = (Ouvrage) descriptifDao.ChercherParId(idSuppr.substring(0, idSuppr.lastIndexOf('_'))); //on prend idActuel et on retire le dernier _ et ce qu'il y a derrière
                        List<Prestation> listePrestation = ouvrageParent.getListePrestation();
                        listePrestation.remove(prestation);
                        ouvrageParent.setListePrestation(listePrestation);
                        descriptifDao.Update(ouvrageParent);
                        descriptifDao.Remove(prestation);
                        JpaUtil.validerTransaction();
                    }
                    break;
            }

            msgStatement = "Succes";
        } catch(Exception ex){
            msgStatement = "une erreur est survenue lors de la suppression de "+idSuppr;
        } finally {
            JpaUtil.fermerContextePersistance();  
        }
        
        return msgStatement;
    }
    
    public Boolean TransformationWordVersExcel(String nameWord) {
        Boolean resultat = false;
        String uriWord = rootImportFiles+nameWord;
        
        try {
            //Import du WORD
            XWPFDocument word = new XWPFDocument(new FileInputStream(uriWord));
            
            //Création du document EXCEL
            Workbook excel = new XSSFWorkbook();
            Sheet sheet = excel.createSheet("Nouvel Onglet");
            //Ajout de l'en-tête des colonnes
            //Nom (aide operateur);Operation;Ouvrage/Prestation;nbPrixRef;indice BT;quantiteInf;quantiteSup;unite;prixUnitaire;anneeRef
            Row row = sheet.createRow(0);
            Cell cellNomDescriptif = row.createCell(0);
            cellNomDescriptif.setCellValue("Nom (aide operateur)");
            Cell cellOperation = row.createCell(1);
            cellOperation.setCellValue("Operation");
            Cell cellIdDescriptif = row.createCell(2);
            cellIdDescriptif.setCellValue("Ouvrage/Prestation");
            Cell cellNbPrixRef = row.createCell(3);
            cellNbPrixRef.setCellValue("nbPrixRef");
            Cell cellIndiceBT = row.createCell(4);
            cellIndiceBT.setCellValue("indice BT");
            Cell cellQuantiteInf = row.createCell(5);
            cellQuantiteInf.setCellValue("quantiteInf");
            Cell cellQuantiteSup = row.createCell(6);
            cellQuantiteSup.setCellValue("quantiteSup");
            Cell cellUnite = row.createCell(7);
            cellUnite.setCellValue("unite");
            Cell cellPrixUnitaire = row.createCell(8);
            cellPrixUnitaire.setCellValue("prixUnitaire");
            Cell cellAnneeRef = row.createCell(9);
            cellAnneeRef.setCellValue("anneeRef");
            
            //TRAITEMENT
            //Variables pour insertion dans l'EXCEL
            String idDescriptif;
            String operation;
            String nomDescriptif;
            int nbRow = 1;
            
            //Pour chaque tableau du WORD
            List<XWPFTable> listeTableaux = word.getTables();
            for (XWPFTable tableau : listeTableaux) {
                //Si l'élement est un ouvrage/prestation
                List<XWPFTableRow> listeRows = tableau.getRows();
                if(listeRows.size() > 2 && (listeRows.get(2).getCell(0).getText().equals("OUVRAGE") || listeRows.get(2).getCell(0).getText().equals("PRESTATION"))) {
                    //Importer l'id du descriptif
                    idDescriptif = listeRows.get(0).getCell(0).getText();
                    //Importer l'opération (AJOUT/SUPPR)
                    operation = listeRows.get(1).getCell(0).getText();
                    //Importer le nom du descriptif
                    nomDescriptif = listeRows.get(3).getCell(0).getText();
                    
                    //On ajoute le tuple dans l'EXCEL
                    row = sheet.createRow(nbRow);
                    cellNomDescriptif = row.createCell(0);
                    cellNomDescriptif.setCellValue(nomDescriptif);
                    cellOperation = row.createCell(1);
                    cellOperation.setCellValue(operation);
                    cellIdDescriptif = row.createCell(2);
                    cellIdDescriptif.setCellValue(idDescriptif);
                    nbRow++;
                }
            }
            
            //Sortie de l'EXCEL
            OutputStream fileOut = new FileOutputStream(rootImportFiles+"BasePrixRefImport.xls");
            excel.write(fileOut);
            
            resultat = true;
            
        } catch(Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Erreur lors de l'appel au Service TransformationWordVersExcel(String uriWord)", ex);
        }
        return resultat;
    }
    
    //! Non testé depuis la modif du champ unité
    public String ModifBasePrixRef(String nameExcel){
        //Importer le CSV
        //Parser le document cas par cas (ajout OU suppr) en sautant la premiere ligne
        //Si suppr alors on delete dans la BD
        //Si ajout on add
        //Dans tous les cas on met à jour soit listeBasePrixRefOuvrage soit listeBasePrixRefPrestation
        //Si erreur alors on affiche l'erreur correspondante
        //Si succès alors on retourne "succes"
        
        JpaUtil.creerContextePersistance();
        
        String rapport = null;
        BufferedReader fileReader = null;
        String uriExcel = rootImportFiles+nameExcel;
         
        //Delimiter used in CSV file
        final String DELIMITER = ";";
        try
        {
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(uriExcel));
             
            //We skip the first line then we read the file line by line
            String line = fileReader.readLine();
            while ((line = fileReader.readLine()) != null) 
            {
                //Get all attributes available in line
                String[] attributes = line.split(DELIMITER);
                
                //On cherche à savoir s'il s'agit d'un ouvrage ou d'une prestation
                String typeDescriptif = null;
                if(attributes[2].length() == 16) //16 caractères dans un ID d'ouvrage 
                    typeDescriptif = "Ouvrage";
                else
                    typeDescriptif = "Prestation";
                
                //Utile pour la suite
                BasePrixRef newBasePrixRef = null;
                Ouvrage ouvrageAModifier = null;
                Prestation prestationAModifier = null;
                List<BasePrixRef> listeBasePrixRef = null;
                Boolean testDejaPresent = false;
                
                switch(attributes[1])
                {
                    case "AJOUT":
                        try {
                            JpaUtil.ouvrirTransaction();
                            //Si des valeurs chaines vides alors on le prend en compte pour la BDD en tant que null
                            Integer annee;
                            if(attributes[9].equals("-"))
                                annee = null;
                            else
                                annee = Integer.parseInt(attributes[9]);
                            Long nbPrixRef = Long.parseLong(attributes[3]);
                            Double BT;
                            if(attributes[4].equals("-"))
                                BT = null;
                            else
                                BT = Double.parseDouble(attributes[4]);
                            Double qteInf;
                            if(attributes[5].equals("-"))
                                qteInf = null;
                            else
                                qteInf = Double.parseDouble(attributes[5]);
                            Double qteSup;
                            if(attributes[6].equals("-"))
                                qteSup = null;
                            else
                                qteSup = Double.parseDouble(attributes[6]);
                            Double prixUnitaire;
                            if(attributes[8].equals("-"))
                                prixUnitaire = null;
                            else
                                prixUnitaire = Double.parseDouble(attributes[8]);
                            
                            //On vérifie si le prix en question n'existe pas déjà par ailleurs dans la BD
                            if(typeDescriptif.equals("Ouvrage")) {
                                ouvrageAModifier = (Ouvrage)descriptifDao.ChercherParId(attributes[2]);
                                listeBasePrixRef = ouvrageAModifier.getListeBasePrixRefOuvrage();
                                for(BasePrixRef basePrixRefParcours : listeBasePrixRef) {
                                    if(basePrixRefParcours.getNbPrixRef().compareTo(nbPrixRef) == 0) {
                                        //On écrase donc le basePrixRef en question
                                        basePrixRefParcours.setAnnee(annee);
                                        basePrixRefParcours.setBT(BT);
                                        basePrixRefParcours.setQteInf(qteInf);
                                        basePrixRefParcours.setQteSup(qteSup);
                                        basePrixRefParcours.setPrixUnitaire(prixUnitaire);
                                        ouvrageAModifier.setListeBasePrixRefOuvrage(listeBasePrixRef);
                                        descriptifDao.Update(ouvrageAModifier);
                                        basePrixRefDao.Update(basePrixRefParcours);
                                        testDejaPresent = true;
                                        break;
                                    }
                                }
                            } else {
                                prestationAModifier = (Prestation)descriptifDao.ChercherParId(attributes[2]);
                                listeBasePrixRef = prestationAModifier.getListeBasePrixRefPrestation();
                                for(BasePrixRef basePrixRefParcours : listeBasePrixRef) {
                                    if(basePrixRefParcours.getNbPrixRef().compareTo(nbPrixRef) == 0) {
                                        //On écrase donc le basePrixRef en question
                                        basePrixRefParcours.setAnnee(annee);
                                        basePrixRefParcours.setBT(BT);
                                        basePrixRefParcours.setQteInf(qteInf);
                                        basePrixRefParcours.setQteSup(qteSup);
                                        basePrixRefParcours.setPrixUnitaire(prixUnitaire);
                                        prestationAModifier.setListeBasePrixRefPrestation(listeBasePrixRef);
                                        descriptifDao.Update(prestationAModifier);
                                        basePrixRefDao.Update(basePrixRefParcours);
                                        testDejaPresent = true;
                                        break;
                                    }
                                }
                            }
                            if(!testDejaPresent) {
                                newBasePrixRef = new BasePrixRef(annee, Long.parseLong(attributes[3]), BT, qteInf, qteSup, prixUnitaire);
                                basePrixRefDao.Creer(newBasePrixRef);
                                if(typeDescriptif.equals("Ouvrage")) {
                                    listeBasePrixRef.add(newBasePrixRef);
                                    ouvrageAModifier.setListeBasePrixRefOuvrage(listeBasePrixRef);
                                    descriptifDao.Update(ouvrageAModifier);
                                } else {
                                    listeBasePrixRef.add(newBasePrixRef);
                                    prestationAModifier.setListeBasePrixRefPrestation(listeBasePrixRef);
                                    descriptifDao.Update(prestationAModifier);
                                }
                            }
                            JpaUtil.validerTransaction();
                        } catch (Exception ex) {
                            rapport += " | Erreur lors de l'insertion de " + attributes[0];
                            throw new Exception();
                        }
                        break;
                    case "SUPPR":
                        try {
                            JpaUtil.ouvrirTransaction();
                            if(typeDescriptif.equals("Ouvrage")) {
                                ouvrageAModifier = (Ouvrage)descriptifDao.ChercherParId(attributes[2]);
                                listeBasePrixRef = ouvrageAModifier.getListeBasePrixRefOuvrage();
                                //On va chercher le prix qui nous interesse
                                for(BasePrixRef basePrixRefParcours : listeBasePrixRef) {
                                    if(basePrixRefParcours.getNbPrixRef().compareTo(Long.parseLong(attributes[3])) == 0) {
                                        //On supprime d'abord le lien entre l'ouvrage et le prix, puis on supprime le prix
                                        listeBasePrixRef.remove(basePrixRefParcours);
                                        ouvrageAModifier.setListeBasePrixRefOuvrage(listeBasePrixRef);
                                        descriptifDao.Update(ouvrageAModifier);
                                        basePrixRefDao.Remove(basePrixRefParcours);
                                        break;
                                    }
                                }
                            } else {
                                prestationAModifier = (Prestation)descriptifDao.ChercherParId(attributes[2]);
                                listeBasePrixRef = prestationAModifier.getListeBasePrixRefPrestation();
                                //On va chercher le prix qui nous interesse
                                for(BasePrixRef basePrixRefParcours : listeBasePrixRef) {
                                    if(basePrixRefParcours.getNbPrixRef().compareTo(Long.parseLong(attributes[3])) == 0) {
                                        //On supprime d'abord le lien entre l'ouvrage et le prix, puis on supprime le prix
                                        listeBasePrixRef.remove(basePrixRefParcours);
                                        prestationAModifier.setListeBasePrixRefPrestation(listeBasePrixRef);
                                        descriptifDao.Update(prestationAModifier);
                                        basePrixRefDao.Remove(basePrixRefParcours);
                                        break;
                                    }
                                }
                            }
                            JpaUtil.validerTransaction();
                        } catch (Exception ex) {
                            rapport += " | Erreur lors de la suppression de " + attributes[0];
                            throw new Exception();
                        }
                        break;
                    default:
                        rapport += " opération non reconnue";
                        break;
                }
            }
        } 
        catch (Exception e) {
            //L'exception aura déjà été collectée et traitée en amont
        } 
        finally
        {
            JpaUtil.fermerContextePersistance();
            if(rapport == null)
                rapport = "Succès !";
            try {
                fileReader.close();
            } catch (IOException e) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Erreur lors de la fermeture du fichier", e);
            }
        }
        return rapport;
    }
}
