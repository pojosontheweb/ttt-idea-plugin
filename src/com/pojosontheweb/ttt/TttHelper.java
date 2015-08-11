package com.pojosontheweb.ttt;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.Function;

public class TttHelper {

    public static boolean isTttEnabled(Project project, VirtualFile vfile) {
        // find JAVA module for vfile
        Module module = ModuleUtil.findModuleForFile(vfile, project);
        if (module == null) {
            return false;
        }

        ModuleType moduleType = ModuleType.get(module);
        if (moduleType != StdModuleTypes.JAVA) {
            return false;
        }

        // check if plugin is active for this module
        TttModuleComponent moduleComponent = module.getComponent(TttModuleComponent.class);
        if (moduleComponent == null) {
            return false;
        }
        if (!moduleComponent.isEnabled()) {
            return false;
        }

        return true;
    }

    public static <Param extends VirtualFile,Result> void handleFile(Project project, VirtualFile vfile, Function<Param,Result> f) {
//        Project[] allProjects = ProjectManager.getInstance().getOpenProjects();
//        if (isTttEnabled(project, vfile)) {
//            f.fun(vfile)
//        }
//
//            if (message!=null) {
//                final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
//                if (statusBar != null) {
//                    statusBar.setInfo(message);
//                }
//            }
//        }
    }


}
