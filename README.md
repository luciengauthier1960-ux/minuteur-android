# Minuteur — projet Android

Application native simple : on saisit une durée en minutes, on appuie sur
"Démarrer l'alarme", et un écran plein écran sonne + vibre une fois le
temps écoulé (même si le téléphone est verrouillé), avec un bouton pour
arrêter.

## Compiler l'APK avec GitHub Actions (sans rien installer)

1. Crée un compte sur https://github.com si tu n'en as pas.
2. Crée un nouveau dépôt (bouton vert **New**), par exemple nommé
   `minuteur-android`. Laisse-le vide (sans README ni .gitignore).
3. Sur la page du dépôt vide, clique **uploading an existing file**.
4. Fais glisser **tout le contenu de ce dossier** (en gardant la structure
   de sous-dossiers : `app/`, `.github/`, `build.gradle`, etc.) dans la
   zone de dépôt, puis clique **Commit changes**.
5. Va dans l'onglet **Actions** du dépôt. Une action "Build APK" se lance
   automatiquement (2 à 4 minutes).
6. Une fois le workflow terminé (coche verte), clique dessus, puis dans
   la section **Artifacts** en bas de page, télécharge `minuteur-apk`
   (fichier .zip contenant `app-debug.apk`).
7. Transfère le fichier `.apk` sur ton téléphone (Drive, mail, câble...),
   ouvre-le et installe-le. Android peut demander d'autoriser
   "l'installation depuis cette source" — accepte pour ce fichier.
8. Au premier lancement, si Android le demande, autorise les
   "alarmes exactes" pour l'application dans les réglages (obligatoire
   sur Android 12+ pour un déclenchement précis).
