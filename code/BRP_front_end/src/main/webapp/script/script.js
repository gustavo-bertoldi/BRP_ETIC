/*************** Variables globales ******************/

test_select_descriptif = false; //permet de ne pas supprimer la selection d'un element à peine faite
test_manageProjet = false;
var barreInsertionNextId = 19; //Mettre à 0
var barreInsertionChiffrageNextId = 3; //Mettre à 0
var testChoixPresent = false;

/************** Appels au chargement de la page ******************/
$(document).ready(function () {
  //Récupère l'arbo Descriptifs de la BDD
  $.ajax({
    url: "./ActionServlet",
    method: "GET",
    data: {
      todo: "arboDescriptifs",
    },
    dataType: "json",
  })
    .done(function (response) {
      // Fonction appelée en cas d'appel AJAX réussi
      console.log("Response", response); // LOG dans Console Javascript
      if (!response.Error) {
        //On insère l'arbo dans le HTML
        for (let i = 0; i < response.arborescence.length; i++) {
          const chapitre = response.arborescence[i];
          $("#arboBDD").append(
            "<div class='lineBDD lineChapitre'><span class='iconBDD'><i class='fas fa-caret-right arrow'></i></span><div class='intitule'>" +
              chapitre.intituleChapitre +
              "</div></div>"
          );
          for (let j = 0; j < chapitre.categories.length; j++) {
            const categorie = chapitre.categories[j];
            $("#arboBDD").append(
              "<div class='lineBDD lineCategorie'><span class='iconBDD'><i class='fas fa-caret-right arrow'></i></span><div class='intitule'>" +
                categorie.intituleCategorie +
                "</div></div>"
            );
            for (let k = 0; k < categorie.familles.length; k++) {
              const famille = categorie.familles[k];
              $("#arboBDD").append(
                "<div class='lineBDD lineFamille'><span class='iconBDD'><i class='fas fa-caret-right arrow'></i></span><div class='intitule'>" +
                  famille.intituleFamille +
                  "</div></div>"
              );
              for (let l = 0; l < famille.sousFamilles.length; l++) {
                const sousFamille = famille.sousFamilles[l];
                $("#arboBDD").append(
                  "<div class='lineBDD lineSousFamille'><span class='iconBDD'><i class='fas fa-caret-right arrow'></i></span><div class='intitule'>" +
                    sousFamille.intituleSousFamille +
                    "</div></div>"
                );
                for (let m = 0; m < sousFamille.descriptifs.length; m++) {
                  const descriptif = sousFamille.descriptifs[m];
                  if (descriptif.type == "generique") {
                    $("#arboBDD").append(
                      "<div class='lineBDD lineDescriptif generique'><input type='hidden' class='idDescriptif' value=" +
                        descriptif.id +
                        "/><span class='iconBDD'>-</span><div class='intitule'>" +
                        descriptif.nom +
                        "</div></div>"
                    );
                  } else if (descriptif.type == "ouvrage") {
                    $("#arboBDD").append(
                      "<div class='lineBDD lineDescriptif ouvrage'><input type='hidden' class='idDescriptif' value=" +
                        descriptif.id +
                        "/><span class='iconBDD'>-</span><div class='intitule'>" +
                        descriptif.nom +
                        "</div></div>"
                    );
                  } else if (descriptif.type == "prestation") {
                    $("#arboBDD").append(
                      "<div class='lineBDD lineDescriptif prestation'><input type='hidden' class='idDescriptif' value=" +
                        descriptif.id +
                        "/><span class='iconBDD'>-</span><div class='intitule'>" +
                        descriptif.nom +
                        "</div></div>"
                    );
                  }
                }
              }
            }
          }
        }
        $(".lineDescriptif").click(function () {
          select_descriptif(this);
        });
        $(".lineChapitre").click(function () {
          gestion_arbo_bdd(this);
        });
        $(".lineCategorie").click(function () {
          gestion_arbo_bdd(this);
        });
        $(".lineFamille").click(function () {
          gestion_arbo_bdd(this);
        });
        $(".lineSousFamille").click(function () {
          gestion_arbo_bdd(this);
        });
      }
    })
    .fail(function (error) {
      // Fonction appelée en cas d'erreur lors de l'appel AJAX
      //console.log("Error", error); // LOG dans Console Javascript
      console.log(
        "Erreur lors de l'appel AJAX pour recuperer l'arborescence BDD"
      );
    });

  $(".container").first().click(unset_select_descriptif);
  $(".container").last().click(SuppressionChoixInsertionTitre);
  addEventsDescriptifs();
  AjoutEventSupprLigneChiffrage();

  //à ajouter une fois lorsque l'utilisateur cherche un projet
  $(".textLineProjet").click(function () {
    clicProjet($(this).parent());
  });
});

