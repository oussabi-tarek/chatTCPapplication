package chatClientTcpApplication;



import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MyClient extends javax.swing.JFrame {
    String iD,clientID="";
    Socket s;
    DataInputStream dis;
    DataOutputStream dous;
    File myfile;
    DefaultListModel dlm;

    public MyClient() {
        initComponents();
    }

    MyClient(String id, Socket s) {
        iD=id;
        this.s=s;
        try{
            initComponents();
            dlm=new DefaultListModel();
            UL.setModel(dlm);
            idlabel.setText(id);
            dis=new DataInputStream(s.getInputStream());
            dous=new DataOutputStream(s.getOutputStream());
            new Read().start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    class Read extends  Thread{
        public void run(){
            while(true){
                try{
                    String m=dis.readUTF();
                    if(m.contains("a t envoye un fichier")){
                        System.out.println("je suis ");
                        String extension = (new DataInputStream(s.getInputStream()).readUTF());
                        String size = (new DataInputStream(s.getInputStream()).readUTF());
                        int FILE_SIZE = (Integer.parseInt(size)) + 1;
                        System.out.println("size: "+FILE_SIZE);
                        int bytesRead;
                        int bytesReaded = 0;
                        byte[] mybytearray = new byte[FILE_SIZE];
                        String FILE_TO_RECEIVED = "C:\\Users\\DELL\\Desktop\\L." + extension;
                        InputStream is = s.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(FILE_TO_RECEIVED);
//                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bytesRead = is.read(mybytearray);
//                        bytesReaded = bytesRead;
                        while (bytesRead != -1) {
                            // Write the buffer to the file output stream
                            fileOutputStream.write(mybytearray, 0, bytesRead + 1);

                            if (bytesRead == (FILE_SIZE - 1)) {
                                break;
                            } else {
                                // Read the next chunk of the file
                                bytesRead = is.read(mybytearray);
                            }
                        }
                        System.out.println("recoie du fuchier");
                        msgBox.append("" + m + "\n");
                    }
                    else if(m.contains("envoye un fichier à tous")){
                        String extension = (new DataInputStream(s.getInputStream()).readUTF());
                        String size = (new DataInputStream(s.getInputStream()).readUTF());
                        int FILE_SIZE = (Integer.parseInt(size)) + 1;
                        System.out.println("size: "+FILE_SIZE);
                        int bytesRead;
                        int bytesReaded = 0;
                        byte[] mybytearray = new byte[FILE_SIZE];
                        String FILE_TO_RECEIVED = "C:\\Users\\DELL\\Desktop\\L." + extension;
                        InputStream is = s.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(FILE_TO_RECEIVED);
//                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bytesRead = is.read(mybytearray);
//                        bytesReaded = bytesRead;
                        while (bytesRead != -1) {
                            // Write the buffer to the file output stream
                            fileOutputStream.write(mybytearray, 0, bytesRead + 1);

                            if (bytesRead == (FILE_SIZE - 1)) {
                                break;
                            } else {
                                // Read the next chunk of the file
                                bytesRead = is.read(mybytearray);
                            }
                        }
                        System.out.println("recoie du fuchier");
                        msgBox.append("" + m + "\n");
                    }
                    else{
                        if(m.contains(":;.,/=")){
                            m=m.substring(6);
                            dlm.clear();
                            StringTokenizer st=new StringTokenizer(m,",");
                            while(st.hasMoreTokens()){
                                String u=st.nextToken();
                                if(!iD.equals(u)){
                                    dlm.addElement(u);
                                }
                            }
                        }else{
                            msgBox.append(""+ m + "\n");
                        }
                    }
                }catch(Exception ex){
                    break;
                }

            }
        }
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        idlabel = new javax.swing.JLabel();
        selectall = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        sendButton = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        UL = new javax.swing.JList<>();
        fileupload = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Bonjour :");

        idlabel.setText("................................................");

        selectall.setBackground(new java.awt.Color(242, 242, 242));
        selectall.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        selectall.setText("Selectionner tout");
        selectall.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255)));
        selectall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectallActionPerformed(evt);
            }
        });

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        sendButton.setBackground(new java.awt.Color(204, 204, 204));
        sendButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sendButton.setText("Envoyer");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Message");

        UL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ULMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(UL);

        fileupload.setText("Upload File");
        fileupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileuploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(fileupload)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(21, 21, 21))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(35, 35, 35)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)))
                                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(16, 16, 16)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(selectall, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(19, 19, 19))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(idlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(selectall))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jSeparator1)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(fileupload, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel3)
                                                .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 592, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 425, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }



    private void selectallActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        clientID="";
        System.out.println("afin : "+clientID);
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here
        try{
            String m=jTextField1.getText(),mm=m;
            String CI=clientID;
            if(m.isEmpty() && myfile!=null){
                if(!CI.isEmpty()){
                    m="envoie fichier";
                    dous.writeUTF(m);
                    dous.writeUTF(CI);
                    // Get the file name
                    String fileName = myfile.getName();
                    // Find the index of the last '.' character in the file name
                    int lastDotIndex = fileName.lastIndexOf('.');
                    // Extract the extension from the file name
                    String extension = fileName.substring(lastDotIndex + 1);
                    dous.writeUTF(extension);
                    dous.writeUTF(String.valueOf(myfile.length()));

                    byte[] mybytearray = new byte[(int) myfile.length()];
                    FileInputStream fileInputStream = new FileInputStream(myfile);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                    OutputStream outputStream = s.getOutputStream();
                    System.out.println("Envoi du fichier "+myfile.length());
                    outputStream.write(mybytearray, 0, mybytearray.length);
                    outputStream.flush();
                    System.out.println("Fait.");
                    msgBox.append("<tu as envoye a "+CI+" un fichier>\n");
                }
                else{
                    m="envoie fichier";
                    dous.writeUTF(m);
                    dous.writeUTF(CI);
                    // Get the file name
                    String fileName = myfile.getName();
                    // Find the index of the last '.' character in the file name
                    int lastDotIndex = fileName.lastIndexOf('.');
                    // Extract the extension from the file name
                    String extension = fileName.substring(lastDotIndex + 1);
                    dous.writeUTF(extension);
                    dous.writeUTF(String.valueOf(myfile.length()));

                    byte[] mybytearray = new byte[(int) myfile.length()];
                    FileInputStream fileInputStream = new FileInputStream(myfile);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                    OutputStream outputStream = s.getOutputStream();
                    System.out.println("Envoi du fichier "+myfile.length());
                    outputStream.write(mybytearray, 0, mybytearray.length);
                    outputStream.flush();
                    System.out.println("Fait.");
                    msgBox.append("<tu as envoye a tous un fichier>\n");
                }
            }
            else{

                if(!clientID.isEmpty()){
                    m="envoie a un client"+CI+":"+mm;
                    dous.writeUTF(m);
                    jTextField1.setText("");
                    msgBox.append("<tu as envoye a "+CI+" >"+mm+"\n");
                }else{
                    dous.writeUTF(m);
                    jTextField1.setText("");
                    msgBox.append("<tu as envoye a tous>"+mm+"\n");
                }
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"le client n existe pas ");
        }
    }

    private void ULMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
        clientID=(String)UL.getSelectedValue();
        System.out.println("afin asat : "+clientID);
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        // TODO add your handling code here:
        String i="formWindowClosing";

        try{
            dous.writeUTF(i);
            this.dispose();

        }catch(IOException ex){
            Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

    private void fileuploadActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.myfile=file;

        }
    }





    // Variables declaration - do not modify
    private javax.swing.JList<String> UL;
    private javax.swing.JButton fileupload;
    private javax.swing.JLabel idlabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextArea msgBox;
    private javax.swing.JButton selectall;
    private javax.swing.JButton sendButton;
    // End of variables declaration
}
