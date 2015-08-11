package com.pojosontheweb.ttt;

import com.intellij.lang.Language;

/**
 * Created by vankeisb on 23/07/15.
 */
public class TttLanguage extends Language {

    public static final TttLanguage INSTANCE = new TttLanguage();

    private TttLanguage() {
        super("Ttt");
    }
}
