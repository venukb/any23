CodeMirror.defineMode("ntriples", function() {
  untilSpace  = function(c) { return c != ' '; };
  untilEndURI = function(c) { return c != '>'; };
  return {
    startState: function() {
       return {inuri : false};
    },
    token: function(stream, state) {
      var ch = stream.next();
      if(ch == '<') {
         state.inuri=true;
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
         state.inuri=false; 
         stream.eatWhile(untilSpace);
         return 'uri';
      }
      if(ch == '_') {
          stream.eatWhile(untilSpace);
          return 'bnode';
      }
      if(ch == '"') {
          stream.eatWhile( function(c) { return c != '"' }  );
          stream.next();
          return 'literal';
      }
      if( ch == '@' ) {
          stream.eatWhile(untilSpace);
          return 'literal-lang';
      }
      if( ch == '.' ) {
          stream.skipToEnd();
      }
    }
  };
});

CodeMirror.defineMIME("text/n-triples", "ntriples");
