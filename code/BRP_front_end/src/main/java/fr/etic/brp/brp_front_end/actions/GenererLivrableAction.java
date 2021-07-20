package fr.etic.brp.brp_front_end.actions;

import fr.etic.brp.brp_back_end.metier.service.ExportService;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author louisrob
 */
public class GenererLivrableAction extends Action {
    
    @Override
    public void execute(HttpServletRequest request){ //Implémentation de la méthode Action.execute()
        
        //Récupération des paramètres de le requête
        
        Long idProjet = Long.parseLong(request.getParameter("idProjet"));  
        int choixTemplate = Integer.parseInt(request.getParameter("choixTemplate"));
        String uriXML = request.getParameter("uriXML");
        
        System.out.println("Appel à générer livrable - "+uriXML);
        
        ExportService exportService = new ExportService();
        
        //Appel du service d'export
        ArrayList<String> resultat = null;
        try {
            resultat = exportService.ExporterProjet(idProjet, choixTemplate, uriXML);
        } catch (Exception ex) {
            request.setAttribute("ErrorState", true);
        }
        
        //Stockage des résultats dans les attributs de la requête
        if(resultat != null && !resultat.isEmpty())
        {
            request.setAttribute("ErrorState", false);
            request.setAttribute("fichiers", resultat);
 
        } else {
            request.setAttribute("ErrorState", true);
        }
    }
}