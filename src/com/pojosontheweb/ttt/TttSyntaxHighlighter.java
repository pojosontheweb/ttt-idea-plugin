package com.pojosontheweb.ttt;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.pojosontheweb.ttt.psi.TttTypes;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class TttSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD = createTextAttributesKey("TTT_KEYWORD", SyntaxHighlighterColors.KEYWORD);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new TttIdeaLexer((Reader) null));
    }

    private static final List<IElementType> KW_TYPES = Arrays.asList(
        TttTypes.SIG_START,
        TttTypes.SIG_END,
        TttTypes.EXPR_START,
        TttTypes.EXPR_END,
        TttTypes.SCRIPT_START,
        TttTypes.SCRIPT_END
    );

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (KW_TYPES.contains(tokenType)) {
            return KEYWORD_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
