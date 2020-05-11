function wordMap() {
    //to lower
    var lower = this.text.toLowerCase();
    //get rid of symbols
    lower = lower.replace(/[^A-Z0-9 ]/ig, "");
    var words = lower.match(/\w+/g);
    if (words === null) {
        return;
    }
    for (var i = 0; i < words.length; i++) {
        //emit with count of one, and source id
        emit(words[i], { count : 1 , source : this._id});
    }
}

function wordReduce(key, values) {
    var total = 0;
    var sources = {};
    for (var i = 0; i < values.length; i++) {
        total += values[i].count;
        if(sources[values[i].source] === undefined){
            sources[values[i].source] = 1;
        }else{
            sources[values[i].source]+=1;
        }
    }
    return { count : total, 'sources' : sources };
}

function scoreMap() {
    //to lower
    var lower = this.text.toLowerCase();
    //get rid of symbols
    lower = lower.replace(/[^A-Z0-9 ]/ig, "");
    var words = lower.match(/\w+/g);
    if (words === null) {
        return;
    }
    for (var i = 0; i < words.length; i++) {
        emit(words[i], { pos : i , source : this._id});
    }
}

function scoreReduce(key, values) {
    var total = 0;
    var sources = {};
    for (var i = 0; i < values.length; i++) {
        total += values[i].count;
        if(sources[values[i].source] === undefined){
            sources[values[i].source] = 1;
        }else{
            sources[values[i].source]+=1;
        }
    }
    return { count : total, 'sources' : sources };
}