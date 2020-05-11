var Message = function (id, auteur, texte, date, commentaire) {
    this.id = id;
    this.auteur = auteur;
    this.texte = texte;
    this.date = date;
    if (this.commentaire == undefined) {
        this.commentaire = [];
    }
    this.commentaire = commentaire;
};

//commentaire hérite de message et rajoute source post id
var Commentaire = function (id, auteur, texte, date, commentaire, source_post_id) {
    Message.call(this, id, auteur, texte, date, commentaire);
    this.source_post_id = source_post_id;
};

//heritage des méthodes
Commentaire.prototype = Object.create(Message.prototype);


var MessageService = function () {
        var initNewMessage = function () {
            $(document).on("submit", "#newMessageForm", function (event) {
                event.preventDefault();
                postMessage();
            });
            $(window).on("click", function (event) {
                if (event.target === document.getElementById("registerModal")) {
                    $("#registerModal").hide();
                }
            });
        };
        /**
         * Gets latest messages and displays them (resets all elements in messageDiv)
         */
        var getLatest = function (key, start = 0, stop = 500) {
            if (env.isConnected) {
                $.ajax({
                    type: 'GET',
                    url: env.path+"/msg/latest",
                    data: {"key": env.key, "start": start, "stop": stop},
                    success: function (rep) {
                        console.log("Succesefully retrieved latest messages");
                        if (rep.messages === undefined) {
                            console.log(rep);
                            mediator.publish(rep.error.error_message);
                            return;
                        }
                        env.messages = {"messages": [], "comments": []};
                        for (var i = 0; i < rep.messages.length; i++) {
                            var msg = rep.messages[i];
                            if (isMessage(msg)) {
                                env.messages.messages.push(msg);
                            } else if (isComment(msg)) {
                                env.messages.comments.push(msg);
                            }
                        }
                        mediator.publish("MessageRefresh");
                    },
                    error: function (errXHR, testStatus, errorthrown) {
                        console.log(errXHR + testStatus + errorthrown);
                        alert(errXHR + testStatus + errorthrown);
                    }
                });
            } else {
                console.log("getLatest : Not connected, no messages");
            }
        };

        /**
         * Return true if msg is not a comment
         * @param msg
         * @returns {boolean}
         */
        var isMessage = function (msg) {
            if (msg.source_post_id === undefined)
                return true;
            return false;
        };

        var isComment = function (msg) {
            if (msg.source_post_id !== undefined)
                return true;
            return false;
        };

        /**
         * Renders message template in #messageDiv
         * @constructor
         */
        var RenderMessageTemplate = function () {
            var data = env.messages;
            var template = $('#messageTemplateScript').html();

            var output = Mustache.render(template, data);
            $('#messageDiv').html(output);

            for (var i = 0; i < data.messages.length; i++) {
                $("#newCommentForm_" + data.messages[i]._id).on("submit", function (event) {
                    event.preventDefault();
                    postComment(event.target);
                });
                $(window).on("click", function (event) {
                    var id = event.target.id.split("_")[1];
                    if (event.target === document.getElementById("modal_" + id)) {
                        $("#modal_" + id).hide();
                    }
                });
                $("#message_" + data.messages[i]._id+ " .message_auteur").on("click", function (event) {
                    event.preventDefault();
                    var login = event.target.text.split(" ")[0];
                    env.ProfileService.loadUserProfile(login);
                });
                $("#message_" + data.messages[i]._id + " .commentLabel").on("click", function (event) {
                    //console.log(event);
                    var id = event.target.id.split("_")[1];
                    $("#message_" + id + " .modal").show();
                });
                var date = new Date(data.messages[i].date['$date']);
                $("#message_" + data.messages[i]._id + " .message_date").text(date.toLocaleString("fr-fr"));
            }
        };

        /**
         * Renders ALL comments on a page
         * @constructor
         */
        var RenderCommentsTemplate = function () {
            var comments = env.messages.comments;
            //console.log(comments);
            var template = $('#commentTemplateScript').html();

            for (var i = comments.length - 1; i >= 0; i--) {
                var com = comments[i];
                if ($("#message_" + com.source_post_id).html() !== undefined) {
                    //console.log("Ajout commentaire " + com._id + " à " + com.source_post_id);
                    var output = Mustache.render(template, com);
                    //console.log(output);
                    $("#message_" + com.source_post_id + " .comments").append(output);
                }
            }
            for (var i = 0; i < comments.length; i++) {
                var date = new Date(comments[i].date['$date']);
                $("#comment_date_" + comments[i]._id).text(date.toLocaleString("fr-fr"));

                $("#comment" + comments[i]._id+ " .comment_auteur").on("click", function (event) {
                    event.preventDefault();
                    var login = event.target.text.split(" ")[0];
                    env.ProfileService.loadUserProfile(login);
                });
            }
        };


        var RenderTemplates = function () {
            if ($('#messageTemplateScript').html() === undefined
                || $('#commentTemplateScript').html() === undefined
                || $('#messageDiv').html() === undefined) {
                setTimeout(RenderTemplates, 200);
                return;
            }
            $.when(RenderMessageTemplate()).then(RenderCommentsTemplate());
        };

        /**
         * Post le message courant (qui est dans la balise d'id newMessageSubmit)
         */
        var postMessage = function () {
            var texte = $("#newMessageText").val();
            $("#newMessageError").text("");
            if (!newPostVerification(texte)) {
                $("#newMessageError").text("Un post ou commentaire doit avoir entre 1 et 255 charactères, ici : " + texte.length);
                return;
            }

            if (env.isConnected) {
                var submitButton = document.getElementById("newMessageSubmit");
                submitButton.disabled = true;
                $.ajax({
                    method: "GET",
                    url: env.path+"/msg/newPost",
                    data: {"key": env.key, "text": texte},
                    success: function (response) {
                        console.log(response);
                        if (response.error === undefined) {
                            submitButton.disabled = false;
                            getLatest();
                        } else {
                            if (response.error.error_type === "Client error") {
                                alert(response.error.error_message);
                            } else {
                                alert("Something went wrong : " + response.error.error_message);
                            }
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        submitButton.disabled = false;
                        alert(response.error.error_message);
                    }
                });
            } else {
                console.log("MessageService : Not connected can't post msg");
            }
        };

        var newPostVerification = function (texte) {
            texte = texte.trim();
            if (texte.length === 0)
                return false;
            if (texte.length > 255)
                return false;
            return true;
        };

        /**
         * Refreshes the html for comments on post (message) corresponding to id
         * @param id source post id to refresh
         * @constructor
         */
        var RenderCommentTemplate = function (id) {
            console.log("Re-rendering comment template for : " + id);
            var comments = env.messages.comments;
            var template = $('#commentTemplateScript').html();
            $("#message_" + id + " .comments").html("");

            for (var i = comments.length - 1; i >= 0; i--) {
                var com = comments[i];
                if (com.source_post_id == id) {
                    console.log("Ajout commentaire " + com._id + " à " + com.source_post_id);
                    var output = Mustache.render(template, com);
                    //console.log(output);
                    $("#message_" + com.source_post_id + " .comments").append(output);
                    var date = new Date(com.date['$date']);
                    $("#comment_date_" + com._id).text(date.toLocaleString("fr-fr"));

                    $("#comment" + com._id+ " .comment_auteur").on("click", function (event) {
                        event.preventDefault();
                        var login = event.target.text.split(" ")[0];
                        env.ProfileService.loadUserProfile(login);
                    });
                }
            }

        };

        /**
         *  Refreshes the list of comments on post (message) corresponding to id
         * @param id
         * @param start
         * @param stop
         */
        var refreshComments = function (id, start = 0, stop = 500) {
            if (env.isConnected) {
                $.ajax({
                    type: 'GET',
                    url: env.path+"/msg/latest",
                    data: {"key": env.key, "start": start, "stop": stop},
                    success: function (rep) {
                        console.log("Succesefully refreshed comments");
                        if (rep.messages === undefined) {
                            console.log(rep);
                            mediator.publish(rep.error.error_message);
                            return;
                        }
                        env.messages = {"messages": [], "comments": []};
                        for (var i = 0; i < rep.messages.length; i++) {
                            var msg = rep.messages[i];
                            if (isMessage(msg)) {
                                env.messages.messages.push(msg);
                            } else if (isComment(msg)) {
                                env.messages.comments.push(msg);
                            }
                        }
                        RenderCommentTemplate(id);
                    },
                    error: function (errXHR, testStatus, errorthrown) {
                        console.log(errXHR + testStatus + errorthrown);
                        alert(errXHR + testStatus + errorthrown);
                    }
                });
            } else {
                console.log("getLatest : Not connected, no messages");
            }

        };
        var postComment = function (target) {
            if (env.isConnected) {
                var texte = $("#" + target.id + " .newCommentText").val();
                //console.log(texte)
                $("#" + target.id + " .newCommentError").text("");
                if (!newPostVerification(texte)) {
                    $("#" + target.id + " .newCommentError").text("Un post ou commentaire doit avoir entre 1 et 255 charactères, ici : " + texte.length);
                    return;
                }
                var id = target.id.split("_")[1];
                console.log(id);
                var submitButton = $("#newCommentSubmit_" + id);
                submitButton.disabled = true;
                $.ajax({
                    method: "GET",
                    url: env.path+"/msg/addComment",
                    data: {"key": env.key, "text": texte, "post_id": id},
                    success: function (response) {
                        //console.log(response);
                        if (response.error === undefined) {
                            submitButton.disabled = false;
                            $("#" + target.id + " .newCommentText").val("");
                            refreshComments(id);
                        } else {
                            if (response.error.error_type === "Client error") {
                                alert(response.error.error_message);
                            } else {
                                alert("Something went wrong : " + response.error.error_message);
                            }
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        submitButton.disabled = false;
                        alert(response.error.error_message);
                    }
                });
            } else {
                console.log("MessageService : Not connected can't post msg");
            }
        };

        var UserMessages = function (login=env.login) {
            if (env.isConnected) {
                $.ajax({
                    type: 'GET',
                    url: env.path+"/msg/friend",
                    data: {"key": env.key, "login": login},
                    success: function (rep) {
                        console.log("Succesefully retrieved latest user messages");
                        if (rep.messages === undefined) {
                            console.log(rep);
                            mediator.publish(rep.error.error_message);
                            return;
                        }
                        env.messages.messages = [];
                        for (var i = 0; i < rep.messages.length; i++) {
                            var msg = rep.messages[i];
                            if (isMessage(msg)) {
                                env.messages.messages.push(msg);
                            } /*
                           else if (isComment(msg)) {
                                env.messages.comments.push(msg);
                            }*/
                        }
                        mediator.publish("MessageRefresh");
                    },
                    error: function (errXHR, testStatus, errorthrown) {
                        console.log(errXHR + testStatus + errorthrown);
                        alert(errXHR + testStatus + errorthrown);
                    }
                });
            } else {
                console.log("User Messages : Not connected, no messages");
            }
        };
        return {
            UserMessages : UserMessages,
            getLatest: getLatest,
            initNewMessage: initNewMessage,
            RenderTemplates: RenderTemplates
        }
    }
;
