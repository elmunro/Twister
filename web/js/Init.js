var env = {};

env.noConnectionHeader = function () {
    if (env.isConnected) {
        $("#noConnectedTitle").hide();
    } else {
        $("#noConnectedTitle").show();
    }
};

var Init = function () {
    env.user = {};
    env.path = "http://localhost:8080";
    env.isConnected = false;
    env.messages = {"messages": [], "comments": []};
    $("#header_buttons").load("../html/header.html");
    $("#messageTemplate").load("../html/messages.html");
    $("#profileTemplate").load("../html/profile.html");
    env.HeaderModalService = HeaderModalService();
    env.MessageService = MessageService();
    env.ProfileService = ProfileService();
    env.ConnexionService = ConnexionService();
    mediator.subscribe("UserConnected", env.HeaderModalService, env.HeaderModalService.InitHeaderService);
    mediator.subscribe("Bad connexion key", env.ConnexionService, env.ConnexionService.disconnect);
    mediator.subscribe("UserConnected", env.MessageService, env.MessageService.getLatest);
    mediator.subscribe("UserConnected", env.MessageService, env.MessageService.initNewMessage);
    mediator.subscribe("UserConnected", env, env.noConnectionHeader);

    mediator.subscribe("MessageRefresh", env.MessageService, env.MessageService.RenderTemplates);
    mediator.subscribe("ProfileRefresh", env.ProfileService, env.MessageService.UserMessages);
    mediator.subscribe("ProfileRefresh", env.ProfileService, env.ProfileService.RenderTemplates);
    ConnexionService().checkConnected();
};


var RefreshMainPanel = function () {

    if (env.isConnected) {
        console.log("Refreshing main panel");
        env.MessageService.getLatest();

    } else {
        console.log("No connection : not refreshing main panel");
    }
};


/**
 * Create, read and delete cookies
 * @returns {{eraseCookie: eraseCookie, createCookie: createCookie, readCookie: readCookie}}
 * @constructor
 */
var CookieService = function () {
    /**
     * Creates a cookie
     * @param name
     * @param value
     * @param days
     */
    var createCookie = function (name, value, hours) {
        if (hours) {
            var date = new Date();
            date.setTime(date.getTime() + (hours * 60 * 60 * 1000));
            var expires = ";expires =" + date.toGMTString();
        }
        else {
            var expires = " ";
        }
        document.cookie = name + "=" + value + expires + " ; path = / ";
    };

    /**
     * Returns a cookie if it exists
     * @param name
     * @returns {*}
     */
    var readCookie = function (name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(";");
        for (var i = 0; i < ca.length; i++) {
            var c = ca [i];
            while (c.charAt(0) == " ")
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0)
                return c.substring(nameEQ.length, c.length);
        }
        return null;
    };

    var eraseCookie = function (name) {
        createCookie(name, " ", -1);
    };

    return {
        eraseCookie: eraseCookie,
        createCookie: createCookie,
        readCookie: readCookie,

    }
};

