package mybatis.log.action;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * @author bruce ge
 */
public class MybatisLogProjectService {
    private Project myProject;

    private List<String> sqlList;

    public Project getMyProject() {
        return myProject;
    }

    private JPanel theSqlPanel;


    public JPanel getTheSqlPanel() {
        return theSqlPanel;
    }

    public void setTheSqlPanel(JPanel theSqlPanel) {
        this.theSqlPanel = theSqlPanel;
    }

    public void setMyProject(Project myProject) {
        this.myProject = myProject;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public MybatisLogProjectService(Project project) {
        myProject = project;
    }

    public static final MybatisLogProjectService getInstance(Project project) {
        return ServiceManager.getService(project, MybatisLogProjectService.class);
    }

}
