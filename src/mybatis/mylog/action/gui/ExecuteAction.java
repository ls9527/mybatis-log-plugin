package mybatis.mylog.action.gui;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author bruce ge
 */
public class ExecuteAction extends AnAction {
    private MySqlForm theForm;

    public ExecuteAction(MySqlForm mySqlForm) {
        super("Execute","Execute sql",AllIcons.Actions.Execute);
        theForm = mySqlForm;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String theSqlText = theForm.getTheSqlText();
        Project project = anActionEvent.getProject();
        File tempFile = null;
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), ".sql");
            FileUtils.writeLines(tempFile, Lists.newArrayList(theSqlText));
            tempFile.deleteOnExit();
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(tempFile);
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            CodeInsightUtil.positionCursor(project, file, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