/****************** Fonctions (popup) *********************/
function popUpNomProjet(sens, idProjet) {
  //on le montre
  if (sens) {
    $("#popUpWindow").css("display", "flex");
    $("#container").css("filter", "blur(5px)");
  }
  //sinon on le cache
  else {
    $("#popUpWindow").hide();
    $("#nomProjetInput").val("");
    $("#container").css("filter", "blur(0px)");
  }

  //on tente de duppliquer
  if (idProjet)
    //on essaie juste de créer un nouveau projet
    $(".popupNomProjet > .buttons > .button")
      .last()
      .click(function () {
        dupliquerProjet(idProjet);
      });
  else $(".popupNomProjet > .buttons > .button").last().click(createProject);
}

/****************** Fonctions (partie gauche) *********************/
function createProject() {
  var nomProjet = $("#nomProjetInput").val();

  $.ajax({
    url: "./ActionServlet",
    method: "POST",
    data: {
      todo: "creationProjet",
      nomProjet: nomProjet,
    },
    dataType: "json",
  }).done(function (response) {
    // Fonction appelée en cas d'appel AJAX réussi
    console.log("Response", response);

    //on ferme le popup
    popUpNomProjet(false);

    ouvrirProjet(response["idProjet"]);
  });
}

function clicProjet(element) {
  var idProjet = $(element).children(".idProjet").val();
  var action = $(element).children(".actionProjet").val();

  if (action == "ouvrir") ouvrirProjet(idProjet);
  else popUpNomProjet(true, idProjet);
}

function dupliquerProjet(idProjet) {
  var nomProjet = $("#nomProjetInput").val();

  $.ajax({
    url: "./ActionServlet",
    method: "POST",
    data: {
      todo: "dupliquerProjet",
      idProjet: idProjet,
      nomProjet: nomProjet,
    },
    dataType: "json",
  }).done(function (response) {
    // Fonction appelée en cas d'appel AJAX réussi
    console.log("Response", response);

    //on ferme le popup
    popUpNomProjet(false);

    ouvrirProjet(response["newIdProjet"]);
  });
}
////////////////////////////////////////////////////
function ouvrirProjet(idProjet) {
  var xslDocumentUrl =
    "/Users/louisrob/Documents/Projets/ETIC/Etude_BRP/code/XMLfiles/ouvrirProjet.xslt";
  var xmlDocumentUrl =
    "/Users/louisrob/Documents/Projets/ETIC/Etude_BRP/code/XMLfiles/" +
    idProjet +
    ".xml";
  var idElementRemplacement = $("#idElementInsertionProjet");

  var xsltProcessor = new XSLTProcessor();

  // Chargement du fichier XSL à l'aide de XMLHttpRequest synchrone
  var xslDocument = chargerHttpXML(xslDocumentUrl);

  // Importation du .xsl
  xsltProcessor.importStylesheet(xslDocument);

  // transfère les paramètres à la feuille de style
  //xsltProcessor.setParameter(null, "idLot", code);

  // Chargement du fichier XML à l'aide de XMLHttpRequest synchrone
  var xmlDocument = chargerHttpXML(xmlDocumentUrl);

  // Création du document XML transformé par le XSL
  var newXmlDocument = xsltProcessor.transformToDocument(xmlDocument);

  //On remplace toutes les balises concernant l'ancien projet actuellement existantes par les nouvelles
  $(".container").last().html(newXmlDocument);
}

//charge le fichier XML se trouvant à l'URL relative donné dans le paramètre et le retourne
function chargerHttpXML(xmlDocumentUrl) {
  var httpAjax;

  httpAjax = window.XMLHttpRequest
    ? new XMLHttpRequest()
    : new ActiveXObject("Microsoft.XMLHTTP");

  if (httpAjax.overrideMimeType) {
    httpAjax.overrideMimeType("text/xml");
  }

  //chargement du fichier XML à l'aide de XMLHttpRequest synchrone (le 3ème paramètre est défini sur false)
  httpAjax.open("GET", xmlDocumentUrl, false);
  httpAjax.send();

  return httpAjax.responseXML;
}

////////////////////////////////////////////////////

function modifierInfosProjet() {
  var typeMarche = $(".typeMarche").val();
  alert(typeMarche);
}

function unset_select_descriptif() {
  if (!test_select_descriptif) {
    $(".selectDescriptif").removeClass("selectDescriptif");
  }
  test_select_descriptif = false;
}

function select_descriptif(element) {
  $(".selectDescriptif").removeClass("selectDescriptif");
  $(element).addClass("selectDescriptif");
  test_select_descriptif = true;
  //alert($(element).children(".idDescriptif").val());	//affiche l'idDescriptif selectionné
}

function gestion_menu_bdd(panel) {
  if (panel == "biblio") {
    $(".menuBiblio").css("color", "black");
    $(".menuInfos").css("color", "grey");
    $("#arboBDD").show();
    $("#infosProjet").hide();
  }

  if (panel == "infos") {
    $(".menuInfos").css("color", "black");
    $(".menuBiblio").css("color", "grey");
    $("#infosProjet").show();
    $("#arboBDD").hide();
  }
}

