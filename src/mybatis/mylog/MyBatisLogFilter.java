package mybatis.mylog;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import mybatis.mylog.action.MybatisLogProjectService;
import mybatis.mylog.action.gui.MySqlForm;
import mybatis.mylog.util.ConfigUtil;
import mybatis.mylog.util.PrintUtil;
import mybatis.mylog.util.RestoreSqlUtil;
import mybatis.mylog.util.StringConst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 语句过滤器
 * @author ob
 */
public class MyBatisLogFilter implements Filter {
    private final Project project;
    private static String preparingLine = "";

    public MyBatisLogFilter(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Result applyFilter(final String currentLine, int endPoint) {
        if(this.project == null) return null;
        if(ConfigUtil.getRunning(project)) {
            //过滤不显示的语句
            String[] filters = PropertiesComponent.getInstance(project).getValues(StringConst.FILTER_KEY);
            if (filters != null && filters.length > 0 && StringUtils.isNotBlank(currentLine)) {
                for (String filter : filters) {
                    if(StringUtils.isNotBlank(filter) && currentLine.toLowerCase().contains(filter.trim().toLowerCase())) {
                        return null;
                    }
                }
            }
            if(currentLine.contains(ConfigUtil.getPreparing(project))) {
                preparingLine = currentLine;
                return null;
            }
            if(StringUtils.isEmpty(preparingLine)) {
                return null;
            }


            boolean contains = currentLine.contains(ConfigUtil.getParameters(project));
            if (!contains) {
                return null;
            }
            System.out.println("currentLine line is:" + currentLine);

            String parametersLine = currentLine;
            if(!parametersLine.endsWith("Parameters: \n") && !parametersLine.endsWith("null\n") && !parametersLine.endsWith(")\n")) {
                return null;
            } else {

            }
            boolean alreadyContainInSqlConsole = false;
            MybatisLogProjectService instance = MybatisLogProjectService.getInstance(project);
            if (instance.getSqlList().contains(preparingLine+currentLine)) {
                alreadyContainInSqlConsole = true;
            } else {
                instance.getSqlList().add(preparingLine+currentLine);
            }


            if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine)) {
                int indexNum = ConfigUtil.getIndexNum(project);
                String preStr = "--  " + indexNum + "  " + parametersLine.split(ConfigUtil.getParameters(project))[0].trim();//序号前缀字符串
                ConfigUtil.setIndexNum(project, ++indexNum);
                String restoreSql = RestoreSqlUtil.restoreSql(project, preparingLine, parametersLine);
                if(ConfigUtil.getSqlFormat(project)) {
                    restoreSql = PrintUtil.format(restoreSql);
                }
//                if (mybatisLogToolWindow instanceof JPanel) {
                String comment = preStr;
                String finalRestoreSql = restoreSql;
                if (!alreadyContainInSqlConsole) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ToolWindowManager.getInstance(project).getToolWindow(StringConst.TOOL_WINDOS).activate(null);
                            JPanel theSqlPanel = instance.getTheSqlPanel();
                            if (theSqlPanel == null) {
                                throw new RuntimeException("the sql panel is null");
                            }
                            MySqlForm mySqlForm = new MySqlForm(project, comment, finalRestoreSql);
                            theSqlPanel.add(mySqlForm.getThePanel());
                            theSqlPanel.revalidate();
                            theSqlPanel.repaint();
                        }
                    });
//                }
                }
                preparingLine = "";
                parametersLine = "";
                int textStartOffset = endPoint - currentLine.length();
                return new Result(textStartOffset, textStartOffset + currentLine.length(), new HyperlinkInfo() {
                    @Override
                    public void navigate(Project project) {
                        File tempFile = null;
                        try {
                            tempFile = File.createTempFile(UUID.randomUUID().toString(), ".sql");
                            FileUtils.writeLines(tempFile, Lists.newArrayList(finalRestoreSql));
                            tempFile.deleteOnExit();
                            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(tempFile);
                            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                            CodeInsightUtil.positionCursor(project, file, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return null;
    }
}
