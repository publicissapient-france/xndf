Projet X-Ndf
==========
Roadmap
----------
### Epics de la v1.0
- Création d'une note de frais (stockage db relationnelle)
- Upload des fichiers de justificatif (jpeg/pdf et stockage mongodb si possible)
- Intégration openid
- Création du fichier excel de note de frais sur le modèle de l'excel actuel (intégration de poi ? )
- Envoi par mail de l'excel et des elements de justificatif à nadia et a sara

### Epics de la v2.0
- compatibilité mobile
- intégration avec l'appareil photo du téléphone
- intégration des problèmes levés par nadia et de sara 

### Epics de la v3.0
- intégration avec google OCR pour tenter de générer la ligne de note de frais automatiquement depuis l'image du justif

Developper sur X-NDF
----------
Le strict minimum est d'avoir une jvm, sbt 0.11.2 et un éditeur de texte. Le reste est téléchargé par le projet.

Si vous souhaitez développer dans un IDE, vous pouvez utiliser au choix ScalaIDE (basé sur eclipse) ou IntelliJ dont la version community edition supporte le plugin scala. 

Placez vous à la racine du projet et tapez sbt quand le prompt de sbt est disponible, vous n'avez plus qu'a taper au choix :
- gen-idea 
- eclipsify

pour générer la configuration pour votre IDE préféré et à charger le projet dans l'IDE de votre choix. 
 