function apply_filter() {
  var filtre = $(".filtre").val();
  var array_show = new Array(),
    array_hide = new Array();

  switch (filtre) {
    case "generique":
      array_show = ["generique"];
      array_hide = ["ouvrage", "prestation"];
      break;
    case "ouvrage":
      array_show = ["ouvrage"];
      array_hide = ["generique", "prestation"];
      break;
    case "prestation":
      array_show = ["prestation"];
      array_hide = ["ouvrage", "generique"];
      break;
    default:
      array_show = ["generique", "ouvrage", "prestation"];
      array_hide = [];
  }

  for (var i = 0; i < array_show.length; i++) {
    $("." + array_show[i]).each(function () {
      //on va selectionner le parent
      var getParent = false;
      var beforeElement = this;
      while (getParent == false) {
        beforeElement = $(beforeElement).prev();
        if (!beforeElement.attr("class").includes("lineDescriptif")) {
          var parent = beforeElement;
          getParent = true;
        }
      }

      //si le parent est visible alors on montre
      if ($(parent).is(":visible")) $(this).css("display", "flex");
    });
  }
  for (var i = 0; i < array_hide.length; i++) {
    $("." + array_hide[i]).each(function () {
      $(this).hide();
    });
  }
}

function show_catConstruction() {
  var val = $("#categorieConstruction").val();

  if (val == "") {
    $(".lineSousCatConstruction").hide();
    $(".lineCaractDim").hide();
  } else {
    $(".lineSousCatConstruction").css("display", "flex");
    $(".lineCaractDim").css("display", "flex");
    //lien BDD
  }
}

function gestion_arbo_bdd(element) {
  //on range les différents niveaux de l'arborescence dans un tableau
  var ordre_arbo = new Map();
  ordre_arbo.set("lineChapitre", 1);
  ordre_arbo.set("lineCategorie", 2);
  ordre_arbo.set("lineFamille", 3);
  ordre_arbo.set("lineSousFamille", 4);

  //on détermine le rang de l'objet clické
  var classElement = $(element).attr("class").substr(8);
  var rangElement = ordre_arbo.get(classElement);

  //on détermine le type d'opération
  var spanArrow = $(element).children(".iconBDD");
  var classArrow = $(spanArrow).children(".arrow").attr("class");

  if (classArrow.includes("down")) var action = "remonter";
  else var action = "derouler";

  //on modifie la fleche
  if (action == "derouler")
    $(spanArrow).html('<i class="fas fa-caret-down arrow">');
  else $(spanArrow).html('<i class="fas fa-caret-right arrow">');

  //on détermine tous les objets de rangs directement supérieur ou égal à 5
  var oldElement = element;
  var nextElement = null;
  var classNextElement = null;
  var rangNextElement = null;
  var rangOldElement = null;
  var valFilter = $(".filtre").val();
  do {
    nextElement = $(oldElement).next();
    classNextElement = $(nextElement).attr("class").substr(8);
    rangNextElement = ordre_arbo.get(classNextElement);

    if (classNextElement && rangNextElement == null) rangNextElement = 5;

    //on descend dans l'arbo
    if (action == "derouler") {
      if (
        rangNextElement == rangElement + 1 ||
        (rangNextElement == 5 && rangOldElement == rangElement)
      ) {
        if (rangNextElement == 5 && valFilter != "") {
          //si c'est un descriptif et qu'un filtre est demandé, on l'applique
          if (classNextElement.includes(valFilter)) {
            $(nextElement).css("display", "flex");
          }
        } else {
          $(nextElement).css("display", "flex");
        }
      }
    } else {
      //on remonte dans l'arbo
      if (rangNextElement > rangElement) {
        $(nextElement).css("display", "none");
      }
    }

    if (
      rangNextElement != null &&
      rangNextElement > rangElement &&
      rangNextElement != 5
    ) {
      //on modifie la fleche de l'élement enfant
      spanArrow = $(nextElement).children(".iconBDD");
      if (action == "remonter")
        $(spanArrow).html('<i class="fas fa-caret-right arrow">');
    }

    oldElement = nextElement;
    rangOldElement = rangNextElement;
  } while (rangNextElement > rangElement);
}

function display_manage_project() {
  if (!test_manageProjet) {
    $("#manageProjet").css("display", "flex");
    $("#arboBDD").hide();
    $("#menu").hide();
    $("#infosProjet").hide();
    $(".menuGestion").hide();
    $(".nomProjet").last().html('<i class="fas fa-chevron-up">');
    test_manageProjet = true;
  } else {
    $("#manageProjet").hide();
    $("#arboBDD").show();
    $("#menu").show();
    $("#infosProjet").hide();
    $(".menuGestion").show();
    $(".nomProjet").last().html('<i class="fas fa-chevron-down">');
    test_manageProjet = false;
  }
}

