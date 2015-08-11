package com.pojosontheweb.ttt;

import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

public class TttConsoleLogger extends AbstractProjectComponent {

    private ConsoleView consoleView;
    private TttCompilationResult lastResult;

    public TttConsoleLogger(Project project) {
        super(project);
    }

    public void setConsoleView(ConsoleView consoleView) {
        this.consoleView = consoleView;
        if (lastResult != null) {
            log(lastResult);
        }
    }

    public void log(TttCompilationResult compilationResult) {
        if (consoleView == null) {
            lastResult = compilationResult;
        } else {
            consoleView.clear();
            if (compilationResult.hasErrors()) {
                consoleView.print("Compilation failed :\n", ConsoleViewContentType.ERROR_OUTPUT);
            } else {
                consoleView.print("Compilation succeeded :\n", ConsoleViewContentType.NORMAL_OUTPUT);
            }
            int nbOk = 0, nbKo = 0;
            for (TttCompilationResult.TttTemplateResult tr : compilationResult.getResults()) {
                VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
                VirtualFile file = virtualFileManager.findFileByUrl("file://" + tr.getTemplateFileName());
                if (tr.hasErrors()) {
                    nbKo++;
                    for (TttCompileError error : tr.getErrors()) {
                        consoleView.print("[ERROR] ", ConsoleViewContentType.ERROR_OUTPUT);
                        OpenFileHyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(myProject, file, error.getLine(), error.getCharInLine());
                        consoleView.printHyperlink(tr.getTemplateFileName(), hyperlinkInfo);
                        consoleView.print(
                            " (" + error.getLine() + "," + error.getCharInLine() + ") : " + error.getMessage() + "\n",
                            ConsoleViewContentType.ERROR_OUTPUT
                        );
                    }
                } else {
                    nbOk++;
                    OpenFileHyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(myProject, file, 0);
                    consoleView.printHyperlink(tr.getTemplateFileName(), hyperlinkInfo);
                    consoleView.print(" -> ", ConsoleViewContentType.NORMAL_OUTPUT);
                    hyperlinkInfo = new OpenFileHyperlinkInfo(myProject, virtualFileManager.findFileByUrl("file://" + tr.getGeneratedFileName()), 0);
                    consoleView.printHyperlink(tr.getGeneratedFileName(), hyperlinkInfo);
                    consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
                }
            }
            consoleView.print("Compilation finished : " + nbOk + " OK, " + nbKo + " failed (took " + compilationResult.getElapsed() + " ms).\n", nbKo > 0 ? ConsoleViewContentType.ERROR_OUTPUT : ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.printHyperlink("Cleanup / compile all\n", project -> {
                DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResultSync();
                AnAction action = ActionManager.getInstance().getAction("com.pojosontheweb.ttt.TttCompileAction");
                action.actionPerformed(new AnActionEvent(null, dataContext,
                    ActionPlaces.UNKNOWN, action.getTemplatePresentation(),
                    ActionManager.getInstance(), 0));
            });

        }
    }


}
