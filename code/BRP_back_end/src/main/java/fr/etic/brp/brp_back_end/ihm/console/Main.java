package fr.etic.brp.brp_back_end.ihm.console;

import com.google.common.hash.Hashing;
import fr.etic.brp.brp_back_end.dao.DomUtil;
import fr.etic.brp.brp_back_end.dao.JpaUtil;
import fr.etic.brp.brp_back_end.metier.modele.BasePrixRef;
import fr.etic.brp.brp_back_end.metier.modele.CaractDim;
import fr.etic.brp.brp_back_end.metier.modele.Categorie;
import fr.etic.brp.brp_back_end.metier.modele.CategorieConstruction;
import fr.etic.brp.brp_back_end.metier.modele.CoeffRaccordement;
import fr.etic.brp.brp_back_end.metier.modele.Chapitre;
import fr.etic.brp.brp_back_end.metier.modele.Descriptif;
import fr.etic.brp.brp_back_end.metier.modele.Famille;
import fr.etic.brp.brp_back_end.metier.modele.Generique;
import fr.etic.brp.brp_back_end.metier.modele.Operateur;
import fr.etic.brp.brp_back_end.metier.modele.Ouvrage;
import fr.etic.brp.brp_back_end.metier.modele.Prestation;
import fr.etic.brp.brp_back_end.metier.modele.Projet;
import fr.etic.brp.brp_back_end.metier.modele.Projet.Site;
import fr.etic.brp.brp_back_end.metier.modele.Projet.TypeConstruction;
import fr.etic.brp.brp_back_end.metier.modele.Projet.TypeLot;
import fr.etic.brp.brp_back_end.metier.modele.Projet.TypeMarche;
import fr.etic.brp.brp_back_end.metier.modele.SousCategorieConstruction;
import fr.etic.brp.brp_back_end.metier.modele.SousFamille;
import fr.etic.brp.brp_back_end.metier.service.ExportService;
import fr.etic.brp.brp_back_end.metier.service.ImportService;
import fr.etic.brp.brp_back_end.metier.service.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 *
 * @author louisrob
 */
public class Main {
    
