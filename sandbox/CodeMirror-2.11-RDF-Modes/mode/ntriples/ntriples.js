Location = {
      BEGIN     : 0,
      SUB_URI   : 1,
      SUB_BNODE : 2,
      SUB_PRE   : 3,
      PRED      : 4,
      PRED_OBJ  : 5,
      OBJ_URI   : 6,
      OBJ_BNODE : 7,
      OBJ_LIT   : 8,
      POST_OBJ  : 9,
      ERROR     : 10
};
function transitState(currState, c) {
    var currLocation = currState.location;
    var ret;
    
    if( currLocation == Location.SUB_URI       && c == '>') ret = Location.SUB_PRE;
    else if(currLocation == Location.SUB_BNODE && c == ' ') ret = Location.SUB_PRE;
    else if(currLocation == Location.PRED      && c == '>') ret = Location.PRED_OBJ;
    else if(currLocation == Location.OBJ_URI   && c == '>') ret = Location.POST_OBJ;
    else if(currLocation == Location.OBJ_BNODE && c == ' ') ret = Location.POST_OBJ;
    else if(currLocation == Location.OBJ_LIT   && c == '"') ret = Location.POST_OBJ;    
    
    else if(currLocation == Location.BEGIN && c == '<') ret = Location.SUB_URI;
    else if(currLocation == Location.BEGIN && c == '_') ret = Location.SUB_BNODE;
    else if(currLocation == Location.SUB_PRE          ) ret = Location.PRED;
    else if(currLocation == Location.PRED_OBJ && c == '<') ret = Location.OBJ_URI;
    else if(currLocation == Location.PRED_OBJ && c == '_') ret = Location.OBJ_BNODE;
    else if(currLocation == Location.PRED_OBJ && c == '"') ret = Location.OBJ_LIT;
    else if(
        ( currLocation == Location.OBJ_URI || currLocation == Location.OBJ_LIT || currLocation == Location.OBJ_BNODE )
        &&
        c == '.'
    ) ret = Location.BEGIN;    
    else ret = Location.ERROR;
    
    console.log('next location: ' + ret + ' curr location: ' + currLocation);
    currState.location=ret;
}
CodeMirror.defineMode("ntriples", function() {  
  untilSpace  = function(c) { return c != ' '; };
  untilEndURI = function(c) { return c != '>'; };
  return {
    startState: function() {
       return { location : Location.BEGIN, inside : false };
    },
    token: function(stream, state) {
      var ch = stream.next();
      if(ch == '<') {
         transitState(state, ch);
         stream.eatWhile( function(c) { return c != '#' && c != '>'; } );
         if( stream.match('#', false) ) return 'uri';
         stream.eatWhile(untilEndURI);
         stream.next();
         return 'uri';
      }
      if(ch == '#') {
        stream.eatWhile(untilEndURI);
        return 'uri-anchor';
      }
      if(ch == '>') {
         transitState(state, ch);  
         stream.eatWhile(untilSpace);
         return 'uri';
      }
      if(ch == '_') {
          transitState(state, ch);
          stream.eatWhile(untilSpace);
          transitState(state, ' ');
          return 'bnode';
      }
      if(ch == '"') {
          transitState(state, ch);
          stream.eatWhile( function(c) { return c != '"' }  );
          transitState(state, '"');
          stream.next();
          return 'literal';
      }
      if( ch == '@' ) {
          stream.eatWhile(untilSpace);
          transitState(state, ' ');
          return 'literal-lang';
      }
      if( ch == '.' ) {
          transitState(state, ch);
          stream.skipToEnd();
      }
    }
  };
});

CodeMirror.defineMIME("text/n-triples", "ntriples");
