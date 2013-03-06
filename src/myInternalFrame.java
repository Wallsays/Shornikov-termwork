import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Dis
 * Date: 16.09.12
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */

public class myInternalFrame extends JInternalFrame {
    private File filename;
    private String text = "";
    private String defTitle = "Новый файл ";
    private JTextArea CodeTextArea;

    public File getFilename() {
        return filename;
    }

    public JTextArea getCodeTextArea() {
        return CodeTextArea;
    }

    public myInternalFrame(File filename, String text) {
        super();
        this.filename = filename;
        this.text=text;
        createInternalFrame();
    }

    private void createInternalFrame() {
        setTitleNameAndFrameCount();
        initProperties();
        initInnerConteiners();
        setBounds(0, 0, 582, 162);
    }

    private void initInnerConteiners() {
        Container internalFrameContentPane = getContentPane();
        internalFrameContentPane.setLayout(new FormLayout(
                "default:grow",
                "fill:default:grow"));

        CodeTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(CodeTextArea);
        if(null!= text){
            CodeTextArea.setText(text);
        }

        internalFrameContentPane.add(scrollPane, CC.xy(1, 1, CC.FILL, CC.FILL));

    }

    private void setTitleNameAndFrameCount(){
        if(null==filename){
             setTitle(this.defTitle);

        }
        else setTitle(filename.getName());
    }

    private void initProperties() {
        setMaximizable(false);
        setIconifiable(false);
        setResizable(false);
        setClosable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

}