/****************** Fonctions (partie droite) *********************/

function addEventsDescriptifs() {
  $(".barreInsertion").mouseenter(function () {
    var element = $("#" + $(this).attr("id") + " > .panBarreInsertion").first();
    $(element).css("border-bottom", "solid");
    $(element).css("border-width", "1.5px");
    $(element).css("border-color", "rgb(95, 95, 95)");
  });
  $(".barreInsertion").mouseleave(function () {
    var element = $("#" + $(this).attr("id") + " > .panBarreInsertion").first();
    $(element).css("border-bottom", "none");
  });
}

function AjoutEventSupprLigneChiffrage() {
  $(".descriptif > .ligneChiffrage").first().children().last().html("");
  $(".suppressionLigneChiffrage").click(function () {
    $(this).parent().remove();
  });
}

function AjouterElement(idPosition) {
  //Si un descriptif a été sélectionné alors on l'insère directement dans l'arborescence
  if ($(".selectDescriptif").length) {
    //On n'autorise pas un descriptif à se mettre avant un titre1
    if (
      $("#" + idPosition)
        .next()
        .children(":first")
        .children(":first")
        .html() != "I."
    ) {
      //On créer le div du descriptif avec le style correspondant à sa place dans l'arbo
      var divInsertionDescriptif = document.createElement("div");

      var titreAuDessus = $("#" + idPosition).prev();
      if (!titreAuDessus.hasClass("descriptif")) {
        if (titreAuDessus.hasClass("titre1")) {
          divInsertionDescriptif.className = "descriptif titre2";
        } else if (titreAuDessus.hasClass("titre2")) {
          divInsertionDescriptif.className = "descriptif titre3";
        } else if (titreAuDessus.hasClass("titre3")) {
          divInsertionDescriptif.className = "descriptif titre4";
        } else if (titreAuDessus.hasClass("titre4")) {
          divInsertionDescriptif.className = "descriptif titre5";
        }
      } else {
        var classDescriptifAuDessus = titreAuDessus.attr("class");
        divInsertionDescriptif.className = classDescriptifAuDessus;
      }

      var divInputGroupTitre = document.createElement("div");
      divInputGroupTitre.className = "input-group";

      var divInputGroupPrependNumero = document.createElement("div");
      divInputGroupPrependNumero.className = "input-group-prepend";

      var spanInputGroupText = document.createElement("span");
      spanInputGroupText.className = "input-group-text";

      divInputGroupPrependNumero.appendChild(spanInputGroupText);

      var inputTitre = document.createElement("input");
      inputTitre.type = "text";
      inputTitre.className = "form-control";
      inputTitre.placeholder = "Descriptif";

      divInputGroupTitre.appendChild(divInputGroupPrependNumero);
      divInputGroupTitre.appendChild(inputTitre);

      var divInputGroupDescription = document.createElement("div");
      divInputGroupDescription.className = "input-group description";

      var textareaFormControl = document.createElement("textarea");
      textareaFormControl.className = "form-control";
      textareaFormControl.placeholder = "Description";
      //Appel AJAX pour récupérer la description
      var idDescriptif = $(".selectDescriptif").children(":first").val();

      divInputGroupDescription.appendChild(textareaFormControl);

      if (!$(".selectDescriptif").hasClass("generique")) {
        var divInputGroupLigneChiffrage = document.createElement("div");
        divInputGroupLigneChiffrage.className = "ligneChiffrage";

        var inputLocalisation = document.createElement("input");
        inputLocalisation.type = "text";
        inputLocalisation.className = "form-control";
        inputLocalisation.placeholder = "Localisation";

        var inputQuantite = document.createElement("input");
        inputQuantite.type = "text";
        inputQuantite.className = "form-control";
        inputQuantite.placeholder = "Quantité";

        var divInputGroupPrependUnite = document.createElement("div");
        divInputGroupPrependUnite.className = "input-group-prepend";

        var spanInputGroupTextUnite = document.createElement("span");
        spanInputGroupTextUnite.className = "input-group-text";
        //Faut rajouter l'unité en AJAX

        divInputGroupPrependUnite.appendChild(spanInputGroupTextUnite);

        divInputGroupLigneChiffrage.appendChild(inputLocalisation);
        divInputGroupLigneChiffrage.appendChild(inputQuantite);
        divInputGroupLigneChiffrage.appendChild(divInputGroupPrependUnite);

        divInsertionDescriptif.appendChild(divInputGroupTitre);
        divInsertionDescriptif.appendChild(divInputGroupDescription);
        divInsertionDescriptif.appendChild(divInputGroupLigneChiffrage);

        $(divInsertionDescriptif).insertBefore($("#" + idPosition));

        //On insère une barre d'insertion au dessus du nouveau descriptif
        var divBarreInsertion = document.createElement("div");
        divBarreInsertion.className = "barreInsertion";
        divBarreInsertion.id = "barre_" + barreInsertionNextId;
        barreInsertionNextId++;
        var divPanBarreInsertionFirst = document.createElement("div");
        divPanBarreInsertionFirst.className = "panBarreInsertion";
        var divPanBarreInsertionSecond = document.createElement("div");
        divPanBarreInsertionSecond.className = "panBarreInsertion";
        divBarreInsertion.appendChild(divPanBarreInsertionFirst);
        divBarreInsertion.appendChild(divPanBarreInsertionSecond);
        var parent = divInsertionDescriptif.parentElement;
        parent.insertBefore(divBarreInsertion, divInsertionDescriptif);
        $("#" + divBarreInsertion.id).click(function () {
          AjouterElement(divBarreInsertion.id);
        });

        //On ajoute une barre d'insertion ligneChiffrage à la fin de l'ouvrage/prestation
        var divBarreInsertionLigneChiffrage = document.createElement("div");
        divBarreInsertionLigneChiffrage.className =
          "barreInsertionLigneChiffrage";
        divBarreInsertionLigneChiffrage.id =
          "barreChiffrage_" + barreInsertionChiffrageNextId;
        barreInsertionChiffrageNextId++;
        var divPanBarreInsertionChiffrageFirst = document.createElement("div");
        divPanBarreInsertionChiffrageFirst.className =
          "panBarreInsertionLigneChiffrage";
        var divPanBarreInsertionChiffrageSecond = document.createElement("div");
        divPanBarreInsertionChiffrageSecond.className =
          "panBarreInsertionLigneChiffrage";
        divBarreInsertionLigneChiffrage.appendChild(
          divPanBarreInsertionChiffrageFirst
        );
        divBarreInsertionLigneChiffrage.appendChild(
          divPanBarreInsertionChiffrageSecond
        );
        divInsertionDescriptif.appendChild(divBarreInsertionLigneChiffrage);
        $("#" + divBarreInsertionLigneChiffrage.id).click(function () {
          AjouterLigneChiffrage(divBarreInsertionLigneChiffrage.id);
        });

        //Appel de la fonction de numérotation de l'arborescence
        //var lot = parent.parentElement;
        NumerotationArbo(parent.id);
      } else {
        divInsertionDescriptif.appendChild(divInputGroupTitre);
        divInsertionDescriptif.appendChild(divInputGroupDescription);

        $(divInsertionDescriptif).insertBefore($("#" + idPosition));

        //On insère une barre d'insertion au dessus du nouveau descriptif
        var divBarreInsertion = document.createElement("div");
        divBarreInsertion.className = "barreInsertion";
        divBarreInsertion.id = "barre_" + barreInsertionNextId;
        barreInsertionNextId++;
        var divPanBarreInsertionFirst = document.createElement("div");
        divPanBarreInsertionFirst.className = "panBarreInsertion";
        var divPanBarreInsertionSecond = document.createElement("div");
        divPanBarreInsertionSecond.className = "panBarreInsertion";
        divBarreInsertion.appendChild(divPanBarreInsertionFirst);
        divBarreInsertion.appendChild(divPanBarreInsertionSecond);
        var parent = divInsertionDescriptif.parentElement;
        parent.insertBefore(divBarreInsertion, divInsertionDescriptif);
        $("#" + divBarreInsertion.id).click(function () {
          AjouterElement(divBarreInsertion.id);
        });

        //Ajout de l'hover sur toutes les barres d'insertions titre
        addEventsDescriptifs();

        //Appel de la fonction de numérotation de l'arborescence
        NumerotationArbo(parent.id);
      }
    }
  } else {
    //Sinon on propose d'insérer un titre du même niveau d'arboresence que le titre d'au dessus ou de celui d'en-dessous sous réserve d'existence
    var type1 = null;
    var type2 = null;
    var prev = $("#" + idPosition).prev();
    var next = $("#" + idPosition).next();
    if (prev !== null) {
      if (prev.hasClass("titre1")) {
        type1 = "titre1";
      } else if (prev.hasClass("titre2")) {
        type1 = "titre2";
      } else if (prev.hasClass("titre3")) {
        type1 = "titre3";
      } else if (prev.hasClass("titre4")) {
        type1 = "titre4";
      } else if (prev.hasClass("titre5")) {
        type1 = "titre5";
      }
    }
    if (next !== null && next.attr("class") !== "finLot") {
      if (next.hasClass("titre1")) {
        type2 = "titre1";
      } else if (next.hasClass("titre2")) {
        type2 = "titre2";
      } else if (next.hasClass("titre3")) {
        type2 = "titre3";
      } else if (next.hasClass("titre4")) {
        type2 = "titre4";
      } else if (next.hasClass("titre5")) {
        type2 = "titre5";
      }
    } else {
      if (type1 == "titre1") {
        type2 = "titre2";
      } else if (type1 == "titre2") {
        type2 = "titre3";
      } else if (type1 == "titre3") {
        type2 = "titre4";
      } else if (type1 == "titre4") {
        type2 = "titre5";
      }
    }

    if (type1 == type2) type2 = null;

    //On affiche un panneau demandant quel type de titre il veut afficher parmis les choix dispo
    var divInsertionTitre = document.createElement("div");
    divInsertionTitre.className = "divInsertionTitre";

    if (type1 !== null) {
      var divType1 = document.createElement("div");
      divType1.className = "sousDivInsertionTitre";
      switch (type1) {
        case "titre1":
          divType1.innerHTML = "Titre 1";
          break;
        case "titre2":
          divType1.innerHTML = "Titre 2";
          break;
        case "titre3":
          divType1.innerHTML = "Titre 3";
          break;
        case "titre4":
          divType1.innerHTML = "Titre 4";
          break;
        case "titre5":
          divType1.innerHTML = "Titre 5";
          break;
        default:
          break;
      }
      divInsertionTitre.appendChild(divType1);
    }
    if (type2 !== null) {
      var divType2 = document.createElement("div");
      divType2.className = "sousDivInsertionTitre";
      switch (type2) {
        case "titre1":
          divType2.innerHTML = "Titre 1";
          break;
        case "titre2":
          divType2.innerHTML = "Titre 2";
          break;
        case "titre3":
          divType2.innerHTML = "Titre 3";
          break;
        case "titre4":
          divType2.innerHTML = "Titre 4";
          break;
        case "titre5":
          divType2.innerHTML = "Titre 5";
          break;
        default:
          break;
      }
      divInsertionTitre.appendChild(divType2);
    }

    if (type1 == null || type2 == null) divInsertionTitre.style.height = "25px";

    var nodePositionRef = document.getElementById(idPosition);
    var parent = nodePositionRef.parentNode;
    parent.insertBefore(divInsertionTitre, nodePositionRef);

    $(".sousDivInsertionTitre").click(function () {
      AjouterTitre(this);
    });

    testChoixPresent = true;
  }
}

