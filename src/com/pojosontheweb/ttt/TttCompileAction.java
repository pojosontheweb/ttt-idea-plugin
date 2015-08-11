package com.pojosontheweb.ttt;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TttCompileAction extends AnAction {

    private final static Logger LOG = Logger.getInstance(TttProjectComponent.class.getName());

    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TttCompileAction() {
        // Set the menu item name.
        super("Compile _TTT", "Compile TTT templates", TttIcons.FILE);
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent event) {
        // compile all ttt files in project
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if (project!=null) {
            LOG.info("Compiling all templates in project " + project.getName());
            compileTemplates(this, project);
        } else {
            LOG.info("No project available, nothing done.");
        }
    }

    public static void compileTemplates(Object requestor, Project project) {
        compile(requestor, project, null);
    }

    public static void compileTemplate(Object requestor, Project project, VirtualFile templateFile) {
        compile(requestor, project, templateFile);
    }

    private static void compile(Object requestor, Project project, VirtualFile templateFile) {
        if (project!=null) {
            final long start = System.currentTimeMillis();
            TttConsoleLogger consoleLogger = project.getComponent(TttConsoleLogger.class);
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module m : modules) {
                List<TttCompilationResult.TttTemplateResult> results = new ArrayList<>();

                TttModuleComponent tttModuleComponent = m.getComponent(TttModuleComponent.class);
                LOG.info("Compiling templates in module " + m.getName());
                if (tttModuleComponent != null && tttModuleComponent.isEnabled()) {
                    // get target gen path
                    String target = tttModuleComponent.getTargetPath();
                    if (target == null) {
                        target = "ttt-gen";
                    }
                    String fullTarget = project.getBasePath() + File.separator + target;
                    LOG.info("Full target " + fullTarget);
                    // rm previously generated code and collect
                    // source roots to be visited
                    VirtualFile[] srcRoots = ModuleRootManager.getInstance(m).getSourceRoots();
                    List<VirtualFile> rootsToVisit = new ArrayList<VirtualFile>();
                    VirtualFile targetDir = null;
                    VirtualFile templateSourceRoot = null;
                    for (VirtualFile srcRoot : srcRoots) {
                        if (srcRoot.getPath().equals(fullTarget)) {
                            if (templateFile == null) {
                                // remove all files in target
                                for (VirtualFile child : srcRoot.getChildren()) {
                                    FileUtil.delete(new File(child.getPath()));
                                }
                            }
                            targetDir = srcRoot;
                        } else {
                            if (templateFile == null) {
                                // collect this src root for later...
                                rootsToVisit.add(srcRoot);
                            } else if (templateSourceRoot == null) {
                                // check if the template comes from
                                // this source root, and store it
                                if (isFileInDir(templateFile, srcRoot)) {
                                    templateSourceRoot = srcRoot;
                                }
                            }
                        }
                    }

                    final VirtualFile td = targetDir;
                    Application app = ApplicationManager.getApplication();

                    if (td == null) {
                        Notifications.Bus.notify(
                            new Notification(
                                TttModuleComponent.TTT_GROUP,
                                "Unable to compile TTT templates",
                                "No target dir found at " + fullTarget + ".\n"
                                + "Please create the folder and mark it as a \n"
                                + "generated source dir.",
                                    NotificationType.ERROR)
                        );

                    } else {

                        if (templateFile == null) {
                            // no tpl file supplied, compile all templates...
                            // visit the source roots, find all .ttt files in
                            // there, and compile'em
                            for (VirtualFile srcRoot : rootsToVisit) {
                                final String srcRootPath = srcRoot.getPath();
                                // find all ".ttt" files under this...
                                VfsUtilCore.visitChildrenRecursively(srcRoot, new VirtualFileVisitor() {
                                    @Override
                                    public boolean visitFile(@NotNull VirtualFile file) {
                                        if (file.getName().endsWith(".ttt")) {
                                            try {
                                                Reader in = new InputStreamReader(file.getInputStream());
                                                String targetDir = td.getPath();
                                                String relPath = file.getPath().substring(srcRootPath.length());
                                                String absPath = targetDir + relPath;
                                                absPath = absPath.replace(".ttt", ".java");
                                                File f = new File(absPath);
                                                File parent = f.getParentFile();
                                                if (!parent.exists()) {
                                                    parent.mkdirs();
                                                }
                                                Writer out = new FileWriter(f);
                                                String fqn = relPath
                                                    .substring(1)
                                                    .replace(File.separatorChar, '.')
                                                    .replace(".ttt", "");
                                                try {
                                                    List<TttCompileError> compileErrors = TttCompiler.compile(in, out, fqn);
                                                    results.add(
                                                        new TttCompilationResult.TttTemplateResult(
                                                            file.getPath(),
                                                            f.getAbsolutePath(),
                                                            compileErrors
                                                        )
                                                    );
                                                } finally {
                                                    out.close();
                                                }
                                            } catch (Exception e) {
                                                // TODO handle error
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        return true;
                                    }
                                });
                            }

                            long elapsed = System.currentTimeMillis() - start;
                            app.invokeLater(() -> td.refresh(true, true, () -> {
                                TttCompilationResult res = new TttCompilationResult(results);
                                res.setElapsed(elapsed);
                                consoleLogger.log(res);
                                res.toLines().forEach(LOG::info);
                                String msg = "TTT template(s) generated ";
                                if (res.hasErrors()) {
                                    msg += "with errors ";
                                }
                                msg += "to " + td.getPath();
                                final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                                if (statusBar != null) {
                                    statusBar.setInfo(msg);
                                }
                            }));

                        } else if (templateSourceRoot != null) {

                            // template file provided, only compile this one
                            try {
                                Reader in = new InputStreamReader(templateFile.getInputStream());
                                String relPath = templateFile.getPath().substring(templateSourceRoot.getPath().length());
                                String absPath = targetDir.getPath() + relPath;
                                absPath = absPath.replace(".ttt", ".java");
                                File f = new File(absPath);
                                File parent = f.getParentFile();
                                if (!parent.exists()) {
                                    parent.mkdirs();
                                }
                                String fqn = relPath
                                    .substring(1)
                                    .replace(File.separatorChar, '.')
                                    .replace(".ttt", "");

                                OutputStream os;
                                // check to see if we have a gen file already...
                                VirtualFile existingTargetFile = td.findFileByRelativePath(relPath.replace(".ttt", ".java"));
                                if (existingTargetFile != null) {
                                    os = existingTargetFile.getOutputStream(requestor);
                                } else {
                                    os = new FileOutputStream(f);
                                }

                                try (Writer out = new OutputStreamWriter(os)) {
                                    List<TttCompileError> compileErrors = TttCompiler.compile(in, out, fqn);
                                    results.add(
                                        new TttCompilationResult.TttTemplateResult(
                                            templateFile.getPath(),
                                            absPath,
                                            compileErrors
                                        )
                                    );
                                }

                                long elapsed = System.currentTimeMillis() - start;
                                app.invokeLater(() -> td.refresh(true, true, () -> {
                                    TttCompilationResult res = new TttCompilationResult(results);
                                    res.setElapsed(elapsed);
                                    res.toLines().forEach(LOG::info);
                                    consoleLogger.log(res);
                                    String msg = "TTT template(s) generated ";
                                    if (res.hasErrors()) {
                                        msg += "with errors ";
                                    }
                                    msg += "to " + td.getPath();
                                    final StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                                    if (statusBar != null) {
                                        statusBar.setInfo(msg);
                                    }
                                }));

                            } catch (Exception e) {
                                // TODO handle error
                                throw new RuntimeException(e);
                            }

                        }

                    }

                }
            }
        }
    }


    private static boolean isFileInDir(@NotNull VirtualFile file, @NotNull VirtualFile parent) {
        VirtualFile fp = file.getParent();
        if (fp == null) {
            return false;
        } else {
            return fp.equals(parent) || isFileInDir(fp, parent);
        }
    }

}