package chatServerTcpApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

// ce class permet de gerer le serveur
public class Server extends javax.swing.JFrame {

    ServerSocket ss;
    // HashMap pour stocker les clients
    HashMap clientColl=new HashMap();

    public Server() {
        try{
            initComponents();
            ss=new ServerSocket(4400);
            this.sStatus.setText("Server Started");
            // lance le thread pour accepter les clients
            new ClientAccept().start();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    // ce thread permet d'accepter les clients a chaque fois qu'un client se connecte
    class ClientAccept extends Thread{
        public void run(){
            while(true){
                try{
                    Socket s=ss.accept();
                    String i=new DataInputStream(s.getInputStream()).readUTF();
                    if(clientColl.containsKey(i)){
                        DataOutputStream data=new DataOutputStream(s.getOutputStream());
                        data.writeUTF("Vous êtes déjà inscrit!!!");
                    }else{
                        // ajoute le client dans le HashMap
                        clientColl.put(i,s);
                        // afficher un message dans le serveur pour dire que le client est connecté
                        msgBox.append(i+" Join!\n");
                        DataOutputStream data=new DataOutputStream(s.getOutputStream());
                        data.writeUTF("");
                        // lance le thread pour recevoir les messages du client
                        new MsgRead(s,i).start();
                        // lancer le thread pour preparer la liste des clients connectés et l'envoyer au client
                        new PrepareClientList().start();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    // ce class interne c est un Thread pour lire les messages du client
    class MsgRead extends Thread{
        Socket s;
        String ID;

        MsgRead(Socket s,String ID){
            this.s=s;
            this.ID=ID;
        }
        public void run(){
            // tant que il ya des clients connectés au serveur on envoie et on reçoit les messages
            while(!clientColl.isEmpty()){
                try{
                    // lire le message du client
                    String i=new DataInputStream(s.getInputStream()).readUTF();
                    // le client a quitte le chat en fermant la fenetre
                    if(i.equals("formWindowClosing")){
                        clientColl.remove(ID);
                        msgBox.append(ID+" removed! \n");
                        // lancer le thread pour preparer la liste des clients connectés et l'envoyer a tous les clients
                        new PrepareClientList().start();
                        Set<String> k=clientColl.keySet();
                        Iterator itr=k.iterator();
                        while(itr.hasNext()){
                            String key=(String)itr.next();
                            if(!key.equalsIgnoreCase(ID)){
                                try{
                                    new DataOutputStream(((Socket)clientColl.get(key)).getOutputStream()).writeUTF(ID+" A quitté le chat");

                                }catch(Exception ex){
                                    clientColl.remove(key);
                                    msgBox.append(key+" removed");
                                    new PrepareClientList().start();
                                }
                            }
                        }
                    }
                    // le client veut envoyer un fichier
                    else if(i.contains("envoie fichier")){
                        String CI=(new DataInputStream(s.getInputStream()).readUTF());
                        // recuperer la longueur du fichier et l extension du fichier
                        String extension = (new DataInputStream(s.getInputStream()).readUTF());
                        String size = (new DataInputStream(s.getInputStream()).readUTF());
                        int FILE_SIZE = (Integer.parseInt(size))+1;
                        System.out.println("size: " + FILE_SIZE);
                        // lire le fichier
                        int bytesRead;
                        int bytesReaded = 0;
                        byte[] mybytearray = new byte[FILE_SIZE];
                        // telecharger le fichier dans le serveur dans ce chemin
                        String FILE_TO_RECEIVED = "C:\\Users\\DELL\\Downloads\\L." + extension;
                        InputStream is = s.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(FILE_TO_RECEIVED);
//                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        bytesRead = is.read(mybytearray);
//                        bytesReaded = bytesRead;
                        while (bytesRead != -1 ) {
                            System.out.println("bytes:"+bytesRead);
                            fileOutputStream.write(mybytearray, 0, bytesRead+1);
                            if(bytesRead==(FILE_SIZE-1)){
                                break;
                            }else{

                                bytesRead = is.read(mybytearray);
                            }

                        }
                        // dormir le thread pour attendre que le fichier soit telecharger
                        sleep(2000);

                        System.out.println("Fichier " + FILE_TO_RECEIVED
                                + " telecharge avec succes  (" + bytesReaded + " bytes lit)");
                        // envoyer le fichier a un client specifique
                        if (!CI.isEmpty()) {
                            new DataOutputStream(((Socket)clientColl.get(CI)).getOutputStream()).writeUTF("< "+ID+" a t envoye un fichier>veuillez consultez vos dossiers ");
                            File myFile = new File(FILE_TO_RECEIVED);
                            System.out.println("file lenget: "+myFile.length());
                            byte[] mybytearray1 = new byte[(int) myFile.length()];
                            String fileName = myFile.getName();
                            int lastDotIndex = fileName.lastIndexOf('.');
                            String extension1 = fileName.substring(lastDotIndex + 1);
                            new DataOutputStream(((Socket)clientColl.get(CI)).getOutputStream()).writeUTF(extension1);
                            new DataOutputStream(((Socket)clientColl.get(CI)).getOutputStream()).writeUTF(String.valueOf(myFile.length()));


                            FileInputStream fileInputStream = new FileInputStream(myFile);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                            bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                            OutputStream outputStream = ((Socket)clientColl.get(CI)).getOutputStream();
                            System.out.println("Envoi du fichier " + myFile.length());
                            outputStream.write(mybytearray, 0, mybytearray.length);
                            outputStream.flush();
                        }
                        // envoie a tous les clients
                        else{
                            Set k = clientColl.keySet();
                            Iterator itr = k.iterator();
                            File myFile = new File(FILE_TO_RECEIVED);
                            System.out.println("file lenget: "+myFile.length());
                            byte[] mybytearray1 = new byte[(int) myFile.length()];
                            String fileName = myFile.getName();
                            int lastDotIndex = fileName.lastIndexOf('.');
                            String extension1 = fileName.substring(lastDotIndex + 1);
                            while (itr.hasNext()) {
                                String key = (String) itr.next();
                                if (!key.equalsIgnoreCase(ID)) {
                                    try {
                                        new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF("< " + ID + " envoye un fichier à tous >");
                                        new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF(extension1);
                                        new DataOutputStream(((Socket) clientColl.get(key)).getOutputStream()).writeUTF(String.valueOf(myFile.length()));
                                        FileInputStream fileInputStream = new FileInputStream(myFile);
                                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                                        bufferedInputStream.read(mybytearray, 0, mybytearray.length);
                                        OutputStream outputStream = ((Socket) clientColl.get(key)).getOutputStream();
                                        System.out.println("Envoi du fichier " + myFile.length());
                                        outputStream.write(mybytearray, 0, mybytearray.length);
                                        outputStream.flush();
                                    } catch (Exception ex) {
                                        clientColl.remove(key);
                                        msgBox.append(key + ": removed!");
                                        new PrepareClientList().start();
                                    }
                                }
                            }
                        }
                    }
                    // message normale vers un client
                    else if(i.contains("envoie a un client")){
                        i=i.substring(18);
                        StringTokenizer  st=new StringTokenizer(i,":");
                        String id=st.nextToken();
                        i=st.nextToken();
                        try{
                            new DataOutputStream(((Socket)clientColl.get(id)).getOutputStream()).writeUTF("< "+ID+" a t envoye> "+i);
                        }catch(Exception ex){
                            clientColl.remove(id);
                            msgBox.append(id+": removed!");
                            new PrepareClientList().start();
                        }
                    }
                    // l envoie d un message normale a tous les clients
                    else{
                        Set k=clientColl.keySet();
                        Iterator itr=k.iterator();
                        while(itr.hasNext()){
                            String key=(String)itr.next();
                            if(!key.equalsIgnoreCase(ID)){
                                try{
                                    new DataOutputStream(((Socket)clientColl.get(key)).getOutputStream()).writeUTF("< "+ID+" à tous >"+i);
                                }catch(Exception ex){
                                    clientColl.remove(key);
                                    msgBox.append(key+": removed!");
                                    new PrepareClientList().start();
                                }
                            }
                        }
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    // classe pour preparer la liste des clients et l envoyer a tous les clients
    class PrepareClientList extends Thread{
        public void run(){
            try{
                String ids="";
                Set k=clientColl.keySet();
                Iterator itr=k.iterator();
                while(itr.hasNext()){
                    String key=(String)itr.next();
                    ids+=key+",";
                }
                if(ids.length()!=0){
                    ids.substring(0,ids.length()-1);
                }
                itr=k.iterator();
                while(itr.hasNext()){
                    String key=(String)itr.next();
                    try{
                        new DataOutputStream(((Socket)clientColl.get(key)).getOutputStream()).writeUTF(":;.,/="+ids);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }



    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        msgBox = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        sStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MyServer");

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        msgBox.setColumns(20);
        msgBox.setRows(5);
        jScrollPane1.setViewportView(msgBox);

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel1.setText("Server Status :");

        sStatus.setText(".........................................................");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(18, 18, 18)
                                                .addComponent(sStatus)))
                                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(sStatus))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea msgBox;
    private javax.swing.JLabel sStatus;

}
