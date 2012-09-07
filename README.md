Projet X-Ndf
==========
Build Status
----------
master status ![Alt text](https://secure.travis-ci.org/jeantil/xndf.png?branch=master)
repo status ![Alt text](https://secure.travis-ci.org/jeantil/xndf.png)
Roadmap
----------
### Epics de la v1.0
- (done) Création d'une note de frais (stockage mongodb) issues 
- (done) Upload des fichiers de justificatif (jpeg/pdf et stockage mongodb si possible)
- (done) Intégration openid
- (done) Création du fichier excel de note de frais sur le modèle de l'excel actuel (intégration de poi ? )
- (in progress) Envoi par mail de l'excel et des elements de justificatif à nadia et a sara

### Epics de la v2.0
- (in progress) compatibilité mobile
- intégration avec l'appareil photo du téléphone
- correction des problèmes levés par nadia et sara 

### Epics de la v3.0
- intégration avec google OCR pour tenter de générer la ligne de note de frais automatiquement depuis l'image du justif

Developper sur X-NDF server
----------
### Prérequis
Le strict minimum est d'avoir une jvm, sbt 0.11.3, un clone du projet, un serveur mongo et un éditeur de texte.
Quelque soit votre mode de développement préféré il faut commencer par lancer `sbt` depuis le dossier server du workspace et mettre à jour les dépendances :

    $ sbt
    [info] Loading global plugins from D:\programs\Java\sbt\plugins
    [info] Loading project definition from D:\devel\perso\xndf\project
    [info] Set current project to Xndf (in build file:/D:/devel/perso/xndf/)
    sbt (Xndf)> update
    sbt (Xndf)> update-classifiers 
La seconde commande est optionnelle mais permet de télécharger les sources et les javadocs (pour les artéfacts qui les ont publiés). 

### Lancer la console play
Ca ne sert a rien mais ça affiche le logo :) Depuis la racine du projet lancez `sbt play` :

    $ sbt play
    [info] Loading global plugins from D:\programs\Java\sbt\plugins
    [info] Loading project definition from D:\devel\perso\xndf\project
    [info] Set current project to Xndf (in build file:/D:/devel/perso/xndf/)
           _            _
     _ __ | | __ _ _  _| |
    | '_ \| |/ _' | || |_|
    |  __/|_|\____|\__ (_)
    |_|            |__/
    
    play! 2.0, http://www.playframework.org
    
    > Type "help play" or "license" for more information.
    > Type "exit" or use Ctrl+D to leave this console.
    
    sbt (Xndf)>

### Configurer un IDE
Play supporte en natif la configuration des deux plux gros IDE java : [Eclipse (ScalaIDE)](http://scala-ide.org/download/current.html), [IntelliJ (Community suffit)](http://confluence.jetbrains.net/display/IDEADEV/IDEA+11.1+EAP). Le détail des instructions pour eclipse est là http://www.playframework.org/documentation/2.0/IDE , en court il s'agit de lancer la console play et d'utiliser `eclipsify with-source=true` 
    
    sbt (Xndf)> eclipsify with-source=true
    [info] Compiling 7 Scala sources and 1 Java source to D:\devel\perso\xndf\target\scala-2.9.1\classes...
    [warn]        _            _
    [warn]  _ __ | | __ _ _  _| |
    [warn] | '_ \| |/ _' | || |_|
    [warn] |  __/|_|\____|\__ (_)
    [warn] |_|            |__/
    [warn]
    [warn] play! 2.0, http://www.playframework.org
    [info] ...about to generate an Intellij project module(SCALA) called xndf.iml
    [warn] xndf.iml was generated
    [warn] If you see unresolved symbols, you might need to run compile first.
    [warn] Have fun!
    [success] Total time: 9 s, completed 22 mars 2012 13:49:27
    sbt (Xndf)>

Pour IntelliJ, c'est tout aussi simple avec `idea with-sources` : 

    sbt (Xndf)> idea with-sources
    [info] Compiling 7 Scala sources and 1 Java source to D:\devel\perso\xndf\target\scala-2.9.1\classes...
    [warn]        _            _
    [warn]  _ __ | | __ _ _  _| |
    [warn] | '_ \| |/ _' | || |_|
    [warn] |  __/|_|\____|\__ (_)
    [warn] |_|            |__/
    [warn]
    [warn] play! 2.0, http://www.playframework.org
    [info] ...about to generate an Intellij project module(SCALA) called xndf.iml
    [warn] xndf.iml was generated
    [warn] If you see unresolved symbols, you might need to run compile first.
    [warn] Have fun!
    [success] Total time: 9 s, completed 22 mars 2012 13:49:27
    sbt (Xndf)>

### Les commandes sbt utiles : 

- `update` met à jour les dépendances
- `compile` compile l'application
- `test` joue les tests unitaires
- `~ [command]` joue [command] en continu (compile et test sont de bonnes idées)
- `run` lance l'application


License
--------

See LICENSE file.