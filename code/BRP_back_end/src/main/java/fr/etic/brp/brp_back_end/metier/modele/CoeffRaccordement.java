
package fr.etic.brp.brp_back_end.metier.modele;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author louisrob
 */
@Entity
public class CoeffRaccordement implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long idCoeffRaccordement;
    private String localisation;
    private Float valeur;

    public CoeffRaccordement() {
    }

    public CoeffRaccordement(String localisation, Float valeur) {
        this.localisation = localisation;
        this.valeur = valeur;
    }

    public Long getIdCoeffRaccordement() {
        return idCoeffRaccordement;
    }

    public void setIdCoeffRaccordement(Long idCoeffRaccordement) {
        this.idCoeffRaccordement = idCoeffRaccordement;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public Float getValeur() {
        return valeur;
    }

    public void setValeur(Float valeur) {
        this.valeur = valeur;
    } 

    @Override
    public String toString() {
        return "CoeffRaccordement{" + "idCoeffRaccordement=" + idCoeffRaccordement + ", localisation=" + localisation + ", valeur=" + valeur + '}';
    }
}