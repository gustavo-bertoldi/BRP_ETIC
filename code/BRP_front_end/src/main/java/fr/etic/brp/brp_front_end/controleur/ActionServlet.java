package fr.etic.brp.brp_front_end.controleur;

import fr.etic.brp.brp_back_end.dao.JpaUtil;
import fr.etic.brp.brp_front_end.actions.Action;
import fr.etic.brp.brp_front_end.actions.AffichageAdminAction;
import fr.etic.brp.brp_front_end.actions.ArboDescriptifsAction;
import fr.etic.brp.brp_front_end.actions.ChercherProjetAction;
import fr.etic.brp.brp_front_end.actions.ConnexionAction;
import fr.etic.brp.brp_front_end.actions.CreationProjetAction;
import fr.etic.brp.brp_front_end.actions.DeplacerDescriptifAction;
import fr.etic.brp.brp_front_end.actions.DupliquerProjetAction;
import fr.etic.brp.brp_front_end.actions.EditerInfosProjetAction;
import fr.etic.brp.brp_front_end.actions.EnregistrerUriProjetExportAction;
import fr.etic.brp.brp_front_end.actions.GenererLivrableAction;
import fr.etic.brp.brp_front_end.actions.ImportAction;
import fr.etic.brp.brp_front_end.actions.InscriptionAction;
import fr.etic.brp.brp_front_end.actions.ListerRaccordementCatConstAction;
import fr.etic.brp.brp_front_end.actions.ListerSousCatConstCaractDimAction;
import fr.etic.brp.brp_front_end.actions.ModifierXMLAction;
import fr.etic.brp.brp_front_end.actions.OuvrirProjetAction;
import fr.etic.brp.brp_front_end.actions.RecupererDescriptifAction;
import fr.etic.brp.brp_front_end.actions.RecupererUriProjetExportAction;
import fr.etic.brp.brp_front_end.actions.SupprComplexeAction;
import fr.etic.brp.brp_front_end.actions.SupprimerProjetAction;
import fr.etic.brp.brp_front_end.actions.SupprimerXMLAction;
import fr.etic.brp.brp_front_end.actions.TestConnexionAction;
import fr.etic.brp.brp_front_end.serialisations.AffichageAdminSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ArboDescriptifsSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ChercherProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ConnexionSerialisation;
import fr.etic.brp.brp_front_end.serialisations.CreationProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.DeplacerDescriptifSerialisation;
import fr.etic.brp.brp_front_end.serialisations.DupliquerProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.EditerInfosProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.EnregistrerUriProjetExportSerialisation;
import fr.etic.brp.brp_front_end.serialisations.GenererLivrableSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ImportSerialisation;
import fr.etic.brp.brp_front_end.serialisations.InscriptionSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ListerRaccordementCatConstSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ListerSousCatConstCaractDimSerialisation;
import fr.etic.brp.brp_front_end.serialisations.ModifierXMLSerialisation;
import fr.etic.brp.brp_front_end.serialisations.OuvrirProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.RecupererDescriptifSerialisation;
import fr.etic.brp.brp_front_end.serialisations.RecupererUriProjetExportSerialisation;
import fr.etic.brp.brp_front_end.serialisations.Serialisation;
import fr.etic.brp.brp_front_end.serialisations.SupprComplexeSerialisation;
import fr.etic.brp.brp_front_end.serialisations.SupprimerProjetSerialisation;
import fr.etic.brp.brp_front_end.serialisations.SupprimerXMLSerialisation;
import fr.etic.brp.brp_front_end.serialisations.TestConnexionSerialisation;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Quentin MARC & Louis ROB
 */
