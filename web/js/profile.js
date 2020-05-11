var ProfileService = function () {
        var loadUserProfile = function (login=env.login) {
            document.getElementById("profileDiv").style.visibility = "visible";
            document.getElementById("userMessagesDiv").style.visibility = "visible";
            document.getElementById("friendListDiv").style.visibility = "visible";
            document.getElementById("allMessagesDiv").style.visibility = "hidden";
            InitProfileInfo(login);
            InitFriendList(login);
            InitHandlers();
        };

        var InitHandlers = function () {
            $("#userMessagesDiv").on("click", function () {
                $("#messageDiv").show();
                $("#friendList").hide();
            });
            $("#friendListDiv").on("click", function () {
                $("#messageDiv").hide();
                $("#friendList").show()
            });
        };

        var InitProfileInfo = function (login, isCurrentUser) {
            if(env.isConnected){
                $.ajax({
                    method: "GET",
                    url: env.path + "/user/profile",
                    data: {"key": env.key, "login": login},
                    success: function (response) {
                        console.log(response);
                        if (response.error === undefined && response.u_info !== undefined) {
                            if (isCurrentUser) {
                                //todo gérer suppression de msg
                            }
                            env.user.nom = response.u_info.nom;
                            env.user._id = response.u_info._id;
                            env.user.prenom = response.u_info.prenom;
                            env.user.login = response.u_info.login;
                            env.user.imgRef = response.u_info.imgReference;
                            mediator.publish("ProfileRefresh", login);
                        } else {
                            if (response.error.error_type === "Client error") {
                                alert(response.error.error_message);
                            } else {
                                alert("Something went wrong : " + response.error.error_message);
                            }
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert(response.error.error_message);
                    }
                });
            }

        };

        var InitFriendList = function (login) {
            //console.log("InitProfileInfo : starting for " + env.login);
            $.ajax({
                method: "GET",
                url: env.path + "/friend/list",
                data: {"key": env.key, "login" : login},
                success: function (response) {
                    console.log(response);
                    if (response.error === undefined && response.users !== undefined) {
                        env.users = response.users;
                        mediator.publish("ProfileRefresh", login);
                    } else {
                        if (response.error.error_type === "Client error") {
                            alert(response.error.error_message);
                        } else {
                            alert("Something went wrong : " + response.error.error_message);
                        }
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(response.error.error_message);
                }
            });
        };
        /**
         * Renders profile template in #profileDiv
         * @constructor
         */
        var RenderProfileInfoTemplate = function () {
            var data = env.user;
            var template = $('#profileTemplateScript').html();
            var output = Mustache.render(template, data);
            $('#profileDiv').html(output);
            ProfileInfoHandlers(data);
        };

        var ProfileInfoHandlers = function () {
            if($("#follow_" + env.user._id).html() === undefined){
                setTimeout(ProfileInfoHandlers, 200);
                console.log("RETRY "+env.user._id);
                return;
            }
            $("#follow_" + env.user._id).on("click", function(event){
                event.preventDefault();
                var id=event.target.id.split("_")[1];
                follow(id);
            });

            if(env.user.login != env.login){
                console.log("Showing add button for other users");
                $("#follow_" + env.user._id).show();
            }else{
                console.log("User profile is current user");
            }
        };

        /**
         * Renders friend template in #frienList
         * @constructor
         */
        var RenderFriendListTemplate = function () {
            var data = env;
            var template = $("#friendListTemplateScript").html();
            var output = Mustache.render(template, data);
            //console.log(output);
            $('#friendList').html(output);
            for (var i = 0; i < data.users.length; i++) {
                if(data.users[i].login == env.login){
                    $("#unfollow_" + data.users[i]._id).hide();
                }
                $("#unfollow_" + data.users[i]._id).on("click", function (event) {
                    event.preventDefault();
                    var id=event.target.id.split("_")[1];
                    unfollow(id);
                });

                $("#f_login_"+data.users[i]._id).on("click", function (event) {
                    event.preventDefault();
                    var login = event.target.text;
                    console.log(login);
                    loadUserProfile(login);
                });
            }
        };


        var RenderTemplates = function () {
            if ($('#profileTemplateScript').html() === undefined
                || $("#friendListTemplateScript").html() === undefined
                || $('#profileDiv').html() === undefined
            ||  env.users === undefined) {
                setTimeout(RenderTemplates, 200);
                return;
            }
            $("#messageDiv").show();
            $("#friendList").hide();
            RenderProfileInfoTemplate();
            RenderFriendListTemplate()
        };

        var unfollow = function(id) {
            if (env.isConnected) {
                $.ajax({
                    method: "GET",
                    url: env.path + "/friend/remove",
                    data: {"key": env.key, "id_friend": id},
                    success: function (response) {
                        //console.log(response);
                        if (response.error === undefined) {
                            console.log("User successefuly deleted !");
                            mediator.publish("ProfileRefresh");
                        } else {
                            if (response.error.error_type === "Client error") {
                                alert(response.error.error_message);
                            } else {
                                alert("Something went wrong : " + response.error.error_message);
                            }
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert(response.error.error_message);
                    }
                });
            }
        };

    var follow = function(id) {
        if (env.isConnected) {
            $.ajax({
                method: "GET",
                url: env.path + "/friend/add",
                data: {"key": env.key, "id_friend": id},
                success: function (response) {
                    //console.log(response);
                    if (response.error === undefined) {
                        console.log("User successefuly friended !");
                        //TODO refresh button state
                    } else {
                        if (response.error.error_type === "Client error") {
                            alert(response.error.error_message);
                        } else {
                            alert("Something went wrong : " + response.error.error_message);
                        }
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(response.error.error_message);
                }
            });
        }
    };

    return {
            loadUserProfile: loadUserProfile,
            RenderTemplates: RenderTemplates
        }
    }
;