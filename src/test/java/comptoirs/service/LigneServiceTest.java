package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;
    static final int REFERENCE_PRODUIT_INEXISTANT = 99999;

    @Autowired
    LigneService service;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(),
        "La ligne doit être enregistrée, sa clé générée"); 
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class, 
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1),
            "La quantite d'une ligne doit être positive");
    }

    @Test
    void leProduitexiste(){
        assertThrows(Exception.class, () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_INEXISTANT, 1),
                "Le produit était inexistant");
    }

    @Test
    void leProduitEstDisponible(){
        assertThrows(Exception.class, () ->  service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_INDISPONIBLE, 1),
                "Le produit n'est pas disponible");
    }

    @Test
    void laCommandeEstPasLivree(){
        assertThrows(Exception.class, () -> service.ajouterLigne(NUMERO_COMMANDE_DEJA_LIVREE,REFERENCE_PRODUIT_DISPONIBLE_1, 1),
                "La commande était déjà livrée");
    }

    @Test
    void quantiteSuffisante(){
        assertThrows(Exception.class, () -> service.ajouterLigne((NUMERO_COMMANDE_PAS_LIVREE),REFERENCE_PRODUIT_DISPONIBLE_1, 10000),
                "la quantité commandée était supérieure au stock");
    }

}
