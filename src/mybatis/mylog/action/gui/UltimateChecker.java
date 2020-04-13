package mybatis.mylog.action.gui;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Pair;
import com.intellij.sql.SqlFileType;
import com.intellij.sql.psi.SqlLanguage;

/**
 * @author bruce ge
 */
public class UltimateChecker {

    public static Pair<Language, FileType> getLanguageAndType() {
        return Pair.create(SqlLanguage.INSTANCE, SqlFileType.INSTANCE);
    }
}
