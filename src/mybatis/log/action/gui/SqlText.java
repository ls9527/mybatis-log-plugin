package mybatis.log.action.gui;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import mybatis.log.util.BareBonesBrowserLaunch;
import mybatis.log.util.ConfigUtil;
import mybatis.log.util.RestoreSqlUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ob
 * @email huanglingbin@chainfly.com
 * @date 2019/6/20
 */
public class SqlText extends JFrame {
    private static String preparingLine = "";
    private static String parametersLine = "";
    private static boolean isEnd = false;

    private JPanel panel1;
    private JButton buttonOK;
    private JButton buttonCopy;
    private JButton buttonClose;
    private JTextArea originalTextArea;
    private JTextArea resultTextArea;
    private JButton buttonClear;
    private JButton alipayDonate;
    private JButton githubButton;

    public SqlText(Project project) {
        this.setTitle("restore sql from text"); //设置标题
        setContentPane(panel1);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK(project));
        buttonCopy.addActionListener(e -> onCopy());
        buttonClear.addActionListener(e -> onClear());
        buttonClose.addActionListener(e -> onClose());

        alipayDonate.setContentAreaFilled(false);
        alipayDonate.addActionListener(e -> BareBonesBrowserLaunch.openURL("https://github.com/kookob/mybatis-log-plugin/blob/master/DONATE.md"));

        githubButton.setBorder(null);
        githubButton.setContentAreaFilled(false);
        githubButton.addActionListener(e -> BareBonesBrowserLaunch.openURL("https://github.com/kookob/mybatis-log-plugin"));

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
        panel1.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(Project project) {
        if (originalTextArea == null || StringUtils.isBlank(originalTextArea.getText())) {
            this.resultTextArea.setText("Can't restore sql from text.");
            return;
        }
        String originalText = originalTextArea.getText();
        final String PREPARING = ConfigUtil.getPreparing(project);
        final String PARAMETERS = ConfigUtil.getParameters(project);
        if (originalText.contains(PREPARING) && originalText.contains(PARAMETERS)) {
            String[] sqlArr = originalText.split("\n");
            if (sqlArr != null && sqlArr.length >= 2) {
                String resultSql = "";
                for (int i = 0; i < sqlArr.length; ++i) {
                    String currentLine = sqlArr[i];
                    if (StringUtils.isBlank(currentLine)) {
                        continue;
                    }
                    if (currentLine.contains(PREPARING)) {
                        preparingLine = currentLine;
                        continue;
                    } else {
                        currentLine += "\n";
                    }
                    if (StringUtils.isEmpty(preparingLine)) {
                        continue;
                    }
                    if (currentLine.contains(PARAMETERS)) {
                        parametersLine = currentLine;
                    } else {
                        if (StringUtils.isBlank(parametersLine)) {
                            continue;
                        }
                        parametersLine += currentLine;
                    }
                    if (!parametersLine.endsWith("Parameters: \n") && !parametersLine.endsWith("null\n") && !parametersLine.endsWith(")\n")) {
                        if (i == sqlArr.length - 1) {
                            this.resultTextArea.setText("Can't restore sql from text.");
                            break;
                        }
                        continue;
                    } else {
                        isEnd = true;
                    }
                    if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine) && isEnd) {
                        resultSql += RestoreSqlUtil.restoreSql(project, preparingLine, parametersLine) + "\n------------------------------------------------------------\n";
                    }
                }
                if (StringUtils.isNotEmpty(resultSql)) {
                    this.resultTextArea.setText(resultSql);
                }
            } else {
                this.resultTextArea.setText("Can't restore sql from text.");
            }
        } else {
            this.resultTextArea.setText("Can't restore sql from text.");
        }
    }

    private void onCopy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(this.resultTextArea.getText());
        clipboard.setContents(selection, null);
    }

    private void onClear() {
        this.resultTextArea.setText("");
        this.originalTextArea.setText("");
    }

    private void onClose() {
        this.setVisible(false);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel2.add(scrollPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextArea = new JTextArea();
        scrollPane1.setViewportView(resultTextArea);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel2.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        originalTextArea = new JTextArea();
        scrollPane2.setViewportView(originalTextArea);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("Restore Sql");
        panel3.add(buttonOK, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonCopy = new JButton();
        buttonCopy.setText("Copy");
        panel3.add(buttonCopy, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonClear = new JButton();
        buttonClear.setText("Clear");
        panel3.add(buttonClear, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(5, 10, 5, 10), -1, -1));
        panel2.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        alipayDonate = new JButton();
        alipayDonate.setText("Donate");
        panel4.add(alipayDonate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        githubButton = new JButton();
        githubButton.setText("GitHub");
        panel4.add(githubButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonClose = new JButton();
        buttonClose.setText("Close");
        panel4.add(buttonClose, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
