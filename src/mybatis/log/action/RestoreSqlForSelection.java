package mybatis.log.action;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import mybatis.log.Icons;
import mybatis.log.action.gui.MySqlForm;
import mybatis.log.util.ConfigUtil;
import mybatis.log.util.PrintUtil;
import mybatis.log.util.RestoreSqlUtil;
import mybatis.log.util.StringConst;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * restore sql from selection
 * @author ob
 */
public class RestoreSqlForSelection extends AnAction {
    private static String preparingLine = "";
    private static String parametersLine = "";
    private static boolean isEnd = false;

    public RestoreSqlForSelection(){
        super(Icons.MyBatisIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;
        CaretModel caretModel = e.getData(LangDataKeys.EDITOR).getCaretModel();
        Caret currentCaret = caretModel.getCurrentCaret();
        String sqlText = currentCaret.getSelectedText();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(StringConst.TOOL_WINDOS);
        if(!ConfigUtil.active || !toolWindow.isAvailable()) {
            new ShowLogInConsoleAction(project).showLogInConsole(project);
        }
        //激活Restore Sql tab
        toolWindow.activate(null);
        final String PREPARING = ConfigUtil.getPreparing(project);
        final String PARAMETERS = ConfigUtil.getParameters(project);
        if(StringUtils.isNotBlank(sqlText) && sqlText.contains(PREPARING) && sqlText.contains(PARAMETERS)) {
            String[] sqlArr = sqlText.split("\n");
            if(sqlArr != null && sqlArr.length >= 2) {
                for(int i=0; i<sqlArr.length; ++i) {
                    String currentLine = sqlArr[i];
                    if(StringUtils.isBlank(currentLine)) {
                        continue;
                    }
                    if(currentLine.contains(PREPARING)) {
                        preparingLine = currentLine;
                        continue;
                    } else {
                        currentLine += "\n";
                    }
                    if(StringUtils.isEmpty(preparingLine)) {
                        continue;
                    }
                    if(currentLine.contains(PARAMETERS)) {
                        parametersLine = currentLine;
                    } else {
                        if(StringUtils.isBlank(parametersLine)) {
                            continue;
                        }
                        parametersLine += currentLine;
                    }
                    if(!parametersLine.endsWith("Parameters: \n") && !parametersLine.endsWith("null\n") && !RestoreSqlUtil.endWithAssembledTypes(parametersLine)) {
                        if(i == sqlArr.length -1) {
                            Messages.showErrorDialog(project, "Can't restore sql from selection.", "Restore Sql Error");
                            this.reset();
                            break;
                        }
                        continue;
                    } else {
                        isEnd = true;
                    }
                    if(StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine) && isEnd) {
                        int indexNum = ConfigUtil.getIndexNum(project);
                        String preStr = "--  " + indexNum + "  restore sql from selection  - ==>";
                        ConfigUtil.setIndexNum(project, ++indexNum);
                        String restoreSql = RestoreSqlUtil.restoreSql(project, preparingLine, parametersLine);

                        if(ConfigUtil.getSqlFormat(project)) {
                            restoreSql = PrintUtil.format(restoreSql);
                        }

                        MybatisLogProjectService instance = MybatisLogProjectService.getInstance(project);
                        JPanel theSqlPanel = instance.getTheSqlPanel();

//                if (mybatisLogToolWindow instanceof JPanel) {
                        String finalRestoreSql = restoreSql;

                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                MySqlForm mySqlForm = new MySqlForm(project, preStr,finalRestoreSql);
                                theSqlPanel.add(mySqlForm.getThePanel());
                                theSqlPanel.revalidate();
                                theSqlPanel.repaint();
                            }
                        });

                        this.reset();
                    }
                }
            } else {
                Messages.showErrorDialog(project, "Can't restore sql from selection.", "Restore Sql Error");
                this.reset();
            }
        } else {
            Messages.showErrorDialog(project, "Can't restore sql from selection.", "Restore Sql Error");
            this.reset();
        }
    }

    private void reset(){
        preparingLine = "";
        parametersLine = "";
        isEnd = false;
    }
}
