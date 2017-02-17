grammar LexerParser;

options {
	language = Java;
}

/*Parser rule */
r
:	formula EOF
;

formula
:	LPAREN formula op formula RPAREN #nested
|	LPAREN NOT formula RPAREN #negation
|	predicate #pred
;

op
:	(AND | OR | IMPL)
;

predicate
:   CONSTANT LPAREN term (',' term)* RPAREN
;

term
:   CONSTANT | VARIABLE
;

/* Lexer rules */
LPAREN: '(';
RPAREN: ')';
NOT:'~';
AND: '&';
OR: '|';
IMPL: '=>';

VARIABLE:  [a-z] ;

CONSTANT: [A-Z] CHARACTER* ;

//PREDICATE: [A-Z] CHARACTER* ;

CHARACTER: [a-zA-Z] ;

WS : [ \t\r\n]+ -> skip  ;