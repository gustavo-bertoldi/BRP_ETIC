package fr.etic.brp.brp_back_end.dao;

import fr.etic.brp.brp_back_end.metier.modele.Projet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author louisrob
 */
public class ProjetDao {
    
    public void Creer(Projet projet){
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.persist(projet);
    }
    
    public Projet ChercherParId(Long idProjet) {
        return JpaUtil.obtenirContextePersistance().find(Projet.class, idProjet); // renvoie null si l'identifiant n'existe pas
    }
    
    public List<Projet> ListerProjets() {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        TypedQuery<Projet> query = em.createQuery("SELECT p FROM Projet p", Projet.class);
        return query.getResultList();
    }
    
    public Projet ChercherDernierParNom(String nomProjet) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        String jpql = "SELECT p FROM Projet p WHERE p.nomProjet = :nomProjet";
        Query query = em.createQuery(jpql);
        query.setParameter("nomProjet", nomProjet);
        List<Projet> resultats = query.getResultList();
        return resultats.get(resultats.size() - 1 );
    }
    
    public void Update(Projet projet) {
        EntityManager em = JpaUtil.obtenirContextePersistance();
        em.merge(projet);
    }
    
    public void Remove(Projet projet) {
        JpaUtil.obtenirContextePersistance().remove(projet);
    }
}
