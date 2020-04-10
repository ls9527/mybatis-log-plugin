package mybatis.mylog;

import com.intellij.execution.filters.Filter;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import mybatis.mylog.action.MybatisLogProjectService;
import mybatis.mylog.action.gui.MySqlForm;
import mybatis.mylog.util.ConfigUtil;
import mybatis.mylog.util.PrintUtil;
import mybatis.mylog.util.RestoreSqlUtil;
import mybatis.mylog.util.StringConst;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
            MybatisLogProjectService instance = MybatisLogProjectService.getInstance(project);
            if (instance.getSqlList().contains(preparingLine+currentLine)) {
                preparingLine = "";
                return null;
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

                JPanel theSqlPanel = instance.getTheSqlPanel();
//                if (mybatisLogToolWindow instanceof JPanel) {
                String comment = preStr;
                String finalRestoreSql = restoreSql;

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            MySqlForm mySqlForm = new MySqlForm(project, comment,finalRestoreSql);
                            theSqlPanel.add(mySqlForm.getThePanel());
                            theSqlPanel.revalidate();
                            theSqlPanel.repaint();
                        }
                    });
//                }
                preparingLine = "";
                parametersLine = "";
            }
        }
        return null;
    }
}