@WebServlet(name = "ActionServlet", urlPatterns = {"/ActionServlet"})
public class ActionServlet extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        super.init();
        JpaUtil.init();
    }

    @Override
    public void destroy() {
        JpaUtil.destroy();
        super.destroy();
}

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getSession(true); //Init de la session
        request.setCharacterEncoding("UTF-8"); //Encodage des param de la requête (pour une lecture correcte)
        String todo = request.getParameter("todo");
        //System.out.println("TODO= " + todo); //si besoin, pour le debug
        
        Action action = null;
        Serialisation serialisation = null;
        
        switch(todo) {
            case "inscription" : {
                action = new InscriptionAction();
                serialisation = new InscriptionSerialisation();  
                break;
            }
            case "connexion" : {
                action = new ConnexionAction();
                serialisation = new ConnexionSerialisation(); 
                break;
            }
            case "testConnexion" : {
                action = new TestConnexionAction();
                serialisation = new TestConnexionSerialisation();
                break;
            }
            case "arboDescriptifs" : {
                action = new ArboDescriptifsAction();
                serialisation = new ArboDescriptifsSerialisation();
                break;
            }
            case "creationProjet" : {
                action = new CreationProjetAction();
                serialisation = new CreationProjetSerialisation();
                break;
            }
            case "dupliquerProjet" : {
                action = new DupliquerProjetAction();
                serialisation = new DupliquerProjetSerialisation();
                break;
            }
            case "editerInfosProjet" : {
                action = new EditerInfosProjetAction();
                serialisation = new EditerInfosProjetSerialisation();
                break;
            }
            case "recupererDescriptif" : {
                action = new RecupererDescriptifAction();
                serialisation = new RecupererDescriptifSerialisation();
                break;
            }
            case "ouvrirProjet" : {
                action = new OuvrirProjetAction();
                serialisation = new OuvrirProjetSerialisation();
                break;
            }
            case "listerRaccordementCatConst" : {
                action = new ListerRaccordementCatConstAction();
                serialisation = new ListerRaccordementCatConstSerialisation();
                break;
            }
            case "listerSousCatConstCaractDim" : {
                action = new ListerSousCatConstCaractDimAction();
                serialisation = new ListerSousCatConstCaractDimSerialisation();
                break;
            }
            case "chercherProjet" : {
                action = new ChercherProjetAction();
                serialisation = new ChercherProjetSerialisation();
                break;
            }
            case "modifierXML" : {
                action = new ModifierXMLAction();
                serialisation = new ModifierXMLSerialisation();
                break;
            }
            case "supprimerXML" : {
                action = new SupprimerXMLAction();
                serialisation = new SupprimerXMLSerialisation();
                break;
            }
            case "supprComplexe" : {
                action = new SupprComplexeAction();
                serialisation = new SupprComplexeSerialisation();
                break;
            }
            case "deplacerDescriptif" : {
                action = new DeplacerDescriptifAction();
                serialisation = new DeplacerDescriptifSerialisation();
                break;
            }
            case "import" : {
                action = new ImportAction();
                serialisation = new ImportSerialisation();
                break;
            }
            case "affichageAdmin" : {
                action = new AffichageAdminAction();
                serialisation = new AffichageAdminSerialisation();
                break;
            }
            case "genererLivrable" : {
                action = new GenererLivrableAction();
                serialisation = new GenererLivrableSerialisation();
                break;
            }
            case "enregistrerUriProjetExport" : {
                action = new EnregistrerUriProjetExportAction();
                serialisation = new EnregistrerUriProjetExportSerialisation();
                break;
            }
            case "recupererUriProjetExport" : {
                action = new RecupererUriProjetExportAction();
                serialisation = new RecupererUriProjetExportSerialisation();
                break;
            } case "supprimerProjet" : {
                action = new SupprimerProjetAction();
                serialisation = new SupprimerProjetSerialisation();
                break;
            }
        }
       
        if(action != null && serialisation != null) {
            action.execute(request); // Exécuter l'action
            serialisation.serialiser(request, response); // Sérialiser le résultat de l'action
        } else { // Erreur : pas d'action ou de Sérialisation pour traiter cette requête
            response.sendError(400, "Bad Request (pas d'action ou de serialisation pour traiter cette requete)");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}