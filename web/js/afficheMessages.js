//A finir

function developpeMessage(id){
	var m = env.msgs[id];
	var el = $("#message_"+id+".comments");
	for(var i=0; i<m.comments.length; i++){
		var c = m.comments[i];
		console.log(id,c);
		el.append(c.getHtml());
	}
	el = $("#message_"+id+".new_comment");
	el.append("<form name=\"new_comment.form\" id=\"new.comment_form\" onSubmit=\"func_new_comment("+id+");return false;\">" +
			"<textArea id=\"new_"+id+"\" >" +
			"<input type=\"submit\" value=\"Publier\" id=\"new_button\">" +
			"</form>");
	$("#message_"+id+"img").replaceWith("<img src = \"\" onClick=\"replieMessages("id")\">";
}

function replieMessage(id){

}

function newComment(id){
	var text = $("#new_"+id).val();
	if(!noConnexion){
		
	}else{
		newCommentReponse(id,JSON.stringify(new Commentaire(env.msgs[id].comments.length+1, {"id":env.id, "login":env.login}, text, new Date())));
	}
}

function newCommentReponse(id, rep){
	var com = JSON.parse(rep,revival);
	if(com != undefined && com.erreur == undefined){
		el = $("#message_"+id+".comments")
		el.append(com.getHtml());
		env.msgs[id].comments.push(com);
		if(noConnexion){
			localDb = 
		}else{
			
		}
	}
}