    public static void main(String[] args) throws IOException {

        // Contrôlez l'affichage du log de JpaUtil grâce à la méthode log de la classe JpaUtil
        JpaUtil.init();
        DomUtil.init();

    //------------initialisations------------------
    
        // A faire tout le temps (l'ordre est important)
       //InitialiserBasePrixRef();
       InitialiserCaractDim();
       InitialiserSousCategorieConstruction();
       //InitialiserCategorie();
       InitialiserCategorieConstruction();
       InitialiserCoeffRaccordement();
       //InitialiserChapitre();
       //InitialiserDescriptif();
       //InitialiserFamille();
       InitialiserOperateur();
       //InitialiserProjets();
       //InitialiserSousFamille();
        
    //----------tests-des-services-----------------
    
//        testerModifBaseDescriptif();
//        testerModifBasePrixRef();
//        testerListerBasePrixRefs();
//        testerListerCaractDims();
//        testerListerCategories();
//        testerListerCategorieConstructions();
//        testerListerCoeffRaccordements();
//        testerListerChapitres();
//        testerListerDescriptifs();
//        testerListerFamilles();
//        testerListerOperateurs();
//        testerListerPrestations();
//        testerListerProjets();
//        testerListerSousCategorieConstructions();
//        testerListerSousFamilles();
//        testerAuthentifierOperateur();
//        testerCreerProjet();
//        testerRechercherProjetParId();
//        testerDupliquerProjet();
//        testerEditerNomProjet();
//        testerEditerInfoEnumProjet();
//        testerEditerDateProjet();
//        testerEditerCoeffAdaptProjet();
//        testerEditerCoeffRaccordementProjet();
//        testerEditerCategorieConstructionProjet();
//        testerTransformationWordVersExcel();
//        testerAjouterLot();
//        testerAjouterTitre1();
//        testerAjouterTitre2();
//        testerAjouterTitre3();
//        testerAjouterTitre4();
//        testerAjouterDescriptif();
//        testerAjouterLigneChiffrage();
//        testerCoutSynthese();
//        testerSuppressionBalise();
//        testerModifierIntituleTitre();
//        testerSupprimerLigneChiffrage();          
//        testerModifierDescriptionDescriptif();
//        testerModifierCourteDescriptionDescriptif();
//        testerModifierLocalisationDescriptif();
//        testerModifierQuantiteDescriptif();      
//        testerModifierPrixLigneChiffrage();

//        testerExporterProjet();

      //----------Scenarii----------//
        
//        Scenario1();                     
//        Scenario2();
//        Scenario3();

        JpaUtil.destroy();
        DomUtil.destroy();
    }
    
//------------------------------------------------------------------------------    
//------------------------------- SCENARII -------------------------------------
//------------------------------------------------------------------------------

//Explication du scénario n°1 (classique pour dérouler)
public static void Scenario1() {
    
    // - Importer des Chapitres/Catégories/.../Descriptifs
    System.out.println();
    System.out.println("------------  Import Descriptifs  -------------");
    ImportService importService = new ImportService();
    Service service = new Service();
    String msgSuppr = "";
    String uriWord = "../import_files/XX_Jeu_Test_BRP_v0.2.docx";
    
    //returnListe[0] = status
    //les autres contiennent les identifiants à supprimer
    ArrayList<String> returnListe = importService.ModifBaseDescriptif(uriWord);
    
    //les ajouts se sont bien passés, on passe aux suppressions
    if(returnListe.get(0).equals("Succes")){
        System.out.println("Succès ajout en BD");
        int testSuppr = 0;
        for(int i = 1; i < returnListe.size(); i++){
            msgSuppr = importService.CompterEnfants(returnListe.get(i));
            //on envoie au comptage des enfants
            if(msgSuppr.equals("suppr ok")){ //on supprime direct car pas d'enfant
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
            else{ //on demande la permission au client
                System.out.println(msgSuppr);
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
        }
        if((testSuppr+1) == returnListe.size())
                System.out.println("Suppressions ok");
    }
    else{       //on affiche l'erreur
        System.out.println(returnListe.get(0));
    }
    System.out.println();
           
    // - Importer des prixRef liés aux Descriptifs
    System.out.println("------------  Import prix  -------------");
    String uriExcel = "../import_files/templateBasePrix_jeau_test_0.2.csv";
    String msgState = importService.ModifBasePrixRef(uriExcel);
    System.out.println(msgState);
    System.out.println();

    // - Créer un Projet n°1
    System.out.println("------------  Creation projet 1  -------------");
    String nomProjet = "projet1";
    Long idProjet = service.CreerProjet(nomProjet);
    if (idProjet != -1) 
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    // - Editer infos Projet (nouveau nom, typeMarche, date, coeffAdapt, coeffRaccordement, categorieConstruction)
    System.out.println("------------  Editer Projet --------------");
    
    String nouveauNomProjet = "nouveauNomProjet1";
    Boolean resultat = service.EditerNomProjet(idProjet, nouveauNomProjet);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    resultat = service.EditerInfoEnumProjet(idProjet, "TypeMarche", "marchePublic");
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    resultat = service.EditerCoeffAdaptProjet(idProjet, 9.0F);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    resultat = service.EditerDateProjet(idProjet, new Date());
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    CoeffRaccordement coeffRaccordement = service.ListerCoeffRaccordements().get(0);
    resultat = service.EditerCoeffRaccordementProjet(idProjet, coeffRaccordement.getIdCoeffRaccordement());
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    CategorieConstruction categorieConstruction = service.ListerCategorieConstructions().get(0);
    resultat = service.EditerCategorieConstructionProjet(idProjet, categorieConstruction.getCodeCategorieConstruction());
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    // - Ajouter une arbo pour le projet n°1 ainsi que des descriptifs (avec double localisation)
    //Lot 1
    String placement = "APPEND";
    String idRefPlacement = "";
    resultat = service.AjouterLot(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Lot 2
    resultat = service.AjouterLot(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre1
    placement = "APPEND";
    idRefPlacement = "_1";
    resultat = service.AjouterTitre1(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre2
    placement = "APPEND";
    idRefPlacement = "_3";
    resultat = service.AjouterTitre2(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre3
    placement = "APPEND";
    idRefPlacement = "_4";
    resultat = service.AjouterTitre3(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre4
    placement = "APPEND";
    idRefPlacement = "_5";
    resultat = service.AjouterTitre4(idProjet, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Descriptif
    placement = "APPEND";
    idRefPlacement = "_6";
    resultat = service.AjouterDescriptif(idProjet, placement, idRefPlacement, "04_ETA_01_11_001_001");
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Ajout d'une ligneChiffrage
    resultat = service.AjouterLigneChiffrage(idProjet, "04_ETA_01_11_001_001");
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("EchecLigneChiffrage");
    System.out.println();
    
    // - Exporter le projet n°1
    System.out.println();
    System.out.println("------------  Export Projet  -------------");
    ExportService exportService = new ExportService();
    
    ArrayList<String> res = exportService.ExporterProjet(idProjet, 1, "../../../../code/BRP_front_end/src/main/webapp/XMLfiles/" + idProjet + ".xml");
    if (!res.isEmpty())
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
}
    
//Explication du scénario n°2 (duplication)
public static void Scenario2() {
        
    // - Importer des Chapitres/Catégories/.../Descriptifs
    System.out.println();
    System.out.println("------------  Import Descriptifs  -------------");
    ImportService importService = new ImportService();
    Service service = new Service();
    String msgSuppr = "";
    String uriWord = "../import_files/XX_Jeu_Test_BRP_v0.2.docx";

    //returnListe[0] = status
    //les autres contiennent les identifiants à supprimer
    ArrayList<String> returnListe = importService.ModifBaseDescriptif(uriWord);

    //les ajouts se sont bien passés, on passe aux suppressions
    if(returnListe.get(0).equals("Succes")){
        System.out.println("Succès ajout en BD");
        int testSuppr = 0;
        for(int i = 1; i < returnListe.size(); i++){
            msgSuppr = importService.CompterEnfants(returnListe.get(i));
            //on envoie au comptage des enfants
            if(msgSuppr.equals("suppr ok")){ //on supprime direct car pas d'enfant
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
            else{ //on demande la permission au client
                System.out.println(msgSuppr);
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
        }
        if((testSuppr+1) == returnListe.size())
                System.out.println("Suppressions ok");
    }
    else{       //on affiche l'erreur
        System.out.println(returnListe.get(0));
    }
    System.out.println();
           
    // - Importer des prixRef liés aux Descriptifs
    System.out.println("------------  Import prix  -------------");
    String uriExcel = "../import_files/templateBasePrix_jeau_test_0.2.csv";
    String msgState = importService.ModifBasePrixRef(uriExcel);
    System.out.println(msgState);
    System.out.println();

    // - Créer un Projet n°1
    System.out.println("------------  Creation projet 1  -------------");
    String nomProjet = "projet1";
    Long idProjet = service.CreerProjet(nomProjet);
    if (idProjet > 0) 
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    

    // - Dupliquer ce projet en n°2
    System.out.println("------------  Dupliquer projet 1  -------------");
    String nomProjet2 = "projet2";
    Long idProjet2 = service.DupliquerProjet(idProjet, nomProjet2);
    if(idProjet2 > 0)
        System.out.println("Succès");
    else 
        System.out.println("Erreur");
    System.out.println();
    
    // - Renommer le projet n°2   
    System.out.println("------------  Renommer projet 2  -------------");
    String newNomProjet2 = "Nouveau projet2";
    Boolean testEditerNom = service.EditerNomProjet(idProjet2, newNomProjet2);
    if(testEditerNom)
        System.out.println("Succès");
    else 
        System.out.println("Erreur");
    System.out.println();
    
    // - Ajouter une arbo Simple pour le projet n°2 ainsi que des descriptifs
    System.out.println("------------  Ajout Arbo projet 2  -------------");
    //Lot 1
    String placement = "APPEND";
    String idRefPlacement = "";
    Boolean resultat = service.AjouterLot(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Lot 2
    resultat = service.AjouterLot(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre1
    placement = "APPEND";
    idRefPlacement = "_1";
    resultat = service.AjouterTitre1(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre2
    placement = "APPEND";
    idRefPlacement = "_3";
    resultat = service.AjouterTitre2(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre3
    placement = "APPEND";
    idRefPlacement = "_4";
    resultat = service.AjouterTitre3(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    //Titre4
    placement = "APPEND";
    idRefPlacement = "_5";
    resultat = service.AjouterTitre4(idProjet2, placement, idRefPlacement);
    if (resultat)
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    // - Dupliquer le n°2 en n°3
    System.out.println("------------  Dupliquer projet 2  -------------");
    String nomProjet3 = "projet3";
    Long idProjet3 = service.DupliquerProjet(idProjet2, nomProjet3);
    if(idProjet3 > 0)
        System.out.println("Succès");
    else 
        System.out.println("Erreur");
    System.out.println();
}

//Explication du scénario n°3 (suppression)
public static void Scenario3() {
        
    // - Importer des Chapitres/Catégories/.../Descriptifs
    System.out.println();
    System.out.println("------------  Import Descriptifs  -------------");
    ImportService importService = new ImportService();
    Service service = new Service();
    String msgSuppr = "";
    String uriWord = "../import_files/XX_Jeu_Test_BRP_v0.2.docx";

    //returnListe[0] = status
    //les autres contiennent les identifiants à supprimer
    ArrayList<String> returnListe = importService.ModifBaseDescriptif(uriWord);

    //les ajouts se sont bien passés, on passe aux suppressions
    if(returnListe.get(0).equals("Succes")){
        System.out.println("Succès ajout en BD");
        int testSuppr = 0;
        for(int i = 1; i < returnListe.size(); i++){
            msgSuppr = importService.CompterEnfants(returnListe.get(i));
            //on envoie au comptage des enfants
            if(msgSuppr.equals("suppr ok")){ //on supprime direct car pas d'enfant
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
            else{ //on demande la permission au client
                // - Supprimer des descriptifs de la BD
                // - Supprimer des sous-familles/Chapitres etc de la BD
                System.out.println(msgSuppr);
                if(importService.SupprObjet(returnListe.get(i)).equals("Succes"))
                    testSuppr++;
                else
                    System.out.println(importService.SupprObjet(returnListe.get(i)));
            }
        }
        if((testSuppr+1) == returnListe.size())
                System.out.println("Suppressions ok");
    }
    else{       //on affiche l'erreur
        System.out.println(returnListe.get(0));
    }
    System.out.println();
           
    // - Importer des prixRef liés aux Descriptifs (erreur pour ceux qui ont été supprimés en amont)
    System.out.println("------------  Import prix  -------------");
    String uriExcel = "../import_files/templateBasePrix_jeau_test_0.2.csv";
    String msgState = importService.ModifBasePrixRef(uriExcel);
    System.out.println(msgState);
    System.out.println();

    // - Créer un projet
    System.out.println("------------  Creation projet 1  -------------");
    String nomProjet = "projet1";
    Long idProjet = service.CreerProjet(nomProjet);
    if (idProjet > 0) 
        System.out.println("Succès");
    else 
        System.out.println("Echec");
    System.out.println();
    
    // - Supprimer un projet -> Benoit
    
    
}
//------------------------------------------------------------------------------    
//---------------------------- INITIALISATIONS ---------------------------------
//------------------------------------------------------------------------------
    
    public static void InitialiserBasePrixRef() {
        
        System.out.println();
        System.out.println("**** InitialiserBasePrixRef() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        BasePrixRef basePrixRef = new BasePrixRef(2018, 1L, 1.0, 1.0, 3.0, 10.0);
        BasePrixRef basePrixRef2 = new BasePrixRef(2017, 2L, 1.0, 4.0, 6.0, 4.0);
        BasePrixRef basePrixRef3 = new BasePrixRef(2019, 1L, 1.0, null, null, null);
        
        System.out.println("** BasePrixRef avant persistance: ");
        afficherBasePrixRef(basePrixRef);
        afficherBasePrixRef(basePrixRef2);
        afficherBasePrixRef(basePrixRef3);
        System.out.println();
        

        try {
            em.getTransaction().begin();
            em.persist(basePrixRef);
            em.persist(basePrixRef2);
            em.persist(basePrixRef3);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserBasePrixRef()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** BasePrixRef après persistance: ");
        afficherBasePrixRef(basePrixRef);
        afficherBasePrixRef(basePrixRef2);
        afficherBasePrixRef(basePrixRef3);
        System.out.println();
    }
    public static void InitialiserProjets() {
        
        System.out.println();
        System.out.println("**** InitialiserProjets() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        Projet projet1 = new Projet("nomProjet1");
        TypeMarche typeMarche = TypeMarche.marchePublic;
        projet1.setTypeMarche(typeMarche);
        TypeConstruction typeConstruction = TypeConstruction.renovation;
        projet1.setTypeConstruction(typeConstruction);
        TypeLot typeLot = TypeLot.lotSepare;
        projet1.setTypeLot(typeLot);
        Site site = Site.libre;
        projet1.setSite(site);
        projet1.setDatePrixRef(new Date());
        projet1.setCoeffAdapt(1F);
        
        System.out.println("** Projets avant persistance: ");
        afficherProjet(projet1);
        System.out.println();

        try {
            em.getTransaction().begin();
            
            CategorieConstruction categorieConstruction = em.find(CategorieConstruction.class, 1L);
            projet1.setCategorieConstruction(categorieConstruction);
            
            CoeffRaccordement coeffRaccordement = em.find(CoeffRaccordement.class, 1L);
            projet1.setCoeffRaccordement(coeffRaccordement);
            
            em.persist(projet1);
            
            //On crée le XML associé
            String uri = "../XMLfiles/1.xml";
            DocumentBuilder builder = DomUtil.obtenirBuilder();
            Document xml = builder.newDocument();
            
            //Création de la racine
            Element baliseProjet = xml.createElement("projet");
            baliseProjet.setAttribute("idProjet", "1");
            xml.appendChild(baliseProjet);
            
            //Création de la balise nextId init à 1
            Element nextIdBalise = xml.createElement("nextId");
            nextIdBalise.appendChild(xml.createTextNode("1"));
            baliseProjet.appendChild(nextIdBalise);
            
            //Ecriture du XML
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
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserProjets()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Projets après persistance: ");
        afficherProjet(projet1);
        System.out.println();
    }
    public static void InitialiserCaractDim() {
        
        System.out.println();
        System.out.println("**** InitialiserCaractDim() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        CaractDim caractDim1 = new CaractDim("codeCaractDim1", 1.0);
         
        System.out.println("** CaractDim avant persistance: ");
        afficherCaractDim(caractDim1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(caractDim1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserCaractDim()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** CaractDim après persistance: ");
        afficherCaractDim(caractDim1);
        System.out.println();
    }
    public static void InitialiserCategorie() {
        
        System.out.println();
        System.out.println("**** InitialiserCategorie() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        Categorie categorie1 = new Categorie("02_AAA","intitule1");
         
        System.out.println("** Categorie avant persistance: ");
        afficherCategorie(categorie1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(categorie1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service initialiserCategories()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Categorie après persistance: ");
        afficherCategorie(categorie1);
        System.out.println();
    }
    public static void InitialiserCoeffRaccordement() {
        
        System.out.println();
        System.out.println("**** initialiserCoeffRaccordement() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        CoeffRaccordement coeffRaccordement1 = new CoeffRaccordement("Paris", 1.0);
         
        System.out.println("** CoeffRaccordement avant persistance: ");
        afficherCoeffRaccordement(coeffRaccordement1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(coeffRaccordement1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service initialiserCoeffRaccordement()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** CoeffRaccordement après persistance: ");
        afficherCoeffRaccordement(coeffRaccordement1);
        System.out.println();
    }
    public static void InitialiserDescriptif() {
        
        System.out.println();
        System.out.println("**** InitialiserDescriptif() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        Generique generique1 = new Generique("02_AAA_01_01_01", "nomDescriptif1", "descriptionGenerique1", "courteDescriptionGenerique1");
        Ouvrage ouvrage1 = new Ouvrage("02_AAA_01_01_02", "nomOuvrage1", "<p><normal>Un peu de texte normal et un peu de texte </normal><u>souligné </u><normal>lol</normal></p><p><u>Souligné</u></p><p><underlineDash>Souligné trait-tillé</underlineDash></p><p><i>Italique</i></p><p><italic_underline>Italique souligné</italic_underline></p><p><b>Gras</b></p><p><bold_underline>Gras souligné</bold_underline></p><p><bold_italic>Gras italique</bold_italic></p><p><bold_underline_italic>Gras italique souligné</bold_underline_italic></p><li><normal>Puce 1</normal></li><li><normal>Puce 2</normal></li><p><colorRed>Texte rouge</colorRed></p><p><colorOrange>Texte orange</colorOrange></p><p><colorGreen>Texte vert</colorGreen></p><p><colorBlue>Texte bleu</colorBlue></p><p><highlightYellow>Surligné jaune</highlightYellow></p><p><highlightCyan>Surligné bleu</highlightCyan></p><p><highlightRed>Surligné orange</highlightRed></p><p><highlightGreen>Surligné vert</highlightGreen></p><p><highlightMagenta>Surligné violet</highlightMagenta></p><p><highlightGrey>Surligné gris</highlightGrey></p>", "courteDescriptionOuvrage1", "m2");
        Prestation prestation1 = new Prestation("02_AAA_01_01_02_01", "nomPrestation1", "descriptionPrestation1", "courteDescriptionPrestation1", "m2");
         
        System.out.println("** Descriptif avant persistance: ");
        afficherGenerique(generique1);
        afficherOuvrage(ouvrage1);
        afficherPrestation(prestation1);
        System.out.println();

        try {
            em.getTransaction().begin();
            //ajout des basePrixRef à un ouvrage et à une prestation
            List<BasePrixRef> listeBasePrixRefOuvrage = new ArrayList();
            listeBasePrixRefOuvrage.add(em.find(BasePrixRef.class, 1L));
            listeBasePrixRefOuvrage.add(em.find(BasePrixRef.class, 2L));
            ouvrage1.setListeBasePrixRefOuvrage(listeBasePrixRefOuvrage);
            
            List<BasePrixRef> listeBasePrixRefPrestation = new ArrayList();
            listeBasePrixRefPrestation.add(em.find(BasePrixRef.class, 3L));
            prestation1.setListeBasePrixRefPrestation(listeBasePrixRefPrestation);
            
            em.persist(generique1);
            em.persist(ouvrage1);
            em.persist(prestation1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserDescriptif()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Descriptif après persistance: ");
        afficherGenerique(generique1);
        afficherOuvrage(ouvrage1);
        afficherPrestation(prestation1);
        System.out.println();
    }
    public static void InitialiserSousFamille() {
        
        System.out.println();
        System.out.println("**** InitialiserSousFamille() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        SousFamille sousFamille1 = new SousFamille("02_AAA_01_01","intituleSousFamille1");
         
        System.out.println("** SousFamille avant persistance: ");
        afficherSousFamille(sousFamille1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(sousFamille1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserSousFamille()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** SousFamille après persistance: ");
        afficherSousFamille(sousFamille1);
        System.out.println();
    }
    public static void InitialiserCategorieConstruction() {
        
        System.out.println();
        System.out.println("**** initialiserCategorieConstruction() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        CategorieConstruction categorieConstruction1 = new CategorieConstruction("codeCategorieConstruction1", "AAA");
        
        System.out.println("** CategorieConstruction avant persistance: ");
        afficherCategorieConstruction(categorieConstruction1);
        System.out.println();

        try {
            em.getTransaction().begin();
            
            SousCategorieConstruction sousCategorieConstruction = em.find(SousCategorieConstruction.class, 1L);
            List<SousCategorieConstruction> listeSousCategorieConstruction = new ArrayList<>();
            listeSousCategorieConstruction.add(0, sousCategorieConstruction);
            categorieConstruction1.setListeSousCategorieConstruction(listeSousCategorieConstruction);
            
            CaractDim caractDim = em.find(CaractDim.class, 1L);
            List<CaractDim> listeCaractDim = new ArrayList<>();
            listeCaractDim.add(0, caractDim);
            categorieConstruction1.setListeCaractDim(listeCaractDim);
            
            em.persist(categorieConstruction1);
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service initialiserCategorieConstruction()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** CategorieConstruction après persistance: ");
        afficherCategorieConstruction(categorieConstruction1);
        System.out.println();
    } 
    public static void InitialiserChapitre() {
        
        System.out.println();
        System.out.println("**** initialiserChapitre() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        Chapitre chapitre1 = new Chapitre("01","chapitre1");
        Chapitre chapitre2 = new Chapitre("02","chapitre2"); 
        
        System.out.println("** Chapitre avant persistance: ");
        afficherChapitre(chapitre1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(chapitre1);
            em.getTransaction().commit();
            
            em.getTransaction().begin();    //REMOVE
            em.persist(chapitre2);         //REMOVE
            em.getTransaction().commit();   //REMOVE
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service initialiserChapitre()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Chapitre après persistance: ");
        afficherChapitre(chapitre1);
        System.out.println();
    }
    public static void InitialiserFamille() {
        
        System.out.println();
        System.out.println("**** InitialiserFamille() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        Famille famille1 = new Famille("02_AAA_01","famille1");
         
        System.out.println("** Chapitre avant persistance: ");
        afficherFamille(famille1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(famille1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserFamille()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Famille après persistance: ");
        afficherFamille(famille1);
        System.out.println();
    }
    public static void InitialiserOperateur() {
        
        System.out.println();
        System.out.println("**** InitialiserOperateur() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();     
        
        int salt = (int)(Math.random()*1000);
        String mdpConcat = "admin"+salt;
        String mdpHash = Hashing.sha256().hashString(mdpConcat, StandardCharsets.UTF_8).toString();
        Boolean isAdmin = true;
        
        Operateur admin = new Operateur("admin0", mdpHash, salt, "admin0", isAdmin);
        admin.setAdmin(Boolean.TRUE);
         
        System.out.println("** Operateur avant persistance: ");
        afficherOperateur(admin);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(admin);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserOperateur()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** Operateur après persistance: ");
        afficherOperateur(admin);
        System.out.println();
    }
    public static void InitialiserSousCategorieConstruction() {
        
        System.out.println();
        System.out.println("**** InitialiserSousCategorieConstruction() ****");
        System.out.println();
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BRP_PU");
        EntityManager em = emf.createEntityManager();        
        
        SousCategorieConstruction sousCategorieConstruction1 = new SousCategorieConstruction("sousCategorieConstruction1");
         
        System.out.println("** SousCategorieConstruction avant persistance: ");
        afficherSousCategorieConstruction(sousCategorieConstruction1);
        System.out.println();

        try {
            em.getTransaction().begin();
            em.persist(sousCategorieConstruction1);
            em.getTransaction().commit();
        } catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Exception lors de l'appel au Service InitialiserSousCategorieConstruction()", ex);
            try {
                em.getTransaction().rollback();
            }
            catch (IllegalStateException ex2) {
                // Ignorer cette exception...
            }
        } finally {
            em.close();
        }
        
        System.out.println("** SousCategorieConstruction après persistance: ");
        afficherSousCategorieConstruction(sousCategorieConstruction1);
        System.out.println();
    }
    
//------------------------------------------------------------------------------
//------------------------------ TESTS DES SERVICES  ---------------------------
//------------------------------------------------------------------------------
   
    public static void testerAuthentifierOperateur() {
        
        System.out.println();
        System.out.println("**** testerAuthentifierOperateur() ****");
        System.out.println();
        
        Service service = new Service();
        Operateur operateur;
        String mail;
        String mdpEntre;

        mail = "quentinmarc@orange.fr";
        mdpEntre = "monMDP";
        operateur = service.AuthentifierOperateur(mail, mdpEntre);
        if (operateur != null) {
            System.out.println("Authentification réussie avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
            afficherOperateur(operateur);
        }
        else {
            System.out.println("Authentification échouée avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
        }

        mail = "ada.lovelace@insa-lyon.fr";
        mdpEntre = "Ada2020";
        operateur = service.AuthentifierOperateur(mail, mdpEntre);
        if (operateur != null) {
            System.out.println("Authentification réussie avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
            afficherOperateur(operateur);
        }
        else {
            System.out.println("Authentification échouée avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
        }
        
        mail = "quentinmarc@orange.fr";
        mdpEntre = "Ada2020";
        operateur = service.AuthentifierOperateur(mail, mdpEntre);
        if (operateur != null) {
            System.out.println("Authentification réussie avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
            afficherOperateur(operateur);
        }
        else {
            System.out.println("Authentification échouée avec le mail '" + mail + "' et le mot de passe '" + mdpEntre + "'");
        }
    }
    
    public static void testerCreerProjet() {
        
        System.out.println();
        System.out.println("**** testerCreerProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        String nomProjet = "projet1";

        Long projetTest1 = service.CreerProjet(nomProjet);
        if (projetTest1 != -1) {
            System.out.println("Succès: création du projet: " + nomProjet);
        }
        else {
            System.out.println("Echec: impossible de créer: " + nomProjet);
        }/*
        
        //Doit fonctionner (nom deja présent BD)
        Boolean projetTest2 = service.CreerProjet(nomProjet);
        if (projetTest2) {
            System.out.println("Succès: création du projet: "+nomProjet);
        }
        else {
            System.out.println("Echec: impossible de créer: "+nomProjet);
        }*/
        
        //modifie dans le scenario 2
//        String nomProjet = "projet1";
//
//        Boolean projetTest1 = service.CreerProjet(nomProjet);
//        if (projetTest1) {
//            System.out.println("Succès: création du projet: "+nomProjet);
//        }
//        else {
//            System.out.println("Echec: impossible de créer: "+nomProjet);
//        }
    }
    
    
    public static void testerRechercherProjetParId() {
        
        System.out.println();
        System.out.println("**** testerRechercherProjetParId() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjetTest = 1L;

        Projet projetTest1 = service.RechercherProjetParId(idProjetTest);
        if (projetTest1 != null) {
            System.out.println("Ouverture avec succès de" + projetTest1.getNomProjet());
            afficherProjet(projetTest1);
        }
        else {
            System.out.println("Aucun projet trouvé !");
        }
        
        //Ne doit pas fonctionner
        idProjetTest = 2L;

        Projet projetTest2 = service.RechercherProjetParId(idProjetTest);
        if (projetTest2 != null) {
            System.out.println("Ouverture avec succès de" + projetTest2.getNomProjet());
            afficherProjet(projetTest2);
        }
        else {
            System.out.println("Aucun projet correspondant à l'id" + idProjetTest + " !");
        }
    }
    
    public static void testerDupliquerProjet() {
        
        System.out.println();
        System.out.println("**** testerDupliquerProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        
        //a été modifié dans le scenario 2
//        Boolean resultat = service.DupliquerProjet(idProjet);
//        if(resultat)
//        {
//            System.out.println("Duplication avec succès du projet n°" + idProjet);
//        } else {
//            System.out.println("Erreur de duplication du projet n°" + idProjet);
//        }
//       
//        //Ne doit pas fonctionner
//        Long idProjet2 = 100L;
//        
//        Boolean resultat2 = service.DupliquerProjet(idProjet2);
//        if(resultat2)
//        {
//            System.out.println("Duplication avec succès du projet n°" + idProjet2);
//        } else {
//            System.out.println("Erreur de duplication du projet n°" + idProjet2);
//        }
    }
    
    public static void testerEditerNomProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerNomProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String nouveauNomProjet = "nouveauNomTest";
        
        Boolean resultat = service.EditerNomProjet(idProjet, nouveauNomProjet);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Ne doit pas fonctionner
        Long idProjet2 = 100L;
        String nouveauNomProjet2 = "nouveauNomTest2";
        
        Boolean resultat2 = service.EditerNomProjet(idProjet2, nouveauNomProjet2);
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
    }
    
    public static void testerEditerInfoEnumProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerInfoEnumProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String typeEnum = "TypeMarche";
        String valeurEnum = "marchePublic";
        
        Boolean resultat = service.EditerInfoEnumProjet(idProjet, typeEnum, valeurEnum);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Doit fonctionner
        Long idProjet2 = 1L;
        String typeEnum2 = "TypeMarche";
        String valeurEnum2 = "marchePrive";
        
        Boolean resultat2 = service.EditerInfoEnumProjet(idProjet2, typeEnum2, valeurEnum2);
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
        
        //Ne doit pas fonctionner
        Long idProjet3 = 1L;
        String typeEnum3 = "Type non existant";
        String valeurEnum3 = "valeur non existante";
        
        Boolean resultat3 = service.EditerInfoEnumProjet(idProjet3, typeEnum3, valeurEnum3);
        if(resultat3)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet3);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet3);
        }
        
        //Ne doit pas fonctionner
        Long idProjet4 = 1L;
        String typeEnum4 = "TypeMarche";
        String valeurEnum4 = "valeur non existante";
        
        Boolean resultat4 = service.EditerInfoEnumProjet(idProjet4, typeEnum4, valeurEnum4);
        if(resultat4)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet4);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet4);
        }
        
        //Doit fonctionner
        Long idProjet5 = 1L;
        String typeEnum5 = "TypeConstruction";
        String valeurEnum5 = "neuf";
        
        Boolean resultat5 = service.EditerInfoEnumProjet(idProjet5, typeEnum5, valeurEnum5);
        if(resultat5)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet5);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet5);
        }
        
        //Ne doit pas fonctionner
        Long idProjet6 = 100L;
        String typeEnum6 = "TypeConstruction";
        String valeurEnum6 = "neuf";
        
        Boolean resultat6 = service.EditerInfoEnumProjet(idProjet6, typeEnum6, valeurEnum6);
        if(resultat6)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet6);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet6);
        }
        
        //Possibilité de faire d'autres tests
    }
    
    public static void testerEditerDateProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerDateProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        
        Boolean resultat = service.EditerDateProjet(idProjet, new Date());
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Ne doit pas fonctionner
        Long idProjet2 = 100L;
        
        Boolean resultat2 = service.EditerDateProjet(idProjet2, new Date());
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
    }
    
    public static void testerEditerCoeffAdaptProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerCoeffAdaptProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        Float nouveauCoeffAdapt = 6.F;
        
        Boolean resultat = service.EditerCoeffAdaptProjet(idProjet, nouveauCoeffAdapt);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Ne doit pas fonctionner
        Long idProjet2 = 100L;
        Float nouveauCoeffAdapt2 = 6.F;
        
        Boolean resultat2 = service.EditerCoeffAdaptProjet(idProjet2, nouveauCoeffAdapt2);
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
    }
    
    public static void testerEditerCoeffRaccordementProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerCoeffRaccordementProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        Long idCoeffRaccordement = 1L;
        
        Boolean resultat = service.EditerCoeffRaccordementProjet(idProjet, idCoeffRaccordement);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Ne doit pas fonctionner
        Long idProjet2 = 100L;
        Long idCoeffRaccordement2 = 1L;
        
        Boolean resultat2 = service.EditerCoeffRaccordementProjet(idProjet2, idCoeffRaccordement2);
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
        
        //Ne doit pas fonctionner
        Long idProjet3 = 1L;
        Long idCoeffRaccordement3 = 2L;
        
        Boolean resultat3 = service.EditerCoeffRaccordementProjet(idProjet3, idCoeffRaccordement3);
        if(resultat3)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet3);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet3);
        }
    }
    
    public static void testerEditerCategorieConstructionProjet() {
        
        System.out.println();
        System.out.println("**** testerEditerCategorieConstructionProjet() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String codeCategorieConstruction = "codeCategorieConstruction1";
        
        Boolean resultat = service.EditerCategorieConstructionProjet(idProjet, codeCategorieConstruction);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
       
        //Ne doit pas fonctionner
        Long idProjet2 = 100L;
        String codeCategorieConstruction2 = "codeCategorieConstruction1";
        
        Boolean resultat2 = service.EditerCategorieConstructionProjet(idProjet2, codeCategorieConstruction2);
        if(resultat2)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet2);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet2);
        }
        
        //Ne doit pas fonctionner
        Long idProjet3 = 1L;
        String codeCategorieConstruction3 = "codeCategorieConstruction3";
        
        Boolean resultat3 = service.EditerCategorieConstructionProjet(idProjet3, codeCategorieConstruction3);
        if(resultat3)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet3);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet3);
        }
    }
    
    public static void testerCoutSynthese() {
        
        System.out.println();
        System.out.println("**** testerCoutSynthese() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String typeBalise = "titre2";
        String idBalise = "_3";
        
        Double resultat = service.CoutSynthese(idProjet, typeBalise, idBalise);
        if(resultat != null){
            System.out.println("Cout de synthèse calculé: " + resultat + "€");
        } else {
            System.out.println("Erreur lors du calcul du cout de synthèse");
        }
        
        //si la balise n'existe pas -> echec(comme prevu)
        //si la balise existe mais ne possède pas de prix -> 0€ (comme prevu)
        //si la balise existe et possède un prix -> prix renvoyé (comme prévu)
    }
    
    public static void testerAjouterLot() {
        
        System.out.println();
        System.out.println("**** testerAjouterLot() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String placement = "APPEND";
        String idRefPlacement = null;
        
        Boolean resultat = service.AjouterLot(idProjet, placement, idRefPlacement);
        if(resultat)
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        else
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        
        placement = "APPEND";
        idRefPlacement = null;
        
        resultat = service.AjouterLot(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }
        
        //idProjet n'existe pas -> echec (comme prévu)
        //Insertion BEFORE avec un titre1 non existant -> echec (comme prévu)
        //Insertion BEFORE avec un titre1 exitant -> ok
    }
    
    public static void testerAjouterTitre1() {
        
        System.out.println();
        System.out.println("**** testerAjouterTitre1() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String placement = "APPEND";
        String idRefPlacement = "_12";
        
        Boolean resultat = service.AjouterTitre1(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }
        
        /*idProjet = 01L;
        placement = "BEFORE";
        idRefPlacement = "_1";
        
        resultat = service.AjouterTitre1(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }*/
        
        //idProjet n'existe pas -> echec (comme prévu)
        //Insertion BEFORE avec un titre1 non existant -> echec (comme prévu)
        //Insertion BEFORE avec un titre1 exitant -> ok
    }
    
    public static void testerAjouterTitre2() {
        
        System.out.println();
        System.out.println("**** testerAjouterTitre2() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String placement = "APPEND";
        String idRefPlacement = "_3";
        
        Boolean resultat = service.AjouterTitre2(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }
        
        /*placement = "BEFORE";
        idRefPlacement = "_3";
        
        resultat = service.AjouterTitre2(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }*/
        
        //idProjet n'existe pas -> echec (comme prévu)
        //Pas de titre1 correspondant -> echec (comme prévu)
    }
    
    public static void testerAjouterTitre3() {
        
        System.out.println();
        System.out.println("**** testerAjouterTitre3() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String placement = "APPEND";
        String idRefPlacement = "_4";
        
        Boolean resultat = service.AjouterTitre3(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }
        
        /*placement = "BEFORE";
        idRefPlacement = "_4";
        
        resultat = service.AjouterTitre3(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }*/
        
        //idProjet n'existe pas -> echec (comme prévu)
        //Pas de titre2 correspondant -> echec (comme prévu)
    }
    
    public static void testerAjouterTitre4() {
        
        System.out.println();
        System.out.println("**** testerAjouterTitre4() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String placement = "APPEND";
        String idRefPlacement = "_5";
        
        Boolean resultat = service.AjouterTitre4(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }
        
        /*placement = "BEFORE";
        idRefPlacement = "_6";
        
        resultat = service.AjouterTitre4(idProjet, placement, idRefPlacement);
        if(resultat)
        {
            System.out.println("Modification avec succès du Projet n° " + idProjet);
        } else {
            System.out.println("Echec lors de la modification du Projet n° " + idProjet);
        }*/
        
        //idProjet n'existe pas -> echec (comme prévu)
        //Pas de titre3 correspondant -> echec (comme prévu)
    }
    
    public static void testerSuppressionBalise(){
        System.out.println();
        System.out.println("**** testerSuppressionBalise() ****");
        System.out.println();
        
        // Service service = new Service();
        
        // //Doit fonctionner
        // Long idProjet = 01L;
        // String idBalise = "_1";
        
        // Boolean resultat = service.SuppressionBalise(idProjet, idBalise);
        // if(resultat)
        // {
        //     System.out.println("Suppression avec succès du Projet n° " + idProjet+", balise n°"+idBalise);
        // } else {
        //     System.out.println("Echec lors de la suppression de la balise n°"+idBalise+",Projet n° " + idProjet);
        // }
    }
    
    public static void testerModifierIntituleTitre(){
        System.out.println();
        System.out.println("**** testerModifierIntituleTitre() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 01L;
        String idTitre = "_5";
        String intitule = "Nouvel intitule";
        
        Boolean resultat = service.ModifierIntituleTitre(idProjet, idTitre, intitule);
        if(resultat)
        {
            System.out.println("Modification avec succès de l'intulet: Projet n° " + idProjet+", titre n°"+idTitre);
        } else {
            System.out.println("Echec lors de la modification de l'intituleé: titre n°"+idTitre+",Projet n° " + idProjet);
        }
    }
    
    public static void testerAjouterDescriptif() {
        
        System.out.println();
        System.out.println("**** testerAjouterDescriptif() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner 
        /*Long idProjet = 1L;
        String idDescriptif = "02_AAA_01_01_02";
        String placement = "APPEND";
        String idRefPlacement = "_3";
        
        Boolean resultat = service.AjouterDescriptif(idProjet, placement, idRefPlacement, idDescriptif);
        if(resultat){
            System.out.println("Ajout avec succès du descriptif " + idDescriptif);
        } else {
            System.out.println("Echec lors de l'ajout du descriptif " + idDescriptif);
        }
        
        idDescriptif = "02_AAA_01_01_02";
        placement = "APPEND";
        idRefPlacement = "_2";
        
        resultat = service.AjouterDescriptif(idProjet, placement, idRefPlacement, idDescriptif);
        if(resultat){
            System.out.println("Ajout avec succès du descriptif " + idDescriptif);
        } else {
            System.out.println("Echec lors de l'ajout du descriptif " + idDescriptif);
        }*/
        
        Long idProjet = 1L;
        String idDescriptif = "04_ETA_03_01_001";
        String placement = "APPEND";
        String idRefPlacement = "_13";
        
        Boolean resultat = service.AjouterDescriptif(idProjet, placement, idRefPlacement, idDescriptif);
        if(resultat){
            System.out.println("Ajout avec succès du descriptif " + idDescriptif);
        } else {
            System.out.println("Echec lors de l'ajout du descriptif " + idDescriptif);
        }
        
        //idProjet n'existe pas -> echec (comme prevu)
        //choisit bien en fonction de l'année la plus récente et de la fourchette de prix
        //idRefPlacement non existant -> echec (comme prévu)
        //Descriptif dans un autre descriptif -> échec (comme prévu) 
    }
    
    public static void testerAjouterLigneChiffrage() {
        
        System.out.println();
        System.out.println("**** testerAjouterLigneChiffrage() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner (sinsère uniquement dans le premier chapitre)
        Long idProjet = 1L;
        String idDescriptif = "11_DAP_10_10_001";
        
        Boolean resultat = service.AjouterLigneChiffrage(idProjet, idDescriptif);
        if(resultat){
            System.out.println("Edition avec succès du projet n°" + idProjet+", descriptif n°"+idDescriptif);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet+", descriptif n°"+idDescriptif);
        }
        
        //idProjet n'existe pas -> echec (comme prevu)
        //idDescriptif n'existe pas -> echec (comme prevu)
    }
    
    public static void testerSupprimerLigneChiffrage() {
        
        System.out.println();
        System.out.println("**** testerSupprimerLigneChiffrage() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_9";
        String idLigneChiffrage = "1";
        
        Boolean resultat = service.SupprimerLigneChiffrage(idProjet, idDescriptif, idLigneChiffrage);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
        
        //test pas de descrpitif existant : echec comme prévu
        //test pas de ligne chiffrage existante : echec comme prévu
        
    }
    
    public static void testerModifierDescriptionDescriptif() {
        
        System.out.println();
        System.out.println("**** testerModifierDescriptionDescriptif() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_8";
        String newDescription = "testNew";
        
        Boolean resultat = service.ModifierDescriptionDescriptif(idProjet, idDescriptif, newDescription);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
        
        //test pas de descriptif existant : echec comme prévu
        //test ouvrage ou prestation : ok
    }
    
    public static void testerModifierCourteDescriptionDescriptif() {
        
        System.out.println();
        System.out.println("**** testerModifierCourteDescriptionDescriptif() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_8";
        String newDescription = "testNew";
        
        Boolean resultat = service.ModifierCourteDescriptionDescriptif(idProjet, idDescriptif, newDescription);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
        
        //test pas de descriptif existant : echec comme prévu
        //test ouvrage ou prestation : ok
    }
    
    public static void testerModifierLocalisationDescriptif() {
        
        System.out.println();
        System.out.println("**** testerModifierLocalisationDescriptif() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_8";
        String idLigneChiffrage = "1";
        String newLocalisation = "newLoc";
        
        Boolean resultat = service.ModifierLocalisationDescriptif(idProjet, idDescriptif, idLigneChiffrage, newLocalisation);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
        
        //test pas de descriptif existant : echec comme prévu
        //test pas de ligneChiffrage existante: echec comme prévu
        //test ouvrage ou prestation : ok
    }
    
    public static void testerModifierQuantiteDescriptif() {
        
        System.out.println();
        System.out.println("**** testerModifierQuantiteDescriptif() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_8";
        String idLigneChiffrage = "1";
        Double quantite = 2.0;
        
        Boolean resultat = service.ModifierQuantiteDescriptif(idProjet, idDescriptif, idLigneChiffrage, quantite);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
        
        //test modif prestation : ok
        //test modif prestation avec ligneChiffrage non existante : ok
        //test modif mauvaise quantité : ok
        //test modif ouvrage : ok
    }
    
    public static void testerModifierPrixLigneChiffrage(){
        System.out.println();
        System.out.println("**** testerModifierPrixLigneChiffrage() ****");
        System.out.println();
        
        Service service = new Service();
        
        //Doit fonctionner
        Long idProjet = 1L;
        String idDescriptif = "_8";
        String idLigneChiffrage = "1";
        Double prix = 160.0;
        
        Boolean resultat = service.ModifierPrixLigneChiffrage(idProjet, idDescriptif, idLigneChiffrage, prix);
        if(resultat)
        {
            System.out.println("Edition avec succès du projet n°" + idProjet);
        } else {
            System.out.println("Erreur d'édition du projet n°" + idProjet);
        }
    }
    
                //-----------------------//

    public static void testerListerCategories() {
        
        System.out.println();
        System.out.println("**** testerListerCategories() ****");
        System.out.println();
        
        Service service = new Service();
        List<Categorie> listeCategorie = service.ListerCategories();
        System.out.println("*** Liste des Categories");
        if (listeCategorie != null) {
            listeCategorie.forEach((categorie) -> {
                afficherCategorie(categorie);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerCategorieConstructions() {
        
        System.out.println();
        System.out.println("**** testerListerCategorieConstructions() ****");
        System.out.println();
        
        Service service = new Service();
        List<CategorieConstruction> listCategorieConstruction = service.ListerCategorieConstructions();
        System.out.println("*** Liste des CategorieConstructions");
        if (listCategorieConstruction != null) {
            listCategorieConstruction.forEach((categorieConstruction) -> {
                afficherCategorieConstruction(categorieConstruction);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerCoeffRaccordements() {
        
        System.out.println();
        System.out.println("**** testerListerCoeffRaccordements() ****");
        System.out.println();
        
        Service service = new Service();
        List<CoeffRaccordement> listCoeffRaccordement = service.ListerCoeffRaccordements();
        System.out.println("*** Liste des CoeffRaccordements");
        if (listCoeffRaccordement != null) {
            listCoeffRaccordement.forEach((coeffRaccordement) -> {
                afficherCoeffRaccordement(coeffRaccordement);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerChapitres() {
        
        System.out.println();
        System.out.println("**** testerListerChapitres() ****");
        System.out.println();
        
        Service service = new Service();
        List<Chapitre> listeChapitres = service.ListerChapitres();
        System.out.println("*** Liste des Chapitres");
        if (listeChapitres != null) {
            listeChapitres.forEach((chapitre) -> {
                afficherChapitre(chapitre);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerDescriptifs() {
        
        System.out.println();
        System.out.println("**** testerListerDescriptifs() ****");
        System.out.println();
        
        Service service = new Service();
        List<Descriptif> listeDescriptifs = service.ListerDescriptifs();
        System.out.println("*** Liste des Descriptifs");
        if (listeDescriptifs != null) {
            listeDescriptifs.forEach((descriptif) -> {
                afficherDescriptif(descriptif);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerFamilles() {
        
        System.out.println();
        System.out.println("**** testerListerFamilles() ****");
        System.out.println();
        
        Service service = new Service();
        List<Famille> listeFamilles = service.ListerFamilles();
        System.out.println("*** Liste des Familles");
        if (listeFamilles != null) {
            listeFamilles.forEach((famille) -> {
                afficherFamille(famille);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerOperateurs() {
        
        System.out.println();
        System.out.println("**** testerListerOperateurs() ****");
        System.out.println();
        
        Service service = new Service();
        List<Operateur> listeOperateurs = service.ListerOperateurs();
        System.out.println("*** Liste des Operateurs");
        if (listeOperateurs != null) {
            listeOperateurs.forEach((operateur) -> {
                afficherOperateur(operateur);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerProjets() {
        
        System.out.println();
        System.out.println("**** testerListerProjets() ****");
        System.out.println();
        
        Service service = new Service();
        List<Projet> listeProjets = service.ListerProjets();
        System.out.println("*** Liste des Projets");
        if (listeProjets != null) {
            listeProjets.forEach((projet) -> {
                afficherProjet(projet);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerSousCategorieConstructions() {
        
        System.out.println();
        System.out.println("**** testerListerSousCategorieConstructions() ****");
        System.out.println();
        
        Service service = new Service();
        List<SousCategorieConstruction> listeSousCategorieConstructions = service.ListerSousCategorieConstructions();
        System.out.println("*** Liste des SousCategorieConstructions");
        if (listeSousCategorieConstructions != null) {
            listeSousCategorieConstructions.forEach((sousCategorieConstruction) -> {
                afficherSousCategorieConstruction(sousCategorieConstruction);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerSousFamilles() {
        
        System.out.println();
        System.out.println("**** testerListerSousFamilles() ****");
        System.out.println();
        
        Service service = new Service();
        List<SousFamille> listeSousFamilles = service.ListerSousFamilles();
        System.out.println("*** Liste des SousFamilles");
        if (listeSousFamilles != null) {
            listeSousFamilles.forEach((sousFamille) -> {
                afficherSousFamille(sousFamille);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerBasePrixRefs() {
        
        System.out.println();
        System.out.println("**** testerListerBasePrixRefs() ****");
        System.out.println();
        
        Service service = new Service();
        List<BasePrixRef> listeBasePrixRefs = service.ListerBasePrixRefs();
        System.out.println("*** Liste des BasePrixRefs");
        if (listeBasePrixRefs != null) {
            listeBasePrixRefs.forEach((basePrixRef) -> {
                afficherBasePrixRef(basePrixRef);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerCaractDims() {
        
        System.out.println();
        System.out.println("**** testerListerCaractDims() ****");
        System.out.println();
        
        Service service = new Service();
        List<CaractDim> listeCaractDims = service.ListerCaractDims();
        System.out.println("*** Liste des CaractDims");
        if (listeCaractDims != null) {
            listeCaractDims.forEach((caractDim) -> {
                afficherCaractDim(caractDim);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    public static void testerListerPrestations() {
        
        System.out.println();
        System.out.println("**** testerPrestations() ****");
        System.out.println();
        
        Service service = new Service();
        List<Prestation> listePrestations = service.ListerPrestations();
        System.out.println("*** Liste des Prestations");
        if (listePrestations != null) {
            listePrestations.forEach((prestation) -> {
                afficherPrestation(prestation);
            });
        }
        else {
            System.out.println("=> ERREUR...");
        }
    }
    
//------------------------------------------------------------------------------
//----------------------------------- Imports  ---------------------------------
//------------------------------------------------------------------------------
    
    public static void testerTransformationWordVersExcel(){
        
        System.out.println();
        System.out.println("**** testerTransformationWordVersExcel() ****");
        System.out.println();
        
        ImportService service = new ImportService();
        
        String uriWord = "../import_files/XX_Jeu_Test_BRP_v0.2.docx";
        Boolean resultat = service.TransformationWordVersExcel(uriWord);
        
        if(resultat)
            System.out.println("Transformation avec succès du document d'import de descriptif vers le document d'import de base de prix");
        else
            System.out.println("Echec de la transformation du document d'import de descriptif vers le document d'import de base de prix");
    }
    
    //TODO : revoir les strings de sortie (relation avec le front)
    //niveau commentaire c'est pas clair ...
    public static void testerModifBaseDescriptif(){
        
        ImportService service = new ImportService();
        String msgSuppr = "";
        String uriWord = "../BRP_front_end/src/main/webapp/import_files/XX_Jeu_Test_BRP_v0.2.docx";

        //returnListe[0] = status
        //les autres contiennent les identifiants à supprimer
        ArrayList<String> returnListe = service.ModifBaseDescriptif(uriWord);
        
        //les ajouts se sont bien passés, on passe aux suppressions
        if(returnListe.get(0).equals("Succes")){
            for(int i = 1; i < returnListe.size(); i++){
                msgSuppr = service.CompterEnfants(returnListe.get(i));
                //on envoie au comptage des enfants
                if(msgSuppr.equals("suppr ok")){ //on supprime direct car pas d'enfant
                    System.out.println(service.SupprObjet(returnListe.get(i)));
                }
                else{ //on demande la permission au client
                    System.out.println(msgSuppr);
                    System.out.println(service.SupprObjet(returnListe.get(i)));
                }
            }
        }
        else{       //on affiche l'erreur
            System.out.println(returnListe.get(0));
        }
    }
    
    public static void testerModifBasePrixRef(){
        ImportService service = new ImportService();
        
        String uriExcel = "../BRP_front_end/src/main/webapp/import_files/templateBasePrix_jeau_test_0.2.csv";
        String msgState = service.ModifBasePrixRef(uriExcel);
        System.out.println(msgState);
    }
    
    
    //------------------------------------------------------------------------------
    //----------------------------------- Exports  ---------------------------------
    //------------------------------------------------------------------------------
    
    public static void testerExporterProjet(){
        
        System.out.println();
        System.out.println("**** testerExporterProjet() ****");
        System.out.println();
        
        ExportService service = new ExportService();
        
        //Doit fonctionner
        Long idProjet = 1L;
        int choixTemplate = 1;
        ArrayList<String> resultat = service.ExporterProjet(idProjet, choixTemplate, "../../../../code/BRP_front_end/src/main/webapp/XMLfiles/" + idProjet + ".xml");
        
        if(!resultat.isEmpty())
            System.out.println("Export avec succès du projet n° " + idProjet);
        else
            System.out.println("Echec de l'export du projet n° " + idProjet);
    }
    
    //------------------------------------------------------------------------------    
    //-------------------------- METHODES AFFICHAGE --------------------------------
    //------------------------------------------------------------------------------
    
    public static void afficherBasePrixRef(BasePrixRef basePrixRef) {
        System.out.println("-> " + basePrixRef);
    }
    public static void afficherCaractDim(CaractDim caractDim) {
        System.out.println("-> " + caractDim);
    }
    public static void afficherCategorieConstruction(CategorieConstruction categorieConstruction) {
        System.out.println("-> " + categorieConstruction);
    }
    public static void afficherCategorie(Categorie categorie) {
        System.out.println("-> " + categorie);
    }
    public static void afficherCoeffRaccordement(CoeffRaccordement coeffRaccordement) {
        System.out.println("-> " + coeffRaccordement);
    }
    public static void afficherChapitre(Chapitre chapitre) {
        System.out.println("-> " + chapitre);
    }
    public static void afficherDescriptif(Descriptif descriptif) {
        if (descriptif.getClass() == Generique.class) afficherGenerique((Generique)descriptif);
        else if (descriptif.getClass() == Ouvrage.class) afficherOuvrage((Ouvrage)descriptif);
        else System.out.println("descriptif inconnu\n");
    }
    public static void afficherFamille(Famille famille) {
        System.out.println("-> " + famille);
    }
    public static void afficherGenerique(Generique generique) {
        System.out.println("-> " + generique);
    }
    public static void afficherOperateur(Operateur operateur) {
        System.out.println("-> " + operateur);
    }
    public static void afficherOuvrage(Ouvrage ouvrage) {
        System.out.println("-> " + ouvrage);
    }
    public static void afficherPrestation(Prestation prestation) {
        System.out.println("-> " + prestation);
    }
    public static void afficherProjet(Projet projet) {
        System.out.println("-> " + projet);
    }
    public static void afficherSousCategorieConstruction(SousCategorieConstruction sousCategorieConstruction) {
        System.out.println("-> " + sousCategorieConstruction);
    }
    public static void afficherSousFamille(SousFamille sousFamille) {
        System.out.println("-> " + sousFamille);
    }
}