package com.pojosontheweb.ttt;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.pojosontheweb.ttt.psi.TttTypes;
import com.intellij.psi.TokenType;

%%

%class TttIdeaLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

SIG_START="<%("
EXPR_START="<%="
SCRIPT_START="<%"
EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+
CHAR=.|{WHITE_SPACE}

%state SIGNATURE
SIG_END=")%>"
ID=[:jletter:] [:jletterdigit:]*
TYPE={ID} ("." {ID})*
ARG_SEP=","

%state EXPRESSION
EXPR_END="%>"

%state SCRIPT
SCRIPT_END="%>"
%%

<YYINITIAL> {
	{SIG_START}		{ yybegin(SIGNATURE); return TttTypes.SIG_START; }
	{EXPR_START}	{ yybegin(EXPRESSION); return TttTypes.EXPR_START; }
	{SCRIPT_START}	{ yybegin(SCRIPT); return TttTypes.SCRIPT_START; }
	{CHAR}			{ yybegin(YYINITIAL); return TttTypes.CHAR; }
}
<SIGNATURE> {
	{SIG_END} 		{ yybegin(YYINITIAL); return TttTypes.SIG_END; }
	{ID}			{ yybegin(SIGNATURE); return TttTypes.ID; }
	{TYPE}			{ yybegin(SIGNATURE); return TttTypes.TYPE; }
	{WHITE_SPACE}	{ yybegin(SIGNATURE); return com.intellij.psi.TokenType.WHITE_SPACE; }
	{ARG_SEP}		{ yybegin(SIGNATURE); return TttTypes.ARG_SEP; }
	[^] 			{ yybegin(SIGNATURE); return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
<EXPRESSION> {
	{EXPR_END} 		{ yybegin(YYINITIAL); return TttTypes.EXPR_END; }
	{CHAR}			{ yybegin(EXPRESSION); return TttTypes.CHAR; }
}
<SCRIPT> {
	{SCRIPT_END} 	{ yybegin(YYINITIAL); return TttTypes.SCRIPT_END; }
	{CHAR}			{ yybegin(SCRIPT); return TttTypes.CHAR; }
}