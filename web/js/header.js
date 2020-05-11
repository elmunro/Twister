/**
 * Service des modals du header, gère la génération du html, l'activation des event et la soumission du formulaire, ainsi que la connexion:
 *   Si non connecté -> affiche et gère LoginForm et ConnexionForm
 *   Si connecté -> affiche le menu et gère Deconnexion
 * @constructor
 */
var HeaderModalService = function () {
    /**
     * Init Login et Register, ou le menu d'user connecté
     * @constructor
     */
    var InitHeaderService = function () {
        if (env.isConnected) {

            InitConnected();
            console.log("Init header service : Connected");
        } else {
            InitNotConnected();
            setTimeout(function () {
                if (!env.isConnected) {
                    $("#loginModal").show();
                }
            }, 200);
            console.log("Init header service : Not connected");
        }
        SearchBarInit();
    };


    /**
     * Requette AJAX de connexion et gestion de la réponse
     * @constructor
     */
    var TryConnect = function () {
        $("#login_div_erreur").text("");
        var submitLog = document.getElementById("submitConnexion");
        submitLog.disabled = true;
        $.ajax({
            method: "GET",
            url: env.path + "/user/login",
            data: {"login": $("#loginPseudo").val(), "pswd": $("#loginMdp").val()},
            success: function (response) {
                console.log(response);
                if (response.error === undefined) {
                    $("#loginModal").hide();
                    env.isConnected = true;
                    env.uid = response.id;
                    env.login = response.login;
                    env.key = response.key;
                    submitLog.disabled = false;
                    ConnexionService().setConnexionCookie();
                    mediator.publish("UserConnected", env.key);
                } else {
                    env.isConnected = false;
                    submitLog.disabled = false;
                    if (response.error.error_type === "Client error") {
                        $("#login_div_erreur").text(response.error.error_message);
                    } else {
                        $("#login_div_erreur").text("Something went wrong : " + response.error.error_message);
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                submitLog.disabled = false;
                $("#login_div_erreur").text(response.error.error_message);
            }
        });
    };

    /**
     * Requette AJAX de l'inscription et gestion de la réponse
     * @constructor
     */
    var TryRegister = function () {
        if (!validateRegister()) {
            return;
        }
        var savedLogin = $("#registerPseudo").val();
        var submitReg = document.getElementById("submitInscription");
        submitReg.disabled = true;
        $.ajax({
            method: "GET",
            url: env.path + "/user/create",
            data: {
                "login": $("#registerPseudo").val(),
                "pswd": $("#registerMdp").val(),
                "nom": $("#registerNom").val(),
                "prenom": $("#registerPrenom").val(),
            },
            success: function (response) {
                console.log(response);
                if (response.error === undefined) {
                    $("#registerModal").hide();
                    alert("User created, please login");
                    $("#loginModal").show();
                    $("#loginPseudo").focus();
                    $("#loginPseudo").val(savedLogin);

                    submitReg.disabled = false;
                } else {
                    submitReg.disabled = false;
                    if (response.error.error_type === "Client error") {
                        $("#register_div_erreur").text(response.error.error_message);
                    } else {
                        $("#register_div_erreur").text("Something went wrong : " + response.error.error_message);
                    }
                }
            }
            ,
            error: function (jqXHR, textStatus, errorThrown) {
                submitReg.disabled = false;
                alert(errorThrown);
            }
        });
    };

    /**
     * Init des éléments de conexion (display de la modal, submit)
     * @constructor
     */
    var LoginInit = function () {
        $(document).on("click", "#loginModalButton", function () {
            $("#loginModal").show();
            $("#loginPseudo").focus();
        });
        $(document).on("click", "#loginSpan", function () {
            $("#loginModal").hide();
        });

        $(document).on("submit", "#loginModal", function (event) {
            event.preventDefault();
            TryConnect();
            RefreshMainPanel();
        });
        $("#oubliMdp").click(function (event) {
            //TODO gerer ce cas
        });
        $("#registerInstead").click(function (event) {
            $("#loginModal").hide();
            $("#registerModal").show();
            $("#registerNom").focus();
        });
    };

    /**
     * Init des éléments d'inscription (display de la modal, submit)
     * @constructor
     */
    var RegisterInit = function () {
        $(document).on("click", "#registerModalButton", function () {
            $("#registerModal").show();
            $("#registerNom").focus();
        });
        $(document).on("click", "#registerSpan", function () {
            $("#registerModal").hide();
        });

        $(document).on("submit", "#registerModal", function (event) {
            event.preventDefault();
            $("#register_div_erreur").text("");
            TryRegister();
            RefreshMainPanel();
        });

        $("#loginInstead").click(function (event) {
            $("#loginModal").show();
            $("#loginPseudo").focus();
            $("#registerModal").hide();

        });
    };

    /**
     * Init de la barre de recherche et de sa réactivité
     * @constructor
     */
    var SearchBarInit = function () {
        $(document).on("active", "#searchBar", function (event) {
            event.preventDefault();
            //console.log($("#searchBar input").val());
        });

        $(document).on("submit", "#searchBar", function (event) {
            event.preventDefault();
            //console.log($("#searchBar input").val());
            Search($("#searchBarInput").val());

            //empty input after search
            //$("#searchBarInput").val("");
        });
    };

    var Search = function (query="") {
        if (!env.isConnected) {
            return;
        }
        this.profileID;

        if ($("#profileDiv").html() !== undefined && env.user.id ) {
            console.log(env.user.id)
            this.profileID = env.user.id;
        } else {
            this.profileID = "-11111";
        }
        $.ajax({
            method: "GET",
            url: env.path + "/msg/search",
            data: {
                "key": env.key,
                "query": query,
                "profileID": this.profileID
            },
            success: function (rep) {
                console.log(rep);
                if (rep.error === undefined) {
                    if(rep == {})
                        //TODO hande search res
                    if (rep.searchResult === undefined) {
                        mediator.publish(rep.error.error_message);
                        return;
                    }
                    env.messages = {"messages": [], "comments": []};
                    env.messages.messages = rep.messages;
                    mediator.publish("MessageRefresh");
                } else {
                    alert(rep.error.error_message);
                }
            }
            ,
            error: function (jqXHR, textStatus, errorThrown) {
                submitReg.disabled = false;
                alert(errorThrown);
            }
        });

    };

    /**
     * Fonction à appeler pour initialiser Login et Register (donc, si l'user n'est pas connecté)
     * @constructor
     */
    var InitNotConnected = function () {
        if ($("#loginModal").html() === undefined) {
            setTimeout(InitNotConnected, 200);
            return;
        } else {
            LoginInit();
            RegisterInit();
            $(window).on("click", function (event) {
                if (event.target === document.getElementById("loginModal")) {
                    $("#loginModal").hide();
                } else if (event.target === document.getElementById("registerModal")) {
                    $("#registerModal").hide();
                }
            });
        }

    };

    /**
     * Inits header as connected user, hides modals and connect/register buttons
     * @constructor
     */
    var InitConnected = function () {
        if ($("#loginModal").html() === undefined) {
            setTimeout(InitConnected, 200);
            return;
        } else {
            console.log("InitConnected");
            $("#loginModal").hide();
            $("#registerModal").hide();
            $("#registerModalButton").hide();
            $("#loginModalButton").hide();
            $("#deconnexion").show();
            $("#profile").show();
            $(document).on("click", "#deconnexion", function (event) {
                event.preventDefault();
                env.ConnexionService.disconnect();
            });

            $(document).on("click", "#profile", function (event) {
                event.preventDefault();
                env.ProfileService.loadUserProfile();
            });
        }

    };

    /**
     * Validation du formulaire d'inscription
     */
    var validateRegister = function () {
        var regPseudo = document.getElementById("registerPseudo");
        var regNom = document.getElementById("registerNom");
        var regPrenom = document.getElementById("registerPrenom");
        var regMdp = document.getElementById("registerMdp");
        var regVerif = document.getElementById("verifyMdp");
        console.log(regPseudo.value);
        if (regPseudo.value.length > 20 || regPseudo.value.length < 2) {
            $("#register_div_erreur").text("Pseudo obligatoire, taille min de 2 et max de 20");
            return false;
        }
        if (regNom.value.length > 50 || regPseudo.value.length < 2) {
            $("#register_div_erreur").text("Nom obligatoire, taille min de 2 et max de 50");
            return false;
        }
        if (regPrenom.value.length > 50 || regPseudo.value.length < 2) {
            $("#register_div_erreur").text("Prenom obligatoire, taille min de 2 et max de 50");
            return false;
        }
        if (regMdp.value.length < 8) {
            $("#register_div_erreur").text("Mot de passe obligatoire, taille min de 8");
            return false;
        }
        if (regMdp.value !== regVerif.value) {
            $("#register_div_erreur").text("Mots de passe différends");
            return false;
        }
        return true;
    };

    InitHeaderService();
    return {
        InitHeaderService: InitHeaderService,
        InitConnected: InitConnected
    }
};


/**
 * Change le style du header (barre de navigation) quand l'icone est cliqué
 */
var icon = function () {
    var x = document.getElementById("header_buttons");
    if (x.className === "header-right") {
        x.className += " responsive";
    } else {
        x.className = "header-right";
    }
};