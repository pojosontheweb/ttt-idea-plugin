package com.pojosontheweb.ttt.psi;

import com.intellij.psi.tree.IElementType;
import com.pojosontheweb.ttt.TttLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TttElementType extends IElementType {
    public TttElementType(@NotNull @NonNls String debugName) {
        super(debugName, TttLanguage.INSTANCE);
    }
}