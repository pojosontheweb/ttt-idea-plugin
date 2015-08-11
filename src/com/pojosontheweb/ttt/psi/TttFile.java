package com.pojosontheweb.ttt.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.pojosontheweb.ttt.TttFileType;
import com.pojosontheweb.ttt.TttLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TttFile extends PsiFileBase {
    public TttFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, TttLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return TttFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Ttt File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
