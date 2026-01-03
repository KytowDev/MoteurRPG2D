# Moteur de Jeu Dungeon Crawler/Roguelite

Ce moteur a été développé en Java avec le framework de développement LibGDX (https://libgdx.com/).
Il a été conçu pour ne jamais avoir besoin de modifier le code Java, l'ajout de contenu prévu (monstres, niveaux, cartes) passant intégralement par l'éditeur de cartes Tiled et les fichiers de configuration JSON. La modification de celui-ci est tout de même très simple grâce à l'architecture conçue pour être facilement modifiée.

Un script est fourni à la racine pour compiler et lancer le jeu facilement :

### Sur Windows
Double-cliquez sur le fichier `run.bat` situé à la racine du projet.

### Sur Linux / MacOS
Ouvrez un terminal à la racine du projet et exécutez :
```bash
./run.sh
