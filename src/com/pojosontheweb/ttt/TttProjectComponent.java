package com.pojosontheweb.ttt;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

public class TttProjectComponent extends AbstractProjectComponent {

    private final Logger LOG = Logger.getInstance(TttProjectComponent.class.getName());

    private final TttCompileOnSaveListener listener;

    public TttProjectComponent(@NotNull Project project) {
        super(project);
        listener = new TttCompileOnSaveListener(project);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TttProjectComponent";
    }

    @Override
    public void projectOpened() {
        LOG.info("Registering file listener");
        VirtualFileManager.getInstance().addVirtualFileListener(listener);
    }

    @Override
    public void projectClosed() {
        LOG.info("Removing file listener");
        VirtualFileManager.getInstance().removeVirtualFileListener(listener);
    }

}
