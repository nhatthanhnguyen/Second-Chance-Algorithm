import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import java.util.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.*;
class FileTypeFilter extends FileFilter {
    private String extension;
    private String description;
    public FileTypeFilter(String extension, String desciption) {
        this.extension = extension;
        this.description = desciption;
    }
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        return f.getName().endsWith(this.extension);
    }
    @Override
    public String getDescription() {
        return description + String.format(" (*%s)", extension);
    }
}
class MyFrame extends JFrame {
    private JLabel title;
    private JLabel teacher;
    private JLabel author;
    private JLabel numberOfPage;
    private JTextField inputNumberOfPage;
    private JLabel numberOfFrame;
    private JTextField inputNumberOfFrame;
    private JLabel inputInstruction1;
    private JLabel inputInstruction2;
    private JLabel inputInstruction3;
    private JLabel inputInstruction4;
    private JLabel inputInstruction5;
    private DefaultTableModel defaultTableInput;
    private JTable tableInput;
    private JScrollPane paneInput;
    private JPanel panelButton;
    private JButton run;
    private DefaultTableModel defaultTableOutput;
    private JTable tableOutput;
    private JScrollPane paneOutput;
    private JLabel outputMessage;
    private int MAX_WIDTH = 800;
    private int MAX_HEIGHT = 550;
    private int noOfPage = -1;
    private int noOfFrame = -1;
    private String path = "";
    boolean firstTime = true;

