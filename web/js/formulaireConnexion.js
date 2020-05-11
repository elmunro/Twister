function connexion(formulaire){
	var login = formulaire.login.value;
	var pass = formulaire.password.value;
	var ok = verif_formulaire_connexion(login,pass);
	if(ok){
		connecte(login,pass);
	}
}

function verif_formulaire_connexion(login, pass){
	if(login.length == 0){
		func_erreur("Login obligatoire");
		return false;
	}
	if(pass.length == 0){
		func_erreur("Mot de passe obligatoire");
		return false;
	}
	//A modifier selon taille max du login et du mdp
	if(login.length > 20){
		func_erreur("Taille max login = 20");
		return false;
	}
	if(pass.length > 20){
		func_erreur("Taille max login = 20");
		return false;
	}
	return true
}

function func_erreur(msg){
	var msg_box = "<div id=\"msg_err_connexion>" + msg + "</div>";
	var old_msg = $("#msg_err_connexion");
	if(olod.msg.length == 0){
		$("form").prepend(msg_box);
	}else{
		old_msg.replaceWith(msg_box);
	}
	$("#msg_err_connexion").css({clor:"red", margin_top:"20px", margin_bottom:"30px", margin:"40px", word_wrap:"break_word"});
}

function connecte(login,pass){
	console.log("connection" + login + "," + pass);
	var idUser = 78;
	var key = "567898465";
	if(!noConnexion){
	
	}else{
		reponseConnexion({"key":key, "id":idUser, "login":login, "follows":[2]});
	}
}

function reponseConnexion(rep){
	if(rep.erreur == undefined){
		env.key = rep.key;
		env.id = rep.id;
		env.login = rep.login;
		env.follows = new Set();
		for(var i=0; i<rep.follows.size; i++){
			env.follows.add(rep.follows[i]);
		}
	}
	if(noConnection){
		follows[rep.id] == new Set();
		for(var i=0; i<rep.follows.size; i++){
			follows[rep.id].add(rep.follows[i]);
		}
		makeMainPanel();
	}else{
		func_erreur(rep.erreur)
	}
}



























// A faire
function makeConnectionPanel(){
	var s = "<body>";
}	
	