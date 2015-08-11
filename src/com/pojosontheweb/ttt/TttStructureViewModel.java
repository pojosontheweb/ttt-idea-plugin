package com.pojosontheweb.ttt;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.pojosontheweb.ttt.psi.TttFile;
import org.jetbrains.annotations.NotNull;

public class TttStructureViewModel extends StructureViewModelBase implements
    StructureViewModel.ElementInfoProvider {
    public TttStructureViewModel(PsiFile psiFile) {
        super(psiFile, new TttStructureViewElement(psiFile));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[] {Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof TttFile;
    }
}