function SuppressionChoixInsertionTitre() {
  if (!testChoixPresent) $(".divInsertionTitre").remove();
  testChoixPresent = false;
}

function AjouterTitre(evt) {
  var titleClassName = evt.innerHTML.replace(" ", "").toLowerCase();
  //On ajoute le titre avec le bon style
  var divNewTitle = document.createElement("div");
  divNewTitle.className = "input-group " + titleClassName;

  var divInputPrepend = document.createElement("div");
  divInputPrepend.className = "input-group-prepend";

  var spanPrepend = document.createElement("span");
  spanPrepend.className = "input-group-text";

  divInputPrepend.appendChild(spanPrepend);

  var inputTitle = document.createElement("input");
  inputTitle.type = "text";
  inputTitle.className = "form-control";
  inputTitle.placeholder = evt.innerHTML;

  divNewTitle.appendChild(divInputPrepend);
  divNewTitle.appendChild(inputTitle);

  var parent = evt.parentNode.parentNode;
  parent.insertBefore(divNewTitle, evt.parentNode);

  //On insère une barre d'insertion au-dessus du nouveau titre
  var divBarreInsertion = document.createElement("div");
  divBarreInsertion.className = "barreInsertion";
  divBarreInsertion.id = "barre_" + barreInsertionNextId;
  barreInsertionNextId++;
  var divPanBarreInsertionFirst = document.createElement("div");
  divPanBarreInsertionFirst.className = "panBarreInsertion";
  var divPanBarreInsertionSecond = document.createElement("div");
  divPanBarreInsertionSecond.className = "panBarreInsertion";
  divBarreInsertion.appendChild(divPanBarreInsertionFirst);
  divBarreInsertion.appendChild(divPanBarreInsertionSecond);
  parent.insertBefore(divBarreInsertion, divNewTitle);
  $("#" + divBarreInsertion.id).click(function () {
    AjouterElement(divBarreInsertion.id);
  });
  //Ajout de l'hover sur toutes les barres d'insertions titre
  addEventsDescriptifs();

  //On supprime le panneau de choix des titres
  $(".divInsertionTitre").remove();

  //Appel de la fonction de numérotation de l'arborescence
  NumerotationArbo(parent.id);
}

