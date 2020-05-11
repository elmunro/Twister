var ConnexionService = function () {

    var setConnexionCookie = function () {
        if (env.isConnected) {
            CookieService().createCookie("ConnexionKey", env.key, 6);
            CookieService().createCookie("UserLogin", env.login, 6);
            CookieService().createCookie("UserID", env.uid, 6);
        } else {
            console.log("Can't set the cookie, not connected");
        }
    };

    var disconnect = function () {
        CookieService().eraseCookie("ConnexionKey");
        CookieService().eraseCookie("UserLogin");
        CookieService().eraseCookie("UserID");
        env.login = null;
        env.uid = null;
        env.key = null;
        location.reload();
    };

    var checkConnected = function () {
        var ck = CookieService().readCookie("ConnexionKey");
        var login = CookieService().readCookie("UserLogin");
        var uid = CookieService().readCookie("UserID");
        if (ck === null || login === null || uid === null)
            console.log("no connexion cookie, please log in");
        else {
            console.log("Logging you in with old cookie");
            env.isConnected = true;
            env.key = ck;
            env.login = login;
            env.uid = uid;
            mediator.publish("UserConnected");
        }
    };
    return {
        disconnect : disconnect,
        setConnexionCookie: setConnexionCookie,
        checkConnected: checkConnected
    }
};

