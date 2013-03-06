import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
/*
 * Created by JFormDesigner on Mon Nov 19 20:04:32 NOVT 2012
 */

/**
 * @author D
 */
public class MainView extends JFrame {
    private final ISubFunctions sf = new SubFunctions();
    private final IFunctions pf = new Functions();
    private String copyBuffer = null;
    UndoManager undoManager = new UndoManager();
    private boolean idle =true;
    private String savedText = "";
    private myInternalFrame frame;

    public MainView() {
        setNimbusLookAndFeel();
        initComponents();
        setup();
    }
    private void setup() {
        splitPane1.setDividerLocation(0.7);
        setVisible(true);
    }

    private void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
    }


    private void undoRedoInit(JMenuItem undoItem, JMenuItem redoItem, JTextArea codeTextArea) {
        codeTextArea.getDocument().addUndoableEditListener(
                new UndoableEditListener() {
                    public void undoableEditHappened(UndoableEditEvent e) {
                        undoManager.addEdit(e.getEdit());
                        updateButtons();
                    }
                });

        this.undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (CannotUndoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });

        this.redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.redo();
                } catch (CannotRedoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });

    }

    private void updateButtons() {
        undoItem.setEnabled(undoManager.canUndo());
        redoItem.setEnabled(undoManager.canRedo());
    }

    void setButtonsReady(boolean stat){
        undoItem.setEnabled(stat);
        redoItem.setEnabled(stat);
        saveButton.setEnabled(stat);
        saveItem.setEnabled(stat);
        saveAsButton.setEnabled(stat);
        saveAsItem.setEnabled(stat);
        copyButton.setEnabled(stat);
        copyItem.setEnabled(stat);
        cutButton.setEnabled(stat);
        cutItem.setEnabled(stat);
        pasteButton.setEnabled(stat);
        pasteItem.setEnabled(stat);
        ClearButton.setEnabled(stat);
        closeItem.setEnabled(stat);
        runItem.setEnabled(stat);
    }

    private void newFileFunc(File selectedFile, String text) {
       if(!idle){
           closeFileFunc();
       }
       idle = false;
       savedText = text;
       frame = new myInternalFrame(selectedFile, text);
       frame.addInternalFrameListener(new InternalFrameAdapter() {
           @Override
           public void internalFrameClosing(InternalFrameEvent e) {
               closeFileFunc();
           }
       }
       );
        undoRedoInit(undoItem, redoItem, frame.getCodeTextArea());
        desktopPane1.add(frame, JLayeredPane.DEFAULT_LAYER);
        frame.show();
        try {
            frame.setMaximum(true);
        } catch (PropertyVetoException e) {
            // Vetoed by internalFrame
            // ... possibly add some handling for this case
        }
       setButtonsReady(true);

    }

    private void saveFileFunc() {
        if (frame.getFilename() == null) {
            saveAsFileFunc();
        }
        else {
            savedText = frame.getCodeTextArea().getText();
            sf.writeFile(frame.getFilename(),savedText); 
        } 
    }

    private void saveAsFileFunc() {
        JFileChooser c = new JFileChooser();
        c.setSelectedFile(new File("FOR_example.txt"));
        int rVal = c.showSaveDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            savedText = frame.getCodeTextArea().getText();
            sf.writeFile(c.getSelectedFile(), savedText);
        }
    }

    private void openFileFunc() {
        if(!idle){
            closeFileFunc();
        }
        JFileChooser c = new JFileChooser();
        int rVal = c.showOpenDialog(this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            newFileFunc(c.getSelectedFile(), sf.getText(c.getSelectedFile()));
        }
    }

    private void destroyFrame(){
        frame.dispose();
        idle=true ;
        setButtonsReady(false);
        savedText=null;
        ResultTextArea.setText("");
    }

    private void closeFileFunc() {
        if(frame.getCodeTextArea().getText().equals(savedText)) {
            destroyFrame();
        }
        else {
            switch (showPreCloseDialog("\"" + frame.getTitle()+"\" был изменен. ")) {
                case 0:
                    saveFileFunc();
                    destroyFrame();
                    break;
                case 1:
                    destroyFrame();
                    break;
                case 2:
                    break;
            }
        }
    }

    private int showPreCloseDialog(String message) {
        Object[] options = {"Да",
                "Нет",
                "Отмена"};
        int n = JOptionPane.showOptionDialog(this,
                message +
                        "Сохранить измененния?",
                "Сохранение файла",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);
        return n;
    }

    private void copyFunc() {
        JTextArea ta = frame.getCodeTextArea();
        copyBuffer = ta.getSelectedText();
    }

    private void pasteFunc() {
        JTextArea ta = frame.getCodeTextArea();
        ta.insert(copyBuffer, ta.getCaretPosition());

    }

    private void cutFunc() {
        JTextArea ta =  frame.getCodeTextArea();
        copyBuffer = ta.getSelectedText();
        ta.setText(ta.getText().replace(ta.getSelectedText(), ""));
    }

    private void ClearButtonActionPerformed(ActionEvent e) {
           frame.getCodeTextArea().setText("");
    }

    private void thisComponentResized(ComponentEvent e) {
        splitPane1.setDividerLocation(0.7);
    }

    private void thisWindowClosing(WindowEvent e) {
        System.exit(0);
    }

    private void newItemActionPerformed(ActionEvent e) {
        newFileFunc(null, "");
    }

    private void openItemActionPerformed(ActionEvent e) {
        openFileFunc();
    }

    private void closeItemActionPerformed(ActionEvent e) {
        closeFileFunc();
    }

    private void saveItemActionPerformed(ActionEvent e) {
        saveFileFunc();
    }

    private void saveAsItemActionPerformed(ActionEvent e) {
        saveAsFileFunc();
    }

    private void exitItemActionPerformed(ActionEvent e) {
        System.exit(0);
    }

    private void copyItemActionPerformed(ActionEvent e) {
        copyFunc();
    }

    private void cutItemActionPerformed(ActionEvent e) {
        cutFunc();
    }

    private void pasteItemActionPerformed(ActionEvent e) {
        pasteFunc();
    }

    private void aboutItemActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
                "Курсовая работа по предмету СПО\nОператор For языка С++\n\n",
                "О программе",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void newButtonActionPerformed(ActionEvent e) {
        newFileFunc(null, "");
    }

    private void openButtonActionPerformed(ActionEvent e) {
        openFileFunc();
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        saveFileFunc();
    }

    private void saveAsButtonActionPerformed(ActionEvent e) {
        saveAsFileFunc();
    }

    private void copyButtonActionPerformed(ActionEvent e) {
        copyFunc();
    }

    private void cutButtonActionPerformed(ActionEvent e) {
        cutFunc();
    }

    private void pasteButtonActionPerformed(ActionEvent e) {
        pasteFunc();
    }

    private void runItemActionPerformed(ActionEvent e) {
        File f= new File("~for.txt");
        sf.writeFile(f,frame.getCodeTextArea().getText());
        ResultTextArea.setText(pf.run("~for.txt"));
        f.delete();
    }

    private void menuItem10ActionPerformed(ActionEvent e) {
        // содержание
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("coderjanie.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem11ActionPerformed(ActionEvent e) {
        // постановка задачи
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("postanovka.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem12ActionPerformed(ActionEvent e) {
        // грамматика
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("gramma.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem13ActionPerformed(ActionEvent e) {
        // классификация
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("classification.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem14ActionPerformed(ActionEvent e) {
        // диагностика
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("diagnostic.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem15ActionPerformed(ActionEvent e) {
        // алгоритм анализа
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("algoritm.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem16ActionPerformed(ActionEvent e) {
        // листинг
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("code.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem17ActionPerformed(ActionEvent e) {
        // литература
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File("literature.doc"));
            } catch (IOException e1) {
                //To change body of catch statement use File | Settings | File Templates.
                //e1.printStackTrace();
            }
        }
    }

    private void menuItem18ActionPerformed(ActionEvent e) {
        // помощь
        try {
            Runtime.getRuntime().exec("hh.exe help.chm");
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        newItem = new JMenuItem();
        openItem = new JMenuItem();
        closeItem = new JMenuItem();
        saveItem = new JMenuItem();
        saveAsItem = new JMenuItem();
        exitItem = new JMenuItem();
        menu2 = new JMenu();
        undoItem = new JMenuItem();
        redoItem = new JMenuItem();
        copyItem = new JMenuItem();
        cutItem = new JMenuItem();
        pasteItem = new JMenuItem();
        menu3 = new JMenu();
        menuItem10 = new JMenuItem();
        menuItem11 = new JMenuItem();
        menuItem12 = new JMenuItem();
        menuItem13 = new JMenuItem();
        menuItem14 = new JMenuItem();
        menuItem15 = new JMenuItem();
        menuItem16 = new JMenuItem();
        menuItem17 = new JMenuItem();
        menu4 = new JMenu();
        runItem = new JMenuItem();
        menu5 = new JMenu();
        menuItem18 = new JMenuItem();
        aboutItem = new JMenuItem();
        toolBar1 = new JToolBar();
        newButton = new JButton();
        openButton = new JButton();
        saveButton = new JButton();
        saveAsButton = new JButton();
        copyButton = new JButton();
        cutButton = new JButton();
        pasteButton = new JButton();
        splitPane1 = new JSplitPane();
        desktopPane1 = new JDesktopPane();
        scrollPane2 = new JScrollPane();
        ResultTextArea = new JTextArea();
        toolBar2 = new JToolBar();
        ClearButton = new JButton();
        toolBar3 = new JToolBar();
        radioButton1 = new JRadioButton();
        radioButton2 = new JRadioButton();

        //======== this ========
        setTitle("\u0422\u0435\u043a\u0441\u0442\u043e\u0432\u044b\u0439 \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setIconImage(new ImageIcon(getClass().getResource("/icons/Component.png")).getImage());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                thisComponentResized(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default, default:grow, bottom:41dlu"));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("\u0424\u0430\u0439\u043b");

                //---- newItem ----
                newItem.setText("\u0421\u043e\u0437\u0434\u0430\u0442\u044c");
                newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
                newItem.setIcon(new ImageIcon(getClass().getResource("/icons/List.png")));
                newItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        newItemActionPerformed(e);
                    }
                });
                menu1.add(newItem);

                //---- openItem ----
                openItem.setText("\u041e\u0442\u043a\u0440\u044b\u0442\u044c");
                openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
                openItem.setIcon(new ImageIcon(getClass().getResource("/icons/Folder.png")));
                openItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openItemActionPerformed(e);
                    }
                });
                menu1.add(openItem);

                //---- closeItem ----
                closeItem.setText("\u0417\u0430\u043a\u0440\u044b\u0442\u044c");
                closeItem.setIcon(new ImageIcon(getClass().getResource("/icons/Cancel.png")));
                closeItem.setEnabled(false);
                closeItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeItemActionPerformed(e);
                    }
                });
                menu1.add(closeItem);
                menu1.addSeparator();

                //---- saveItem ----
                saveItem.setText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c");
                saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
                saveItem.setIcon(new ImageIcon(getClass().getResource("/icons/Save.png")));
                saveItem.setEnabled(false);
                saveItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveItemActionPerformed(e);
                    }
                });
                menu1.add(saveItem);

                //---- saveAsItem ----
                saveAsItem.setText("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043a\u0430\u043a");
                saveAsItem.setIcon(new ImageIcon(getClass().getResource("/icons/Download.png")));
                saveAsItem.setEnabled(false);
                saveAsItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveAsItemActionPerformed(e);
                    }
                });
                menu1.add(saveAsItem);
                menu1.addSeparator();

                //---- exitItem ----
                exitItem.setText("\u0412\u044b\u0445\u043e\u0434");
                exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
                exitItem.setIcon(new ImageIcon(getClass().getResource("/icons/Exit.png")));
                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exitItemActionPerformed(e);
                    }
                });
                menu1.add(exitItem);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("\u041f\u0440\u0430\u0432\u043a\u0430");

                //---- undoItem ----
                undoItem.setText("\u041e\u0442\u043c\u0435\u043d\u0438\u0442\u044c");
                undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
                undoItem.setEnabled(false);
                undoItem.setIcon(new ImageIcon(getClass().getResource("/icons/Undo.png")));
                menu2.add(undoItem);

                //---- redoItem ----
                redoItem.setText("\u041f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u044c");
                redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
                redoItem.setEnabled(false);
                redoItem.setIcon(new ImageIcon(getClass().getResource("/icons/Redo.png")));
                menu2.add(redoItem);
                menu2.addSeparator();

                //---- copyItem ----
                copyItem.setText("\u041a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c");
                copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
                copyItem.setIcon(new ImageIcon(getClass().getResource("/icons/Copy.png")));
                copyItem.setEnabled(false);
                copyItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        copyItemActionPerformed(e);
                    }
                });
                menu2.add(copyItem);

                //---- cutItem ----
                cutItem.setText("\u0412\u044b\u0440\u0435\u0437\u0430\u0442\u044c");
                cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
                cutItem.setIcon(new ImageIcon(getClass().getResource("/icons/Cut.png")));
                cutItem.setEnabled(false);
                cutItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cutItemActionPerformed(e);
                    }
                });
                menu2.add(cutItem);

                //---- pasteItem ----
                pasteItem.setText("\u0412\u0441\u0442\u0430\u0432\u0438\u0442\u044c");
                pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
                pasteItem.setIcon(new ImageIcon(getClass().getResource("/icons/Paste.png")));
                pasteItem.setEnabled(false);
                pasteItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        pasteItemActionPerformed(e);
                    }
                });
                menu2.add(pasteItem);
            }
            menuBar1.add(menu2);

            //======== menu3 ========
            {
                menu3.setText("\u0422\u0435\u043a\u0441\u0442");

                //---- menuItem10 ----
                menuItem10.setText("\u0421\u043e\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435");
                menuItem10.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem10ActionPerformed(e);
                    }
                });
                menu3.add(menuItem10);

                //---- menuItem11 ----
                menuItem11.setText("\u041f\u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u0437\u0430\u0434\u0430\u0447\u0438");
                menuItem11.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem11ActionPerformed(e);
                    }
                });
                menu3.add(menuItem11);
                menu3.addSeparator();

                //---- menuItem12 ----
                menuItem12.setText("\u0413\u0440\u0430\u043c\u043c\u0430\u0442\u0438\u043a\u0430");
                menuItem12.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem12ActionPerformed(e);
                    }
                });
                menu3.add(menuItem12);

                //---- menuItem13 ----
                menuItem13.setText("\u041a\u043b\u0430\u0441\u0441\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u044f");
                menuItem13.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem13ActionPerformed(e);
                    }
                });
                menu3.add(menuItem13);

                //---- menuItem14 ----
                menuItem14.setText("\u0414\u0438\u0430\u0433\u043d\u043e\u0441\u0442\u0438\u043a\u0430");
                menuItem14.setIcon(null);
                menuItem14.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem14ActionPerformed(e);
                    }
                });
                menu3.add(menuItem14);

                //---- menuItem15 ----
                menuItem15.setText("\u0410\u043b\u0433\u043e\u0440\u0438\u0442\u043c \u0430\u043d\u0430\u043b\u0438\u0437\u0430");
                menuItem15.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem15ActionPerformed(e);
                    }
                });
                menu3.add(menuItem15);

                //---- menuItem16 ----
                menuItem16.setText("\u041b\u0438\u0441\u0442\u0438\u043d\u0433");
                menuItem16.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem16ActionPerformed(e);
                    }
                });
                menu3.add(menuItem16);
                menu3.addSeparator();

                //---- menuItem17 ----
                menuItem17.setText("\u0421\u043f\u0438\u0441\u043e\u043a \u043b\u0438\u0442\u0435\u0440\u0430\u0442\u0443\u0440\u044b");
                menuItem17.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem17ActionPerformed(e);
                    }
                });
                menu3.add(menuItem17);
            }
            menuBar1.add(menu3);

            //======== menu4 ========
            {
                menu4.setText("\u041f\u0443\u0441\u043a");

                //---- runItem ----
                runItem.setText("\u0417\u0430\u043f\u0443\u0441\u043a");
                runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
                runItem.setIcon(new ImageIcon(getClass().getResource("/icons/Play.png")));
                runItem.setHorizontalAlignment(SwingConstants.LEFT);
                runItem.setEnabled(false);
                runItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        runItemActionPerformed(e);
                    }
                });
                menu4.add(runItem);
            }
            menuBar1.add(menu4);

            //======== menu5 ========
            {
                menu5.setText("\u0421\u043f\u0440\u0430\u0432\u043a\u0430");

                //---- menuItem18 ----
                menuItem18.setText("\u041f\u043e\u043c\u043e\u0449\u044c");
                menuItem18.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
                menuItem18.setIcon(new ImageIcon(getClass().getResource("/icons/Hint.png")));
                menuItem18.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        menuItem18ActionPerformed(e);
                    }
                });
                menu5.add(menuItem18);

                //---- aboutItem ----
                aboutItem.setText("\u041e \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0435");
                aboutItem.setIcon(new ImageIcon(getClass().getResource("/icons/Info.png")));
                aboutItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        aboutItemActionPerformed(e);
                    }
                });
                menu5.add(aboutItem);
            }
            menuBar1.add(menu5);
        }
        setJMenuBar(menuBar1);

        //======== toolBar1 ========
        {
            toolBar1.setFloatable(false);

            //---- newButton ----
            newButton.setIcon(new ImageIcon(getClass().getResource("/icons/List.png")));
            newButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newButtonActionPerformed(e);
                }
            });
            toolBar1.add(newButton);

            //---- openButton ----
            openButton.setIcon(new ImageIcon(getClass().getResource("/icons/Folder.png")));
            openButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openButtonActionPerformed(e);
                }
            });
            toolBar1.add(openButton);

            //---- saveButton ----
            saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/Save.png")));
            saveButton.setEnabled(false);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveButtonActionPerformed(e);
                }
            });
            toolBar1.add(saveButton);

            //---- saveAsButton ----
            saveAsButton.setIcon(new ImageIcon(getClass().getResource("/icons/Download.png")));
            saveAsButton.setEnabled(false);
            saveAsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveAsButtonActionPerformed(e);
                }
            });
            toolBar1.add(saveAsButton);
            toolBar1.addSeparator(new Dimension(40, 10));

            //---- copyButton ----
            copyButton.setIcon(new ImageIcon(getClass().getResource("/icons/Copy.png")));
            copyButton.setEnabled(false);
            copyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    copyButtonActionPerformed(e);
                }
            });
            toolBar1.add(copyButton);

            //---- cutButton ----
            cutButton.setIcon(new ImageIcon(getClass().getResource("/icons/Cut.png")));
            cutButton.setEnabled(false);
            cutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cutButtonActionPerformed(e);
                }
            });
            toolBar1.add(cutButton);

            //---- pasteButton ----
            pasteButton.setIcon(new ImageIcon(getClass().getResource("/icons/Paste.png")));
            pasteButton.setEnabled(false);
            pasteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pasteButtonActionPerformed(e);
                }
            });
            toolBar1.add(pasteButton);
        }
        contentPane.add(toolBar1, CC.xy(1, 1));

        //======== splitPane1 ========
        {
            splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane1.setDividerSize(14);
            splitPane1.setOneTouchExpandable(true);
            splitPane1.setDividerLocation(150);

            //======== desktopPane1 ========
            {
                desktopPane1.setMinimumSize(new Dimension(0, 130));
                desktopPane1.setPreferredSize(new Dimension(1, 1));
            }
            splitPane1.setTopComponent(desktopPane1);

            //======== scrollPane2 ========
            {
                scrollPane2.setMinimumSize(new Dimension(25, 50));
                scrollPane2.setPreferredSize(new Dimension(18, 34));
                scrollPane2.setMaximumSize(new Dimension(32767, 200));

                //---- ResultTextArea ----
                ResultTextArea.setEditable(false);
                scrollPane2.setViewportView(ResultTextArea);
            }
            splitPane1.setBottomComponent(scrollPane2);
        }
        contentPane.add(splitPane1, CC.xy(1, 2, CC.FILL, CC.FILL));

        //======== toolBar2 ========
        {
            toolBar2.setFloatable(false);

            //---- ClearButton ----
            ClearButton.setText("\u041e\u0447\u0438\u0441\u0442\u043a\u0430");
            ClearButton.setIcon(new ImageIcon(getClass().getResource("/icons/Trash.png")));
            ClearButton.setEnabled(false);
            ClearButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ClearButtonActionPerformed(e);
                }
            });
            toolBar2.add(ClearButton);
            toolBar2.addSeparator(new Dimension(50, 10));

            //======== toolBar3 ========
            {
                toolBar3.setBorder(new TitledBorder(null, "\u0412\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u043e\u0432", TitledBorder.LEADING, TitledBorder.ABOVE_TOP,
                    new Font("sansserif", Font.PLAIN, 12)));
                toolBar3.setMinimumSize(new Dimension(176, 62));
                toolBar3.setFloatable(false);

                //---- radioButton1 ----
                radioButton1.setText("\u0412\u0438\u0434\u0438\u043c\u043e");
                toolBar3.add(radioButton1);

                //---- radioButton2 ----
                radioButton2.setText("\u041d\u0435\u0432\u0438\u0434\u0438\u043c\u043e");
                toolBar3.add(radioButton2);
            }
            toolBar2.add(toolBar3);
        }
        contentPane.add(toolBar2, CC.xy(1, 3));
        pack();
        setLocationRelativeTo(null);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButton1);
        buttonGroup1.add(radioButton2);

        //---- bindings ----
        bindingGroup = new BindingGroup();
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            scrollPane2, BeanProperty.create("visible"),
            radioButton1, BeanProperty.create("selected")));
        bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem closeItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem exitItem;
    private JMenu menu2;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem copyItem;
    private JMenuItem cutItem;
    private JMenuItem pasteItem;
    private JMenu menu3;
    private JMenuItem menuItem10;
    private JMenuItem menuItem11;
    private JMenuItem menuItem12;
    private JMenuItem menuItem13;
    private JMenuItem menuItem14;
    private JMenuItem menuItem15;
    private JMenuItem menuItem16;
    private JMenuItem menuItem17;
    private JMenu menu4;
    private JMenuItem runItem;
    private JMenu menu5;
    private JMenuItem menuItem18;
    private JMenuItem aboutItem;
    private JToolBar toolBar1;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton copyButton;
    private JButton cutButton;
    private JButton pasteButton;
    private JSplitPane splitPane1;
    private JDesktopPane desktopPane1;
    private JScrollPane scrollPane2;
    private JTextArea ResultTextArea;
    private JToolBar toolBar2;
    private JButton ClearButton;
    private JToolBar toolBar3;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

