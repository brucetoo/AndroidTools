package template.translate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.apache.http.util.TextUtils;
import template.adb.AdbFacade;
import template.ui.NotificationHelper;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;

/**
 * Created by brucetoo on 27/03/2017.
 */
public class TransitionAction extends AnAction {

    private long latestClickTime;

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        if (!isFastClick(1000)) {
            getTranslation(e);
        }
    }

    private void getTranslation(AnActionEvent event) {
        Editor mEditor = event.getData(PlatformDataKeys.EDITOR);
        if (null == mEditor) {
            return;
        }
        SelectionModel model = mEditor.getSelectionModel();
        String selectedText = model.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            selectedText = getCurrentWords(mEditor);
            if (TextUtils.isEmpty(selectedText)) {
                return;
            }
        }
        String queryText = strip(addBlanks(selectedText));
        AdbFacade.EXECUTOR.submit(new RequestRunnable(mEditor, queryText));
    }

    public String getCurrentWords(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int lineNum = document.getLineNumber(caretOffset);
        int lineStartOffset = document.getLineStartOffset(lineNum);
        int lineEndOffset = document.getLineEndOffset(lineNum);
        String lineContent = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        char[] chars = lineContent.toCharArray();
        int start = 0, end = 0, cursor = caretOffset - lineStartOffset;

        try {
            if (!Character.isLetter(chars[cursor])) {
                NotificationHelper.error("Caret not in a word");
                return null;
            }
        }catch (IndexOutOfBoundsException e){
            NotificationHelper.error("Need choose words correctly");
            return null;
        }

        for (int ptr = cursor; ptr >= 0; ptr--) {
            if (!Character.isLetter(chars[ptr])) {
                start = ptr + 1;
                break;
            }
        }

        int lastLetter = 0;
        for (int ptr = cursor; ptr < lineEndOffset - lineStartOffset; ptr++) {
            lastLetter = ptr;
            if (!Character.isLetter(chars[ptr])) {
                end = ptr;
                break;
            }
        }
        if (end == 0) {
            end = lastLetter + 1;
        }

        String ret = new String(chars, start, end - start);
        NotificationHelper.info("Selected words: " + ret);
        return ret;
    }

    public String addBlanks(String str) {
        String temp = str.replaceAll("_", " ");
        if (temp.equals(temp.toUpperCase())) {
            return temp;
        }
        String result = temp.replaceAll("([A-Z]+)", " $0");
        return result;
    }

    public String strip(String str) {
        return str.replaceAll("/\\*+", "")
                .replaceAll("\\*+/", "")
                .replaceAll("\\*", "")
                .replaceAll("//+", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\\s+", " ");
    }

    public boolean isFastClick(long timeMillis) {
        long time = System.currentTimeMillis();
        long timeD = time - latestClickTime;
        if (0 < timeD && timeD < timeMillis) {
            return true;
        }
        latestClickTime = time;
        return false;
    }
}
