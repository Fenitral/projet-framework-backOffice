Tu dois implémenter de nouvelles règles de gestion dans un système existant d’assignation de réservations à des véhicules. Le système fonctionne déjà : tu dois UNIQUEMENT ajouter/modifier la logique d’assignation sans casser l’existant.

---
Corriger ou implementer de nouveau code pour ces
## RÈGLES DE GESTION

### 1. PRIORITÉ

Lors de l’assignation :

1. Réservations NON assignées en priorité
2. Ensuite les autres
3. Trier par `nombrePassagers` décroissant

---

### 2. REMPLISSAGE DU VÉHICULE

* Ne jamais dépasser la capacité
* Maximiser le remplissage
* Si places restantes :
  → prendre la réservation la plus proche du nombre de places restantes

---

### 3. ASSIGNATION PARTIELLE

* Si une réservation dépasse la capacité restante :
  → assigner partiellement
  → garder le reste comme non assigné

---

### 4. DÉPART DU VÉHICULE

* Si complet → départ immédiat
* Sinon :

  * attendre `dureeAttente`
  * si aucune réservation utile → départ

---


---
## EXEMPLES À RESPECTER (OBLIGATOIRES)

### CAS 1

Entrée :

* R1 = 11 passagers
* R2 = 3 passagers
* V = 20 places

Traitement :

* Affectation : 11 + 3 = 14
* Reste = 6 places
* Véhicule non complet

Résultat attendu :
→ attendre le temps d’attente
→ si aucune réservation → départ

---

### CAS 2

Entrée :

* Véhicule disponible à 9h45
* Fenêtre : [9h45 – 10h15]
* R1 = 10h00 (non assignée)
* R2 = 10h10
* R3 = 10h40

Traitement :

* R1 prioritaire (non assignée)
* R2 dans la fenêtre → peut être assignée
* R3 hors fenêtre → ignorée

Résultat attendu :
→ regroupement basé sur R1 (priorité) + R2
→ R3 traité plus tard

---

### CAS 3

Entrée :

* R1 = 11
* R2 = 12
* V = 11 places

Traitement :

* Trier : R2 (12) puis R1 (11)
* R2 remplit le véhicule → 11/11 → départ
* Reste :

  * R2 = 1 passager
  * R1 = 11 non assignée

Résultat attendu :

* Nouvelle liste triée :
  → R1 (11), puis R2 (1)

---

### CAS 4

Entrée :

* R1 = 11
* R2 = 3
* R3 = 2
* V = 13 places

Traitement :

* R1 = 11 → reste 2 places
* Choix optimal :

  * R2 = 3 ❌
  * R3 = 2 ✅

Résultat attendu :
→ véhicule = R1 (11) + R3 (2) = 13 (complet)
→ R2 reste non assignée

---

## CONTRAINTES TECHNIQUES

* Ne pas modifier l’architecture existante
* Ajouter la logique dans le service d’assignation
* Créer des fonctions si nécessaire :

  * `trierReservationsParPriorite()`
  * `trouverMeilleurAjustement()`
  * `assignerAuVehicule()`
  * `verifierDepartVehicule()`

---

## OBJECTIF

* Respecter strictement les cas métiers ci-dessus
* Optimiser le remplissage des véhicules
* Gérer correctement les priorités et le temps
* Éviter toute régression
