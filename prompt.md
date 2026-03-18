On garde toujours les regles de gestion d'avant mais la suite maintenant c'est:
    -lors de l'assignation d'une ou des reservation a une ou des voitures,
    on respecte  toujours l'ORDRE DE TRI: Réservations avec LE PLUS DE PASSAGERS d'abord
       (ex: R1=8 passagers AVANT R2=4 passagers)
    Pour chaque réservation: REMPLIR LE VÉHICULE AU MAXIMUM avant passer au suivant
       (ex: si R1=8 et V1=6 dispo → assigner 6 à V1, puis chercher une vehicule pour les 2 restants)
    Sélection du véhicule (par ordre de priorité):
       1. Capacité restante la plus proche (remplir avant de changer)
       2. Si égalité → choisir le MOINS DE TRAJETS (moins de réservations assignées)
       3. Si égalité → Diesel prioritaire
       4. Si égalité → aléatoire en dernier recours
    S'il y a toujours des passagers_restants mais il n'y a plus de vahicule,on reporte le reste de passager du reservation au intervalle du temps suivant.

    Le but ici donc c'est de separer les passager s'il n'y a pas de voiture convenable au nbr de place de reservation 