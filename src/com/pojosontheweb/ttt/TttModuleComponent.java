package com.pojosontheweb.ttt;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationsConfiguration;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@State(
    name = TttModuleComponent.COMPONENT_NAME,
    storages = {
        @Storage(id = "ttt", file = "$MODULE_FILE$")}
)
public class TttModuleComponent implements ModuleComponent, Configurable, PersistentStateComponent<TttModuleComponent> {

    public static final String COMPONENT_NAME = "TttModuleComponent";
    public static final String TTT_GROUP = "TttGroup";

    private boolean enabled;
    private String targetPath;
    private TttConfigForm form;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void disposeComponent() {
        // nothing to do
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ttt";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public void projectOpened() {
        // nothing to do
    }

    @Override
    public void projectClosed() {
        // nothing to do
    }

    @Override
    public void moduleAdded() {
        // nothing to do
    }


    @Override
    public void initComponent() {
        NotificationsConfiguration
            .getNotificationsConfiguration()
            .register(TTT_GROUP, NotificationDisplayType.STICKY_BALLOON);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Nullable
    @Override
    public TttModuleComponent getState() {
        return this;
    }

    @Override
    public void loadState(TttModuleComponent tttModuleComponent) {
        this.enabled = tttModuleComponent.enabled;
        this.targetPath = tttModuleComponent.targetPath;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (form == null) {
            form = new TttConfigForm();
        }
        return form.getRoot();
    }

    @Override
    public boolean isModified() {
        return form.isCompileOnSave()!=enabled || !form.getTarget().equals(targetPath);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (form != null) {
            form.getData(this);
        }
    }

    @Override
    public void reset() {
        if (form != null) {
            form.setData(this);
        }
    }

    @Override
    public void disposeUIResources() {
        // nothing to do
    }

}
