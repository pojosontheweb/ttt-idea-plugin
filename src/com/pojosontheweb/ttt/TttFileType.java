package com.pojosontheweb.ttt;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TttFileType extends LanguageFileType {
    public static final TttFileType INSTANCE = new TttFileType();

    private TttFileType() {
        super(TttLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Ttt file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ttt language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ttt";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return TttIcons.FILE;
    }
}