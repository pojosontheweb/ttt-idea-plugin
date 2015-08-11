package com.pojosontheweb.ttt;

import javax.swing.*;

public class TttConfigForm {
    private JPanel panel1;
    private JCheckBox compileOnSaveCheckBox;
    private JTextField genTttTextField;

    public JComponent getRoot() {
        return panel1;
    }

    public void getData(TttModuleComponent tttModuleComponent) {
        tttModuleComponent.setEnabled(isCompileOnSave());
        tttModuleComponent.setTargetPath(getTarget());
    }

    public void setData(TttModuleComponent tttModuleComponent) {
        compileOnSaveCheckBox.setSelected(tttModuleComponent.isEnabled());
        genTttTextField.setText(tttModuleComponent.getTargetPath());
    }

    public boolean isCompileOnSave() {
        return compileOnSaveCheckBox.isSelected();
    }

    public String getTarget() {
        return genTttTextField.getText();
    }
}
