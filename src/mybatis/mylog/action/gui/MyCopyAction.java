package mybatis.mylog.action.gui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * @author bruce ge
 */
public class MyCopyAction extends AnAction {
    private String theSqlText;

    public MyCopyAction(String theSqlText) {
        super("Copy", "Copy sql", AllIcons.Actions.Copy);
        this.theSqlText = theSqlText;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        CopyPasteManager.getInstance().setContents(new StringSelection(theSqlText));
        JBPopupFactory.getInstance().createMessage("Copyed text:" + theSqlText).showInFocusCenter();
    }
}
