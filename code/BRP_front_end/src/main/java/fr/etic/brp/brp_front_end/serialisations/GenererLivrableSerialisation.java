package fr.etic.brp.brp_front_end.serialisations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author louisrob
 */
public class GenererLivrableSerialisation extends Serialisation {
    
    @Override
    public void serialiser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        JsonObject container = new JsonObject(); //Objet "conteneur JSON" pour structurer les données à sérialiser
        
        //Lecture des attributs de la requête (stockés par l'action)
        boolean ErrorState = (boolean)request.getAttribute("ErrorState");
        
        container.addProperty("Error", ErrorState);
        
        //Formatage de la structure de données JSON => Ecriture sur le flux de sortie de la réponse
        PrintWriter out = this.getWriter(response);
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        gson.toJson(container, out);
        out.close();
    }
}