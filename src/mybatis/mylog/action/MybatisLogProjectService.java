package mybatis.mylog.action;


import com.google.common.collect.Sets;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import kotlin.text.Charsets;

import javax.swing.*;
import java.io.File;
import java.util.Set;
import java.util.UUID;

/**
 * @author bruce ge
 */
public class MybatisLogProjectService {
    private Project myProject;

    private Set<String> sqlList = Sets.newHashSet();


    private VirtualFile virtualFile;


    public VirtualFile getVirtualFile() {
        if (virtualFile == null||!virtualFile.exists()) {
            synchronized (this) {
                File tempFile = null;
                try {
                    tempFile = File.createTempFile(UUID.randomUUID().toString(), ".sql");
                    ;
                    tempFile.deleteOnExit();
                    VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(tempFile);
                    virtualFile.setCharset(Charsets.UTF_8);
                    this.virtualFile = virtualFile;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

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
