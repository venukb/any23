/* 
    pre_subject ->
        ( writing_subject_uri | writing_bnode_uri )
            -> pre_predicate 
                -> writing_predicate_uri 
                    -> pre_object 
                        -> writing_object_uri | writing_object_bnode | 
                          ( 
                            writing_object_literal 
                                -> writing_literal_lang | writing_literal_type
                          )
                            -> post_object
                                -> BEGIN
*/
Location = {
      PRE_SUBJECT         : 0,
      WRITING_SUB_URI     : 1,
      WRITING_BNODE_URI   : 2,
      PRE_PRED            : 3,
      WRITING_PRED_URI    : 4,
      PRE_OBJ             : 5,
      WRITING_OBJ_URI     : 6,
      WRITING_OBJ_BNODE   : 7,
      WRITING_OBJ_LITERAL : 8,
      WRITING_LIT_LANG    : 9,
      WRITING_LIT_TYPE    : 10,
      POST_OBJ            : 11,
      ERROR               : 12
};
function transitState(currState, c) {
    var currLocation = currState.location;
    var ret;
    
    // Openings.
    if     (currLocation == Location.PRE_SUBJECT && c == '<') ret = Location.WRITING_SUB_URI;
    else if(currLocation == Location.PRE_SUBJECT && c == '_') ret = Location.WRITING_BNODE_URI;
    else if(currLocation == Location.PRE_PRED && c == '<')    ret = Location.WRITING_PRED_URI;
    else if(currLocation == Location.PRE_OBJ  && c == '<')    ret = Location.WRITING_OBJ_URI;
    else if(currLocation == Location.PRE_OBJ  && c == '_')    ret = Location.WRITING_OBJ_BNODE;
    else if(currLocation == Location.PRE_OBJ  && c == '"')    ret = Location.WRITING_OBJ_LITERAL;
    
    // Closing.
    else if(currLocation == Location.WRITING_SUB_URI   && c == '>') ret = Location.PRE_PRED;
    else if(currLocation == Location.WRITING_BNODE_URI && c == ' ') ret = Location.PRE_PRED;
    else if(currLocation == Location.WRITING_PRED_URI  && c == '>') ret = Location.PRE_OBJ;
    else if(currLocation == Location.WRITING_OBJ_URI     && c == '>') ret = Location.POST_OBJ; // TODO: space could not pe specified.
    else if(currLocation == Location.WRITING_OBJ_BNODE   && c == ' ') ret = Location.POST_OBJ; // TODO: space could not pe specified.
    else if(currLocation == Location.WRITING_OBJ_LITERAL && c == '"') ret = Location.POST_OBJ; // TODO: space could not pe specified.
    
    else if(currLocation == Location.WRITING_OBJ_LITERAL && c == '@') ret = Location.WRITING_LIT_LANG;
    else if(currLocation == Location.WRITING_OBJ_LITERAL && c == '^') ret = Location.WRITING_LIT_TYPE;
    else if(currLocation == Location.WRITING_LIT_LANG || currLocation == Location.WRITING_LIT_TYPE && c == ' ') ret = Location.POST_OBJ;
    
    // Reset.
    else if(currLocation == Location.POST_OBJ && c == '.') ret = Location.PRE_SUBJECT;    
    
    // Error
    else ret = Location.ERROR;
    
    console.log('char ' + c + ' curr location: ' + currLocation + ' next location: ' + ret);
    currState.location=ret;
}
CodeMirror.defineMode("ntriples", function() {  
  untilSpace  = function(c) { return c != ' '; };
  untilEndURI = function(c) { return c != '>'; };
  return {
    startState: function() {
       return { location : Location.PRE_SUBJECT, inside : false };
    },
    token: function(stream, state) {
      var ch = stream.next();
      if(ch == '<') {
         transitState(state, ch);
         stream.eatWhile( function(c) { return c != '#' && c != '>'; } );
         if( stream.match('#', false) ) return 'uri';
         stream.eatWhile(untilEndURI);
         stream.next();
         transitState(state, '>');
         return 'uri';
      }
      if(ch == '#') {
        stream.eatWhile(untilEndURI);
        stream.next();
        transitState(state, '>');
        return 'uri-anchor';
      }
      if(ch == '_') {
          transitState(state, ch);
          stream.eatWhile(untilSpace);
          stream.next();
          transitState(state, ' ');
          return 'bnode';
      }
      if(ch == '"') {
          transitState(state, ch);
          stream.eatWhile( function(c) { return c != '"'; } );
          stream.next();
          if( stream.peek() != '@' && stream.peek() != '^' ) {
              transitState(state, '"');
          }
          return 'literal';
      }
      if( ch == '@' ) {
          stream.next();
          transitState(state, '@');
          stream.eatWhile(untilSpace);
          stream.next();
          transitState(state, ' ');
          return 'literal-lang';
      }
      if( ch == '^' ) {
          stream.next();
          transitState(state, '^');
          stream.eatWhile(untilSpace);
          stream.next();
          transitState(state, ' ');
          return 'literal-type';
      }
      if( ch == '.' ) {
          transitState(state, ch);
      }
    }
  };
});

CodeMirror.defineMIME("text/n-triples", "ntriples");
