package com.pojosontheweb.ttt;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vankeisb on 25/07/15.
 */
class TttCompileOnSaveListener implements VirtualFileListener {

    private final Project project;

    public TttCompileOnSaveListener(Project project) {
        this.project = project;
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        onUpdatingEvent(event.getFile());
    }

    private void onUpdatingEvent(VirtualFile file) {
        if (file==null || file.getName().endsWith(".ttt")) {
            TttCompileAction.compileTemplate(this, project, file);
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent virtualFileEvent) {
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent virtualFileEvent) {
        onUpdatingEvent(virtualFileEvent.getFile());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {
        onUpdatingEvent(virtualFileMoveEvent.getFile());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent virtualFileCopyEvent) {
        onUpdatingEvent(virtualFileCopyEvent.getFile());
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent virtualFilePropertyEvent) {
    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent virtualFileEvent) {
    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent virtualFileEvent) {

    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent virtualFileMoveEvent) {

    }

}
