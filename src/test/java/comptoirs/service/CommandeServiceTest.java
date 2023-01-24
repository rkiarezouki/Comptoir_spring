package comptoirs.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import comptoirs.dao.*;
import comptoirs.entity.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class CommandeServiceTest {
    private static final String ID_PETIT_CLIENT = "0COM";
    private static final String ID_GROS_CLIENT = "2COM";
    private static final String VILLE_PETIT_CLIENT = "Berlin";
    private static final BigDecimal REMISE_POUR_GROS_CLIENT = new BigDecimal("0.15");
    private static final Integer ID_COMMANDE_NON_EXISTANTE = 10;
    private static final Integer ID_COMMANDE_LIVREE = 9999;
    private static final Integer ID_COMMANDE_PAS_LIVREE = 999998;

    @Autowired
    private CommandeService service;

    @Autowired
    private ProduitRepository produitDao;
    @Test
    void testCreerCommandePourGrosClient() {
        var commande = service.creerCommande(ID_GROS_CLIENT);
        assertNotNull(commande.getNumero(), "On doit avoir la clé de la commande");
        assertEquals(REMISE_POUR_GROS_CLIENT, commande.getRemise(),
            "Une remise de 15% doit être appliquée pour les gros clients");
    }

    @Test
    void testCreerCommandePourPetitClient() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertNotNull(commande.getNumero());
        assertEquals(BigDecimal.ZERO, commande.getRemise(),
            "Aucune remise ne doit être appliquée pour les petits clients");
    }

    @Test
    void testCreerCommandeInitialiseAdresseLivraison() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertEquals(VILLE_PETIT_CLIENT, commande.getAdresseLivraison().getVille(),
            "On doit recopier l'adresse du client dans l'adresse de livraison");
    }

    void testDecrementerStock(){
        var produit = produitDao.findById(98).orElseThrow();
        int stockAvant = produit.getUnitesEnStock();
        service.enregistreExpédition(99998);
        produit = produitDao.findById(98).orElseThrow();

        assertEquals(stockAvant-20, produit.getUnitesEnStock(), "on doit décrémenter le stock de 20 unités");

    }
    @Test
    void commandeEnregistreeExiste(){
        assertThrows(Exception.class, () -> service.enregistreExpédition(ID_COMMANDE_NON_EXISTANTE), "L'id n'existe pas");
    }

    @Test
    void commandeEnvoyee(){
        assertThrows(Exception.class, () -> service.enregistreExpédition(ID_COMMANDE_LIVREE),"la commande est enregistrée");
    }

    @Test
    void miseAJourDateEnvoi(){
        var commandeEnvoyee = service.enregistreExpédition(ID_COMMANDE_PAS_LIVREE);
        assertEquals(LocalDate.now(), commandeEnvoyee.getEnvoyeele(),"La date d'envoie n'est pas celle du jour actuel");
    }
}
