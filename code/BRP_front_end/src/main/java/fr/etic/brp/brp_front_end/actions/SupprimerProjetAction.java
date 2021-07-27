/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.etic.brp.brp_front_end.actions;

import fr.etic.brp.brp_back_end.metier.service.Service;
import static java.lang.Integer.parseInt;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author brplyon
 */
public class SupprimerProjetAction extends Action {
    @Override
    public void execute(HttpServletRequest request){ //Implémentation de la méthode Action.execute()
        
        Service service = new Service();
        
        try {
            String idProjet = request.getParameter("idProjet");
            System.out.println("Supprimer projet "+idProjet);
            boolean succes = service.SupprimerProjet(new Long(parseInt(idProjet)));
            if (succes) request.setAttribute("ErrorState", false);
            else request.setAttribute("ErrorState", true);
        } catch (Exception e) {
            request.setAttribute("ErrorState", true);
        }
    }
}
