# Moteur-Optimisation

###### Le moteur d’optimisation a objectif de ne pas exiger à l’utilisateur de pré-optimiser ses requêtes, il s’en occupe lui-même de transformer la requête et chercher le plan optimal pour son exécution.

# composants principaux

Dans son fonctionnement, l'optimiseur repose sur trois composants principaux :

- Transformateur :  il s’occupe de transformer la requête saisie par l’utilisateur au plan initial équivalant. À partir de ce dernier, le transformateur génère les plans logiques possibles, Après le transformateur procède à la génération des plans physiques pour chaque plan logique, cette transformation concerne les opérations de balayage, de tri/fusion, de hashage.
- Estimateur : Ce composant est appelé pour estimer le cout de chaque opérateur. Ce cout est calculé d’une manière différentes d’un nœud à l’autre, avant de calculer ce cout, nous avons mis à notre disposition un fichier qui peux nous servir comme un catalogue d’une base de données, cette dernière concerne le schéma HR, qu’on peut trouver dans le SGBD Oracle.
- Optimiseur : Ce dernier composant vient pour parcourir la liste des plans physiques produite, et les
comparer en deux modes d’exécution : exécution en pipeline et en matérialisation. Le composant retourne les plans optimaux d’optimisation associés aux leurs plans logiques, ces derniers sont employés pour réécrire la requête SQL équivalente.
