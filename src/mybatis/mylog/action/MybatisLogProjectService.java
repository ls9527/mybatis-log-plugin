package mybatis.mylog.action;


import com.google.common.collect.Sets;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.Set;

/**
 * @author bruce ge
 */
public class MybatisLogProjectService {
    private Project myProject;

    private Set<String> sqlList = Sets.newHashSet();


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

    public Set<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(Set<String> sqlList) {
        this.sqlList = sqlList;
    }

    public MybatisLogProjectService(Project project) {
        myProject = project;
    }

    public static final MybatisLogProjectService getInstance(Project project) {
        return ServiceManager.getService(project, MybatisLogProjectService.class);
    }

}