    private boolean checkInputPage(String s) {
        if (s.length() > 4 || s.length() == 0) return false;
        if (s.length() == 1 && s.charAt(0) == '0') return false;
        int n = Integer.parseInt(s);
        if (n > 0 && n <= 1000) return true;
        return false;
    }
    private boolean checkInputFrame(String s) {
        if (s.length() > 2 || s.length() == 0) return false;
        if (s.length() == 1 && s.charAt(0) == '0') return false;
        int n = Integer.parseInt(s);
        if (n > 0 && n <= 30) return true;
        return false;
    }
    private boolean checkPage(String s) {
        if (s.length() > 4 || s.length() == 0) return false;
        if (s.length() == 1 && s.charAt(0) == '0') return false;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9')
                return false;
        }
        return true;
    }
    private boolean checkAllValue() {
        if (noOfFrame != -1 && noOfPage != -1) {
            String[] value = new String[noOfPage];
            for (int i = 0; i < noOfPage; ++i)
                value[i] = defaultTableInput.getValueAt(i, 1).toString();
            ArrayList<Integer> errorValue = new ArrayList<Integer>();
            for (int i = 0; i < noOfPage; ++i) {
                if (checkPage(value[i]) == false) {
                    errorValue.add(i + 1);
                }
            }
            if (!errorValue.isEmpty()) {
                String errorMessage = "";
                if (errorValue.size() == 1) {
                    errorMessage += "Ô " + Integer.toString(errorValue.get(0)) + " có vấn đề nhập xuất! Đầu vào phải là một số nguyên dương không quá 4 chữ số";
                } else {
                    errorMessage += "Những ô ";
                    for (int i = 0; i < errorValue.size() - 1; ++i) {
                        errorMessage += Integer.toString(errorValue.get(i)) + ", ";
                    }
                    errorMessage += Integer.toString(errorValue.get(errorValue.size() - 1));
                    errorMessage += " có vấn đề nhập xuất!\nĐầu vào phải là một số nguyên dương có tối đa 4 chữ số.";
                }
                JOptionPane.showMessageDialog(new JFrame(), errorMessage, 
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                return true;
            }
        } else {
            if (noOfFrame == -1 && noOfPage != -1) {
                JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số khung trang\"", 
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            } else if (noOfPage == -1 && noOfFrame != -1) {
                JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số lượng trang\"", 
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            } else if (noOfFrame == -1 && noOfPage == -1) {
                JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở hai ô nhập dữ liệu:\n- Số lượng trang\n- Số khung trang", 
                    "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }
    private boolean findQueue(Queue<Integer> q, int x) {
        for (Integer value: q) {
            if (value == x) {
                return true;
            }
        }
        return false;
    }
    private void algorithm() {
        int col = defaultTableOutput.getColumnCount();
        int row = defaultTableOutput.getRowCount();
        int numberOfErrorPage = 0;
        int[] page = new int[noOfPage];
        int[] res1 = new int[noOfFrame];
        int[] res2 = new int[noOfFrame + 1];
        for (int i = 0; i < noOfPage; ++i) {
            page[i] = Integer.parseInt(defaultTableInput.getValueAt(i, 1).toString());
        }
        if (col > noOfPage) {
            defaultTableOutput.setColumnCount(noOfPage);
        } else {
            for (int i = 0; i < noOfPage - col; ++i) {
                defaultTableOutput.addColumn("1");
            }
        }
        for (int i = 0; i < noOfPage; ++i) {
            tableOutput.getColumnModel().getColumn(i).setHeaderValue(Integer.toString(page[i]));
            tableOutput.getTableHeader().resizeAndRepaint();
        }
        if (row > noOfFrame) {
            Object[] rowData = new Object[noOfPage];
            for (int i = 0; i < noOfPage; ++i)
                rowData[i] = "";
            for (int i = 0; i < row - noOfFrame; ++i) {
                defaultTableOutput.removeRow(defaultTableOutput.getRowCount() - 1);
            }
            defaultTableOutput.addRow(rowData);
        } else {
            Object[] rowData = new Object[noOfPage];
            for (int i = 0; i < noOfPage; ++i)
                rowData[i] = "";
            for (int i = 0; i < noOfFrame - row; ++i) {
                defaultTableOutput.addRow(rowData);
            }
            defaultTableOutput.addRow(rowData);
        }
        for (int i = 0; i < noOfFrame; ++i)
            res1[i] = res2[i] = -1;
        res2[noOfFrame] = 0;
        Queue<Integer> q = new LinkedList<Integer>();
        int count = 0;
        boolean[] bitRef = new boolean[noOfFrame];
        for (int i = 0; i < noOfFrame; ++i)
            bitRef[i] = true;
        for (int i = 0; i < noOfPage; ++i) {
            int x = page[i];
            if (findQueue(q, x)) {
                q.add(x);
                Queue<Integer>temp = new LinkedList<Integer>();
                int counter = 0;
                while(!q.isEmpty()) {
                    if (q.peek() == x) {
                        q.poll();
                        break;
                    }
                    counter++;
                    temp.add(q.peek());
                    q.poll();
                }
                while(!q.isEmpty()) {
                    temp.add(q.peek());
                    q.poll();
                }
                q = new LinkedList<Integer>(temp);
                for (int j = counter; j < noOfFrame - 1; ++j)
                    bitRef[j] = bitRef[j + 1];
                bitRef[noOfFrame - 1] = true;
                int index = 0;
                while(!temp.isEmpty()) {
                    res1[index] = temp.peek();
                    res2[index] = (bitRef[index] == true ? 1 : 0);
                    index++;
                    temp.poll();
                }
                for (int j = 0; j < noOfFrame; ++j) {
                    if (res1[j] != -1 && res2[j] != -1) {
                        String result = Integer.toString(res1[j]);
                        result += "(";
                        result += Integer.toString(res2[j]);
                        result += ")";
                        defaultTableOutput.setValueAt(result, j, i);
                    } else {
                        defaultTableOutput.setValueAt("", j, i);
                    }
                }
                res2[noOfFrame] = 0;
                defaultTableOutput.setValueAt("", noOfFrame, i);
            } else {
                if (count < noOfFrame) {
                    q.add(x);
                    count++;
                    int index = 0;
                    Queue<Integer>temp = new LinkedList<Integer>(q);
                    while(!temp.isEmpty()) {
                        res1[index] = temp.peek();
                        res2[index] = (bitRef[index] == true ? 1 : 0);
                        index++;
                        temp.poll();
                    }
                    for (int j = 0; j < noOfFrame; ++j) {
                        if (res1[j] != -1 && res2[j] != -1) {
                            String result = Integer.toString(res1[j]);
                            result += "(";
                            result += Integer.toString(res2[j]);
                            result += ")";
                            defaultTableOutput.setValueAt(result, j, i);
                        } else {
                            defaultTableOutput.setValueAt("", j, i);
                        }
                    }
                } else {
                    int counter = 0;
                    while(!q.isEmpty()) {
                        if (bitRef[counter % noOfFrame]) {
                            bitRef[counter % noOfFrame] = !bitRef[counter % noOfFrame];
                            q.add(q.peek());
                            q.poll();
                        } else {
                            break;
                        }
                        counter++;
                    }
                    for (int j = counter % noOfFrame; j < noOfFrame; ++j)
                        bitRef[j - (counter % noOfFrame)] = bitRef[j];
                    for (int j = noOfFrame - (counter % noOfFrame); j < noOfFrame; ++j)
                        bitRef[j] = false;
                    q.poll();
                    q.add(x);
                    for (int j = 0; j < noOfFrame - 1; ++j)
                        bitRef[j] = bitRef[j + 1];
                    bitRef[noOfFrame - 1] = true;
                    Queue<Integer> temp = new LinkedList<Integer>(q);
                    int index = 0;
                    while(!temp.isEmpty()) {
                        res1[index] = temp.peek();
                        res2[index] = (bitRef[index] == true ? 1 : 0);
                        index++;
                        temp.poll();
                    }
                    for (int j = 0; j < noOfFrame; ++j) {
                        if (res1[j] != -1 && res2[j] != -1) {
                            String result = Integer.toString(res1[j]);
                            result += "(";
                            result += Integer.toString(res2[j]);
                            result += ")";
                            defaultTableOutput.setValueAt(result, j, i);
                        } else {
                            defaultTableOutput.setValueAt("", j, i);
                        }
                    }
                }
                res2[noOfFrame] = 1;
                numberOfErrorPage++;
                defaultTableOutput.setValueAt("*", noOfFrame, i);
            }
        }
        paneOutput.setVisible(true);
        String msgString = "Ký hiệu * là có lỗi trang và có " + Integer.toString(numberOfErrorPage) + " lỗi trang";
        outputMessage.setText(msgString);
        outputMessage.setVisible(true);
    }
    private void writeToFile() {
        if (firstTime == true) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setDialogTitle("Chọn file để lưu");
            fileChooser.setFileFilter(new FileTypeFilter(".scc", "Second chance file"));
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                try {
                    File fileSave = fileChooser.getSelectedFile();
                    path = fileSave.getPath();
                    if (path.charAt(path.length() - 4) == '.' 
                    && path.charAt(path.length() - 3) == 's'
                    && path.charAt(path.length() - 2) == 'c'
                    && path.charAt(path.length() - 1) == 'c') {
                        FileWriter fileWriter;
                        String content = "";
                        int n = Integer.parseInt(inputNumberOfPage.getText());
                        content += inputNumberOfPage.getText() + " ";
                        for (int i = 0; i < n; ++i)
                            content += defaultTableInput.getValueAt(i, 1).toString() + " ";
                        content += Integer.parseInt(inputNumberOfFrame.getText());
                        path = fileSave.getPath();
                        fileWriter = new FileWriter(path);
                        fileWriter.write(content);
                        fileWriter.flush();
                        fileWriter.close();
                        firstTime = false;
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "File của bạn lưu không phải đuôi .scc hãy chỉnh sửa lại!", "Lỗi file", JOptionPane.ERROR_MESSAGE);
                        firstTime = true;
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Lỗi ghi file");
                    firstTime = true;
                }
            }
        } else {
            try {
                String content = "";
                int n = Integer.parseInt(inputNumberOfPage.getText());
                content += inputNumberOfPage.getText() + " ";
                for (int i = 0; i < n; ++i)
                    content += defaultTableInput.getValueAt(i, 1).toString() + " ";
                content += Integer.parseInt(inputNumberOfFrame.getText());
                FileWriter fileWriter = new FileWriter(path);
                fileWriter.write(content);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(new JFrame(), "Lỗi ghi file");
            }
        }
    }
    private void showMessageDialog(boolean exit) {
        int ret = JOptionPane.showConfirmDialog(new JFrame(), "Bạn có muốn lưu file không?", 
        "Lưu file", JOptionPane.YES_NO_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION) {
            return;
        } else if (ret == JOptionPane.YES_OPTION) {
            if (checkAllValue()) {
                writeToFile();
                if (exit == true) 
                    System.exit(0);
                else 
                    return;
            } else {
                if (exit == true) 
                    System.exit(0);
                else 
                    return;
            }
        } else if (ret == JOptionPane.NO_OPTION) {
            if (exit == true)
                System.exit(0);
            else
                return;
        }
    }
    public MyFrame() {

        final JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenu = new JMenuItem("Mới");
        JMenuItem openMenu = new JMenuItem("Mở file");
        JMenuItem saveMenu = new JMenuItem("Lưu");

        fileMenu.add(newMenu);
        fileMenu.add(openMenu);
        fileMenu.add(saveMenu);
        menuBar.add(fileMenu);
        menuBar.setBounds(0, 0, 30, 15);
        newMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = JOptionPane.showConfirmDialog(new JFrame(), "Bạn có muốn lưu file không?", 
                "Lưu file", JOptionPane.YES_NO_CANCEL_OPTION);
                if (ret == JOptionPane.CANCEL_OPTION) {
                    return;
                } else if (ret == JOptionPane.YES_OPTION) {
                    boolean check = checkAllValue();
                    if (check) {
                        writeToFile();
                        inputNumberOfPage.setText("");
                        inputNumberOfFrame.setText("");
                        inputInstruction5.setVisible(false);
                        paneInput.setVisible(false);
                        paneOutput.setVisible(false);
                        outputMessage.setVisible(false);
                        noOfFrame = -1;
                        noOfPage = -1;
                        firstTime = true;
                        return;
                    } else {
                        return;
                    }
                } else if (ret == JOptionPane.NO_OPTION) {
                    inputNumberOfPage.setText("");
                    inputNumberOfFrame.setText("");
                    inputInstruction5.setVisible(false);
                    paneInput.setVisible(false);
                    paneOutput.setVisible(false);
                    outputMessage.setVisible(false);
                    noOfFrame = -1;
                    noOfPage = -1;
                    firstTime = true;
                    return;
                }
            }
        });
        openMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(false);
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fileChooser.setDialogTitle("Chọn một file để mở");
                fileChooser.setFileFilter(new FileTypeFilter(".scc", "Second chance file"));
                int ret = fileChooser.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fileChooser.getSelectedFile();
                        path = file.getPath();
                        if (path.charAt(path.length() - 4) == '.' 
                        && path.charAt(path.length() - 3) == 's'
                        && path.charAt(path.length() - 2) == 'c'
                        && path.charAt(path.length() - 1) == 'c') {
                            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
                            String res = "";
                            String line = "";
                            while((line = br.readLine()) != null) {
                                res += line;
                            }
                            if (br != null) {
                                br.close();
                            }
                            String[] value = res.split(" ");
                            boolean isNumber = true;
                            for (int i = 0; i < value.length; ++i) {
                                if (!checkPage(value[i])) {
                                    isNumber = false;
                                }
                            }
                            if (!isNumber) {
                                JOptionPane.showMessageDialog(null, "Hãy kiểm tra lại file của bạn!\nCó một (vài) phần tử không phải là số hoặc các số có trên 4 chữ số!",
                                    "Lỗi file", JOptionPane.ERROR_MESSAGE);
                            } else {
                                noOfPage = Integer.parseInt(value[0]);
                                noOfFrame = Integer.parseInt(value[value.length - 1]);
                                if (value.length - 2 == noOfPage) {
                                    inputNumberOfPage.setText(value[0]);
                                    inputNumberOfFrame.setText(value[value.length - 1]);
                                    int countRow = defaultTableInput.getRowCount();
                                    for (int i = 0; i < countRow; ++i) {
                                        defaultTableInput.setValueAt("", i, 1);
                                    }
                                    if (countRow < noOfPage) {
                                        int temp = countRow + 1;
                                        for (int i = 0; i < noOfPage - countRow; ++i) {
                                            String index = Integer.toString(temp);
                                            temp += 1;
                                            defaultTableInput.addRow(new Object[]{index, ""});
                                        }
                                    } else {
                                        for (int i = 0; i < countRow - noOfPage; ++i) {
                                            defaultTableInput.removeRow(defaultTableInput.getRowCount() - 1);
                                        }
                                    }
                                    for (int i = 0; i < noOfPage; ++i) {
                                        defaultTableInput.setValueAt(value[i + 1], i, 1);
                                    }
                                    paneInput.setVisible(true);
                                    inputInstruction5.setVisible(true);
                                    firstTime = false;
                                } else {
                                    JOptionPane.showMessageDialog(null, "Hãy kiểm tra lại file của bạn!\nSố lượng phần tử không phù hợp.\nHint: Số lượng phần tử số trong file trừ đi 2 phải bằng số đầu tiên của file",
                                    "Lỗi file", JOptionPane.ERROR_MESSAGE);
                                    noOfPage = -1;
                                    noOfFrame = -1;
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(new Frame(), "Bạn chọn file không đúng định dạng .scc hãy chọn một file đúng định dạng .scc", "Lỗi file", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (FileNotFoundException e2) {
                        JOptionPane.showMessageDialog(null, "Lỗi không tìm được file");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "Lỗi luồng vào ra");
                    }
                }
            }
        });
        saveMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(false);
            }
        });

        title = new JLabel("THUẬT TOÁN CƠ HỘI THỨ HAI");
        title.setBounds(0, 15, MAX_WIDTH, 25);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.BLUE);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        teacher = new JLabel("Giáo viên hướng dẫn: Huỳnh Thanh Tâm");
        teacher.setBounds(0, 40, MAX_WIDTH, 25);
        teacher.setHorizontalAlignment(JLabel.CENTER);
        teacher.setForeground(Color.MAGENTA);
        teacher.setFont(new Font("Arial", Font.BOLD, 16));

        author = new JLabel("Sinh viên: Nguyễn Nhật Thanh - N19DCCN190 - D19CQCN02-N");
        author.setBounds(0, 65, MAX_WIDTH, 25);
        author.setHorizontalAlignment(JLabel.CENTER);
        author.setForeground(Color.BLACK);
        author.setFont(new Font("Arial", Font.PLAIN, 16));

        numberOfPage = new JLabel("Nhập vào số lượng trang");
        numberOfPage.setBounds(10, 100, 200, 25);
        numberOfPage.setFont(new Font("Arial", Font.PLAIN, 14));

        noOfPage = 0;
        String[] column = {"STT", "Trang"};
        defaultTableInput = new DefaultTableModel(null, column) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        tableInput = new JTable(defaultTableInput);
        paneInput = new JScrollPane(tableInput);
        tableInput.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        paneInput.setBounds(500, 120, 168, 150);
        paneInput.setVisible(false);

        inputNumberOfPage = new JTextField();
        inputNumberOfPage.setBounds(180, 100, 100, 25);
        inputNumberOfPage.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
        inputNumberOfPage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent k) {
                int key = k.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    String value = inputNumberOfPage.getText();
                    if (checkInputPage(value)) {
                        noOfPage = Integer.parseInt(value);
                        int countRow = defaultTableInput.getRowCount();
                        for (int i = 0; i < countRow; ++i) {
                            defaultTableInput.setValueAt("", i, 1);
                        }
                        if (countRow < noOfPage) {
                            int temp = countRow + 1;
                            for (int i = 0; i < noOfPage - countRow; ++i) {
                                String index = Integer.toString(temp);
                                temp += 1;
                                defaultTableInput.addRow(new Object[]{index, ""});
                            }
                        } else {
                            for (int i = 0; i < countRow - noOfPage; ++i) {
                                defaultTableInput.removeRow(defaultTableInput.getRowCount() - 1);
                            }
                        }
                        paneInput.setVisible(true);
                        inputInstruction5.setVisible(true);
                    } else {
                        noOfPage = -1;
                        JOptionPane.showMessageDialog(new JFrame(),"Đầu vào phải là một số nguyên dương không quá 1000!", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    }
                }
             }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                showMessageDialog(true);
            }
        });

        numberOfFrame = new JLabel("Nhập vào số khung trang");
        numberOfFrame.setBounds(10, 125, 200, 25);
        numberOfFrame.setFont(new Font("Arial", Font.PLAIN, 14));

        inputNumberOfFrame = new JTextField();
        inputNumberOfFrame.setBounds(180, 125, 100, 25);
        inputNumberOfFrame.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 14));
        inputNumberOfFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent k) {
                int key = k.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    String value = inputNumberOfFrame.getText();
                    if (checkInputFrame(value))
                        noOfFrame = Integer.parseInt(value);
                    else {
                        noOfFrame = -1;
                        JOptionPane.showMessageDialog(new JFrame(),"Đầu vào phải là một số nguyên dương không quá 30!", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        inputInstruction1 = new JLabel("*Dữ liệu vào của số trang là một số nguyên dương không quá 1000.");
        inputInstruction1.setBounds(10, 150, 440, 20);
        inputInstruction1.setForeground(Color.RED);
        inputInstruction1.setFont(new Font("Arial", Font.BOLD, 12));

        inputInstruction2 = new JLabel("*Dữ liệu vào của số khung trang là một số nguyên dương không quá 30.");
        inputInstruction2.setBounds(10, 170, 440, 20);
        inputInstruction2.setForeground(Color.RED);
        inputInstruction2.setFont(new Font("Arial", Font.BOLD, 12));

        inputInstruction3 = new JLabel("*Dữ liệu vào ở bảng trang là một số nguyên dương không quá 4 chữ số.");
        inputInstruction3.setBounds(10, 190, 440, 20);
        inputInstruction3.setForeground(Color.RED);
        inputInstruction3.setFont(new Font("Arial", Font.BOLD, 12));

        inputInstruction4 = new JLabel("*Ấn phím Enter để hoàn thành việc nhập liệu.");
        inputInstruction4.setBounds(10, 210, 440, 20);
        inputInstruction4.setForeground(Color.RED);
        inputInstruction4.setFont(new Font("Arial", Font.BOLD, 12));

        inputInstruction5 = new JLabel("Nhập dữ liệu ở đây");
        inputInstruction5.setBounds(520, 100, 180, 20);
        inputInstruction5.setFont(new Font("Arial", Font.BOLD, 14));
        inputInstruction5.setVisible(false);

        // output
        panelButton = new JPanel();
        panelButton.setBounds(0, 270, MAX_WIDTH, 50);
        run = new JButton("Kết quả");
        run.setFont(new Font("Arial", Font.BOLD, 15));
        run.setPreferredSize(new Dimension(150, 40));
        run.setHorizontalAlignment(JButton.CENTER);
        
        defaultTableOutput = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableOutput = new JTable(defaultTableOutput);
        tableOutput.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        paneOutput = new JScrollPane(tableOutput);
        paneOutput.setBounds(20, 330, MAX_WIDTH - 56, 120);
        paneOutput.setVisible(false);

        outputMessage = new JLabel();
        outputMessage.setBounds(0, 450, MAX_WIDTH, 20);
        outputMessage.setHorizontalAlignment(JLabel.CENTER);
        outputMessage.setFont(new Font("Arial", Font.BOLD, 14));

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (noOfFrame != -1 && noOfPage != -1) {
                    int currNoOfPage = Integer.parseInt(inputNumberOfPage.getText().toString());
                    int currNoOfFrame = Integer.parseInt(inputNumberOfFrame.getText().toString());
                    if (currNoOfFrame != noOfFrame && currNoOfPage == noOfPage) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số khung trang\"\nBạn đã không Enter sau khi nhập xong\nHint: Enter sau khi nhập xong hoặc trả về như cũ", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    } else if (currNoOfFrame == noOfFrame && currNoOfPage != noOfPage) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số lượng trang\"\nBạn đã không Enter sau khi nhập xong\nHint: Enter sau khi nhập xong hoặc trả về như cũ", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    } else if (currNoOfFrame != noOfFrame && currNoOfPage != noOfPage) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở hai ô nhập dữ liệu:\n- Số lượng trang\n- Số khung trang\nBạn đã không Enter sau khi nhập xong\nHint: Enter sau khi nhập xong hoặc trả về như cũ", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String[] value = new String[noOfPage];
                        for (int i = 0; i < noOfPage; ++i)
                            value[i] = defaultTableInput.getValueAt(i, 1).toString();
                        ArrayList<Integer> errorValue = new ArrayList<Integer>();
                        for (int i = 0; i < noOfPage; ++i) {
                            if (checkPage(value[i]) == false) {
                                errorValue.add(i + 1);
                            }
                        }
                        if (!errorValue.isEmpty()) {
                            String errorMessage = "";
                            if (errorValue.size() == 1) {
                                errorMessage += "Ô " + Integer.toString(errorValue.get(0)) + " có vấn đề nhập xuất! Đầu vào phải là một số nguyên dương không quá 4 chữ số";
                            } else {
                                errorMessage += "Những ô ";
                                for (int i = 0; i < errorValue.size() - 1; ++i) {
                                    errorMessage += Integer.toString(errorValue.get(i)) + ", ";
                                }
                                errorMessage += Integer.toString(errorValue.get(errorValue.size() - 1));
                                errorMessage += " có vấn đề nhập xuất!\nĐầu vào phải là một số nguyên dương có tối đa 4 chữ số.";
                            }
                            JOptionPane.showMessageDialog(new JFrame(), errorMessage, 
                                "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                        } else {
                            algorithm();   
                        }
                    }
                } else {
                    if (noOfFrame == -1 && noOfPage != -1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số khung trang\"", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    } else if (noOfPage == -1 && noOfFrame != -1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở ô \"Số lượng trang\"", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    } else if (noOfFrame == -1 && noOfPage == -1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Hãy kiểm tra lại việc nhập liệu ở hai ô nhập dữ liệu:\n- Số lượng trang\n- Số khung trang", 
                            "Lỗi nhập dữ liệu", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        panelButton.add(run);

        setTitle("Thuật toán cơ hội thứ hai");
        setSize(MAX_WIDTH, MAX_HEIGHT);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        setVisible(true);
        add(menuBar);
        add(title);
        add(teacher);
        add(author);
        add(numberOfPage);
        add(inputNumberOfPage);
        add(inputInstruction1);
        add(inputInstruction2);
        add(inputInstruction3);
        add(inputInstruction4);
        add(inputInstruction5);
        add(numberOfFrame);
        add(inputNumberOfFrame);
        add(paneInput);
        add(panelButton);
        add(paneOutput);
        add(outputMessage);
    }
}
public class SecondChance{
    public static void main(String[] args) {
        new MyFrame();
    }
}