function NumerotationArbo(idOnglet) {
  var numTitre1 = 0;
  var numTitre2 = 0;
  var numTitre3 = 0;
  var numTitre4 = 0;
  var numTitre5 = 0;
  $(".input-group").each(function () {
    if (
      !$(this).hasClass("description") &&
      !$(this).hasClass("ligneChiffrage") &&
      ($(this).parent().attr("id") == idOnglet ||
        $(this).parent().parent().attr("id") == idOnglet)
    ) {
      if ($(this).parent().hasClass("descriptif")) {
        if ($(this).parent().hasClass("titre1")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre1++;
          numeroTitre.html(toRoman(numTitre1) + ".");
          numTitre2 = 0;
          numTitre3 = 0;
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).parent().hasClass("titre2")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre2++;
          numeroTitre.html(toRoman(numTitre1) + "." + numTitre2);
          numTitre3 = 0;
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).parent().hasClass("titre3")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre3++;
          numeroTitre.html(numTitre2 + "." + numTitre3);
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).parent().hasClass("titre4")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre4++;
          numeroTitre.html(numTitre2 + "." + numTitre3 + "." + numTitre4);
          numTitre5 = 0;
        } else if ($(this).parent().hasClass("titre5")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre5++;
          numeroTitre.html(
            numTitre2 + "." + numTitre3 + "." + numTitre4 + "." + numTitre5
          );
        }
      } else {
        if ($(this).hasClass("titre1")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre1++;
          numeroTitre.html(toRoman(numTitre1) + ".");
          numTitre2 = 0;
          numTitre3 = 0;
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).hasClass("titre2")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre2++;
          numeroTitre.html(toRoman(numTitre1) + "." + numTitre2);
          numTitre3 = 0;
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).hasClass("titre3")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre3++;
          numeroTitre.html(numTitre2 + "." + numTitre3);
          numTitre4 = 0;
          numTitre5 = 0;
        } else if ($(this).hasClass("titre4")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre4++;
          numeroTitre.html(numTitre2 + "." + numTitre3 + "." + numTitre4);
          numTitre5 = 0;
        } else if ($(this).hasClass("titre5")) {
          var numeroTitre = $(this).children(":first").children(":first"); //On récupère le span du chiffre romain
          numTitre5++;
          numeroTitre.html(
            numTitre2 + "." + numTitre3 + "." + numTitre4 + "." + numTitre5
          );
        }
      }
    }
  });
}

