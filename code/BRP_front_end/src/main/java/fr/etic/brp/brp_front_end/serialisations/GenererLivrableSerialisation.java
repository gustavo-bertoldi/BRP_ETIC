package fr.etic.brp.brp_front_end.serialisations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
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
        
        String cheminLivrable = null;

        try {
            ArrayList<String> fichiers = (ArrayList<String>) request.getAttribute("fichiers");
            String cheminDossier = fichiers.get(0).substring(0, fichiers.get(0).lastIndexOf("/") + 1);
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(cheminDossier + "livrables.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
        
        
            for (String cheminFichier : fichiers) {
                System.out.println(cheminFichier);
                File f = new File(cheminFichier);
                InputStream fis = new FileInputStream(f);
                zos.putNextEntry(new ZipEntry(f.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            cheminLivrable = cheminDossier + "livrables.zip";
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        
        if (cheminLivrable != null) {
            File livrable = new File(cheminLivrable);
            InputStream livrableFis = new FileInputStream(livrable);
            
            response.setContentType("application/octet-stream");
            response.setContentLength((int)livrable.length());
            response.setHeader("Content-Disposition","attachment;filename=" + livrable.getName());
            
            OutputStream os = response.getOutputStream();
            
            byte[] bufferData = new byte[1024];
            int read=0;
            while((read = livrableFis.read(bufferData))!= -1){
		os.write(bufferData, 0, read);
            }
            os.close();
            livrableFis.close();
            System.out.println("Fichier envoyé");
        } else {
             //Formatage de la structure de données JSON => Ecriture sur le flux de sortie de la réponse
            PrintWriter out = this.getWriter(response);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            gson.toJson(container, out);
            out.close();
        }
        
        
        
        
        
        
        
    }
}