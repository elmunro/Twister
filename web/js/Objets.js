/**
 * 
 */


Commentaire.prototype.getHtml = function{
	var s = "<div id=\"commentaire_{{id}}\" class=\"commentaire\">\n\" +
			"<div class=\"text_commentaire\"> {{texte}}" +
			"</div>\n\ "+ 
			"<div class=\"infos_commentaire\">\n\" +
			"<span> Post par <span class=\"link\" onclick=\"pageUser({{auteur.id}},{{auteur.login}})\"> </span>"+
			"<span> Date <span class=\"date\"> {{date}} </span>
			"</div>"
	return s;
}

function Message(id,auteur,texte,date,commentaire){
    this.id = id;
    this.auteur = auteur;
    this.texte = texte;
    this.date = date;
	if(commentaire == undefined){
        this.commentaire = [];
    }
    this.commentaire = commentaire;
}

function Commentaire(id,auteur,texte,date){
	this.id = id;
	this.auteur = auteur;
	this.texte = texte;
	this.date = date;
}


function revival(key,value){
	if(value.comments != undefined){
		var c = new Message(value.id,value.auteur,value.texte,value.date);
		return c;
	}
	if(value.text != undefined){
		var c = new Commentaire(value.id, value.auteur, value.texte, value.date);
		return c;
	}
	if(key == "date"){
		var d = new Date(date);
		return d;
	}
	return value;
}