function AfficherOnglet(lotAfficher) {
  $(".lot").hide();
  lotAfficher.show();
  var numBouton = lotAfficher.attr("id").slice(-1); //On récupère le numéro de l'onglet qui doit être mis en couleur plus foncée
  $(".ongletLot").css("background-color", "#3a88c5");
  if (numBouton != "+") {
    $(".ongletLot:nth-child(" + (Number(numBouton) + 1) + ")").css(
      "background-color",
      "#0070c9"
    );
  }
  //on affiche le titre du lot
  AfficherTitreLot($("#divTitreLot_" + numBouton));
}

function AfficherTitreLot(divTitreLotAfficher) {
  $(".divTitreLot").hide();
  divTitreLotAfficher.show();
}

function CreerOnglet() {
  //Création du div contenant tout le lot (on l'affiche par défaut)
  var numOnglet = Number($("#ongletsLot").children().last().prev().html()) + 1;
  var idOnglet = "lot_" + numOnglet;

  var divNouvelOnglet = document.createElement("div");
  divNouvelOnglet.className = "lot";
  divNouvelOnglet.id = idOnglet;

  //Création de deux barres d'insertion d'un titre1 et d'une fin de lot dans ce nouvel onglet
  var divBarreInsertionDessus = document.createElement("div");
  divBarreInsertionDessus.className = "barreInsertion";
  divBarreInsertionDessus.id = "barre_" + barreInsertionNextId;
  barreInsertionNextId++;
  var divPanBarreInsertionFirstDessus = document.createElement("div");
  divPanBarreInsertionFirstDessus.className = "panBarreInsertion";
  var divPanBarreInsertionSecondDessus = document.createElement("div");
  divPanBarreInsertionSecondDessus.className = "panBarreInsertion";
  divBarreInsertionDessus.appendChild(divPanBarreInsertionFirstDessus);
  divBarreInsertionDessus.appendChild(divPanBarreInsertionSecondDessus);

  var divTitre1 = document.createElement("div");
  divTitre1.className = "input-group titre1";
  var divInputPrepend = document.createElement("div");
  divInputPrepend.className = "input-group-prepend";
  var spanPrepend = document.createElement("span");
  spanPrepend.className = "input-group-text";
  divInputPrepend.appendChild(spanPrepend);
  var inputTitle = document.createElement("input");
  inputTitle.type = "text";
  inputTitle.className = "form-control";
  inputTitle.placeholder = "Titre 1";
  divTitre1.appendChild(divInputPrepend);
  divTitre1.appendChild(inputTitle);

  var divBarreInsertionDessous = document.createElement("div");
  divBarreInsertionDessous.className = "barreInsertion";
  divBarreInsertionDessous.id = "barre_" + barreInsertionNextId;
  barreInsertionNextId++;
  var divPanBarreInsertionFirstDessous = document.createElement("div");
  divPanBarreInsertionFirstDessous.className = "panBarreInsertion";
  var divPanBarreInsertionSecondDessous = document.createElement("div");
  divPanBarreInsertionSecondDessous.className = "panBarreInsertion";
  divBarreInsertionDessous.appendChild(divPanBarreInsertionFirstDessous);
  divBarreInsertionDessous.appendChild(divPanBarreInsertionSecondDessous);

  var divFinLot = document.createElement("div");
  divFinLot.className = "finLot";
  //On les insère dans la page
  divNouvelOnglet.appendChild(divBarreInsertionDessus);
  divNouvelOnglet.appendChild(divTitre1);
  divNouvelOnglet.appendChild(divBarreInsertionDessous);
  divNouvelOnglet.appendChild(divFinLot);

  $(divNouvelOnglet).insertBefore($("#ongletsLot"));

  //Création d'un nouveau bouton d'onglet avant le '+'
  var divBoutonOnglet = document.createElement("div");
  divBoutonOnglet.className = "ongletLot";
  divBoutonOnglet.innerHTML = numOnglet;
  $(divBoutonOnglet).insertBefore($(".ongletLot").last());

  //Création d'un nouveau input de titre lot
  $(".container")
    .last()
    .prepend(
      "<div class='divTitreLot' id='divTitreLot_" +
        numOnglet +
        "'><input type='text' class='titreLot' placeholder='Titre Lot' /></div>"
    );
  //$(".divTitreLot").last().attr("id") = "divTitreLot_" + numOnglet;

  //On ajoute les évènements nécessaires au click
  $("#" + divBarreInsertionDessus.id).click(function () {
    AjouterElement(divBarreInsertionDessus.id);
  });

  $("#" + divBarreInsertionDessous.id).click(function () {
    AjouterElement(divBarreInsertionDessous.id);
  });

  $(".ongletLot")
    .eq(-2)
    .click(function () {
      AfficherOnglet($("#" + idOnglet));
    });

  //Ajout de l'hover sur toutes les barres d'insertions titre
  addEventsDescriptifs();

  //On affiche par défaut
  AfficherOnglet($("#" + idOnglet));

  //On affiche le bon titre lot
  AfficherTitreLot($("#divTitreLot_" + numOnglet));

  //On numérote l'unique titre
  NumerotationArbo(idOnglet);
}

