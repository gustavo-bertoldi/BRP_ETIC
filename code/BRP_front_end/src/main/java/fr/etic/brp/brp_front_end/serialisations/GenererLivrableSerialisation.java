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
        
        ArrayList<String> fichiers = (ArrayList<String>)request.getAttribute("fichiers");
        if (fichiers == null || fichiers.isEmpty()) return;
        
        final String cheminDossier = fichiers.get(0).substring(0, fichiers.get(0).lastIndexOf("\\"));
        final String nomFichier = "livrables.zip";
        final String cheminLivrable = cheminDossier + "\\" + nomFichier;
        final int STREAM_SIZE = 1024;
        
        try (FileOutputStream fos = new FileOutputStream(cheminLivrable);
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            byte[] buffer = new byte[STREAM_SIZE];
            
            for (String cheminFichier : fichiers) {
                File f = new File(cheminFichier);
                
                try (InputStream fis = new FileInputStream(f)) {
                    ZipEntry ze = new ZipEntry(f.getName());
                    zos.putNextEntry(ze);
                    int read;
                    while ((read = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, read);
                    }
                    zos.closeEntry();
                } catch (Exception e) {
                    System.out.println("Erreur lors de la création du fichier zip : "+e.toString());
                    return;
                }
            }
            System.out.println("Fichier zip crée");
        }

        File fzip = new File(cheminLivrable);
        response.setContentType("application/zip");
        response.setContentLength((int)fzip.length());
        response.addHeader("Content-Disposition", "attachment; filename=\""+nomFichier+"\"");
        
        try (InputStream is = new FileInputStream(fzip);
                ServletOutputStream sos = response.getOutputStream();) {
            
            byte[] buffer = new byte[(int)fzip.length()];
            is.read(buffer);
            sos.write(buffer);
            sos.flush();
           
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoie du fichier compressé : "+e.toString());
        }

             //Formatage de la structure de données JSON => Ecriture sur le flux de sortie de la réponse
            /*
            PrintWriter out = this.getWriter(response);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
            gson.toJson(container, out);
            out.close();
        */
        
        
        
        
        
        
        
        
    }
}