package com.pojosontheweb.ttt;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.pojosontheweb.ttt.psi.TttArg;
import com.pojosontheweb.ttt.psi.TttFile;
import com.pojosontheweb.ttt.psi.TttPart;
import com.pojosontheweb.ttt.psi.TttSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TttStructureViewElement implements StructureViewTreeElement, SortableTreeElement, ItemPresentation {
    private PsiElement element;

    public TttStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
            ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
            ((NavigationItem) element).canNavigateToSource();
    }

    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

//    @Override
//    public TreeElement[] getChildren() {
//        if (element instanceof TttFile) {
//            TttProperty[] properties = PsiTreeUtil.getChildrenOfType(element, TttProperty.class);
//            List<TreeElement> treeElements = new ArrayList<TreeElement>(properties.length);
//            for (SimpleProperty property : properties) {
//                treeElements.add(new SimpleStructureViewElement(property));
//            }
//            return treeElements.toArray(new TreeElement[treeElements.size()]);
//        } else {
//            return EMPTY_ARRAY;
//        }
//    }


    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof TttFile) {
            List<TreeElement> res = new ArrayList<TreeElement>();
            PsiElement sig = PsiTreeUtil.findChildOfType(element, TttSignature.class);
            if (sig != null) {
                res.add(new TttStructureViewElement(sig));
            }
            Collection<TttPart> parts = PsiTreeUtil.findChildrenOfType(element, TttPart.class);
            for (TttPart p : parts) {
                res.add(new TttStructureViewElement(p));
            }
            TreeElement[] r = new TreeElement[res.size()];
            return res.toArray(r);
        } else if (element instanceof TttSignature) {
            Collection<TttArg> args = PsiTreeUtil.findChildrenOfType(element, TttArg.class);
            List<TreeElement> res = new ArrayList<TreeElement>(args.size());
            for (TttArg arg : args) {
                res.add(new TttStructureViewElement(arg));
            }
            TreeElement[] r = new TreeElement[res.size()];
            return res.toArray(r);
        }
        return EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        if (element instanceof TttFile) {
            TttFile f = (TttFile) element;
            return f.getName();
        } else if (element instanceof TttSignature) {
            return "Arguments";
        } else if (element instanceof TttArg) {
            TttArg arg = (TttArg)element;
            return arg.getArgName().getText() + " : " + arg.getArgType().getText();
        } else if (element instanceof TttPart) {
            TttPart p = (TttPart)element;
            if (p.getExpression()!=null) {
                return "<%= ... %>";
            } else if (p.getScript()!=null) {
                return "<% ... %>";
            } else {
                return "...";
            }
        }
        return element.toString();
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean b) {
        return TttIcons.FILE;
    }
}