function toRoman(num) {
  if (isNaN(num)) return NaN;
  var digits = String(+num).split(""),
    key = [
      "",
      "C",
      "CC",
      "CCC",
      "CD",
      "D",
      "DC",
      "DCC",
      "DCCC",
      "CM",
      "",
      "X",
      "XX",
      "XXX",
      "XL",
      "L",
      "LX",
      "LXX",
      "LXXX",
      "XC",
      "",
      "I",
      "II",
      "III",
      "IV",
      "V",
      "VI",
      "VII",
      "VIII",
      "IX",
    ],
    roman = "",
    i = 3;
  while (i--) roman = (key[+digits.pop() + i * 10] || "") + roman;
  return Array(+digits.join("") + 1).join("M") + roman;
}

function AjouterLigneChiffrage(idBarre) {
  //Création d'une nouvelle ligneChiffrage au dessus de la barre d'insertion ligneChiffrage
  var divInputGroupLigneChiffrage = document.createElement("div");
  divInputGroupLigneChiffrage.className = "ligneChiffrage";

  var inputLocalisation = document.createElement("input");
  inputLocalisation.type = "text";
  inputLocalisation.className = "form-control";
  inputLocalisation.placeholder = "Localisation";

  var inputQuantite = document.createElement("input");
  inputQuantite.type = "text";
  inputQuantite.className = "form-control";
  inputQuantite.placeholder = "Quantité";

  var divInputGroupPrependUnite = document.createElement("div");
  divInputGroupPrependUnite.className = "input-group-prepend";

  var spanInputGroupTextUnite = document.createElement("span");
  spanInputGroupTextUnite.className = "input-group-text";
  //Faut rajouter l'unité en AJAX

  var divSuppressionLigneChiffrage = document.createElement("div");
  divSuppressionLigneChiffrage.className = "suppressionLigneChiffrage";
  var svgSuppr = document.createElement("i");
  svgSuppr.className = "fas fa-times";
  divSuppressionLigneChiffrage.appendChild(svgSuppr);

  divInputGroupPrependUnite.appendChild(spanInputGroupTextUnite);

  divInputGroupLigneChiffrage.appendChild(inputLocalisation);
  divInputGroupLigneChiffrage.appendChild(inputQuantite);
  divInputGroupLigneChiffrage.appendChild(divInputGroupPrependUnite);
  divInputGroupLigneChiffrage.appendChild(divSuppressionLigneChiffrage);

  $(divInputGroupLigneChiffrage).insertBefore($("#" + idBarre));

  //Ajout évènement suppression ligne chiffrage
  AjoutEventSupprLigneChiffrage();
}
