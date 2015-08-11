package com.pojosontheweb.ttt;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.testFramework.LightPlatformTestCase;
import com.intellij.testFramework.UsefulTestCase;
import com.pojosontheweb.ttt.psi.TttTypes;

import java.io.StringReader;

public class LexerTest extends UsefulTestCase {

    public void testError() {
        doTest(
            "<%((((",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "BAD_CHARACTER", "(",
                "BAD_CHARACTER", "(",
                "BAD_CHARACTER", "("
            });
    }

    public void testEmptySignature() {
        doTest(
            "<%()%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testEmptySignatureWs() {
        doTest(
            "<%(  )%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "WHITE_SPACE", "  ",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testOneArg() {
        doTest(
            "<%(java.lang.String foo)%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testOneArgWs() {
        doTest(
            "<%( java.lang.String foo )%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "WHITE_SPACE", " ",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "WHITE_SPACE", " ",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testOneSimpleType() {
        doTest(
            "<%( int foo )%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "WHITE_SPACE", " ",
                "TttToken:ID", "int",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "WHITE_SPACE", " ",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testTwoArgs() {
        doTest(
            "<%(java.lang.String foo, int bar)%>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:ARG_SEP", ",",
                "WHITE_SPACE", " ",
                "TttToken:ID", "int",
                "WHITE_SPACE", " ",
                "TttToken:ID", "bar",
                "TttToken:SIG_END", ")%>"
            });
    }

    public void testSigAndExpr() {
        doTest(
            "<%(java.lang.String foo)%><%= foo %>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:SIG_END", ")%>",
                "TttToken:EXPR_START", "<%=",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "f",
                "TttToken:CHAR", "o",
                "TttToken:CHAR", "o",
                "TttToken:CHAR", " ",
                "TttToken:EXPR_END", "%>"
            });
    }

    public void testSigAndText() {
        doTest(
            "<%(java.lang.String foo)%>\nbar",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:SIG_END", ")%>",
                "TttToken:CHAR", "\n",
                "TttToken:CHAR", "b",
                "TttToken:CHAR", "a",
                "TttToken:CHAR", "r",
            });
    }

    public void testSigAndScript() {
        doTest(
            "<%(java.lang.String foo)%><% script %>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:SIG_END", ")%>",
                "TttToken:SCRIPT_START", "<%",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "s",
                "TttToken:CHAR", "c",
                "TttToken:CHAR", "r",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "p",
                "TttToken:CHAR", "t",
                "TttToken:CHAR", " ",
                "TttToken:SCRIPT_END", "%>"
            });
    }

    public void testComplex() {
        doTest(
            "<%(java.lang.String foo, int bar)%>\n" +
            "Hello, <%=foo%> !\n" +
            "<% for (int i=0; i<bar; i++) { %>\n" +
            "\ti=<%=i%>\n" +
            "<% } %>",
            new String[]{
                "TttToken:SIG_START", "<%(",
                "TttToken:TYPE", "java.lang.String",
                "WHITE_SPACE", " ",
                "TttToken:ID", "foo",
                "TttToken:ARG_SEP", ",",
                "WHITE_SPACE", " ",
                "TttToken:ID", "int",
                "WHITE_SPACE", " ",
                "TttToken:ID", "bar",
                "TttToken:SIG_END", ")%>",
                "TttToken:CHAR", "\n",
                "TttToken:CHAR", "H",
                "TttToken:CHAR", "e",
                "TttToken:CHAR", "l",
                "TttToken:CHAR", "l",
                "TttToken:CHAR", "o",
                "TttToken:CHAR", ",",
                "TttToken:CHAR", " ",
                "TttToken:EXPR_START", "<%=",
                "TttToken:CHAR", "f",
                "TttToken:CHAR", "o",
                "TttToken:CHAR", "o",
                "TttToken:EXPR_END", "%>",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "!",
                "TttToken:CHAR", "\n",
                "TttToken:SCRIPT_START", "<%",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "f",
                "TttToken:CHAR", "o",
                "TttToken:CHAR", "r",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "(",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "n",
                "TttToken:CHAR", "t",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "=",
                "TttToken:CHAR", "0",
                "TttToken:CHAR", ";",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "<",
                "TttToken:CHAR", "b",
                "TttToken:CHAR", "a",
                "TttToken:CHAR", "r",
                "TttToken:CHAR", ";",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "+",
                "TttToken:CHAR", "+",
                "TttToken:CHAR", ")",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "{",
                "TttToken:CHAR", " ",
                "TttToken:SCRIPT_END", "%>",
                "TttToken:CHAR", "\n\t",
                "TttToken:CHAR", "i",
                "TttToken:CHAR", "=",
                "TttToken:EXPR_START", "<%=",
                "TttToken:CHAR", "i",
                "TttToken:EXPR_END", "%>",
                "TttToken:CHAR", "\n",
                "TttToken:SCRIPT_START", "<%",
                "TttToken:CHAR", " ",
                "TttToken:CHAR", "}",
                "TttToken:CHAR", " ",
                "TttToken:SCRIPT_END", "%>"
            });
    }

    private static Lexer newLexer(String text) {
        Lexer lexer = new FlexAdapter(new TttIdeaLexer(new StringReader(text)));
        lexer.start(text);
        return lexer;
    }

    private static void doTest(String text, String[] expectedTokens) {
        System.out.println("Text :");
        System.out.println(text);
        Lexer lexer = newLexer(text);
        int idx = 0;
        System.out.println("Tokens :");
        while (lexer.getTokenType() != null) {
            String tokenTxt = lexer.getTokenText();
            String tokenType = lexer.getTokenType().toString();
            System.out.println("\"" + tokenType + "\", \"" + tokenTxt + "\",");
            lexer.advance();
            idx++;
        }
        System.out.println("nbTokens=" + idx + "\n");
        idx = 0;
        lexer = newLexer(text);
        while (lexer.getTokenType() != null) {
            if (idx >= expectedTokens.length) fail("Too many tokens");
            String tokenName = lexer.getTokenType().toString();
            String expectedTokenType = expectedTokens[idx++];
            String expectedTokenText = expectedTokens[idx++];
            assertEquals(expectedTokenType, tokenName);
            String tokenText = lexer.getBufferSequence().subSequence(lexer.getTokenStart(), lexer.getTokenEnd()).toString();
            assertEquals(expectedTokenText, tokenText);
            lexer.advance();
        }

        if (idx < expectedTokens.length) fail("Not enough tokens");
    }

}
