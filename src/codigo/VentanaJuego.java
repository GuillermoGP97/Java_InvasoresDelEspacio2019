/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;

/**
 *
 * @author Guillermo García Palma
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHOPANTALLA = 600;
    static int ALTOPANTALLA = 450;

    int posX;
    int posY;
    int contadorTiempo=0;
    //numero de marcianos que van a aparecer
    int filas = 4;
    int columnas = 8;

    BufferedImage buffer = null;

    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    //Marciano miMarciano = new Marciano();
    Marciano[][] listaMarcianos = new Marciano[filas][columnas];
    ArrayList <Explosion> listaExplosiones = new ArrayList();
    boolean direccionMarcianos = false;
    //el contador sirve para decidir qué imagen del marciano toca poner
    int contador = 0;
    //imagen para cargar el spritesheet con todos los sprites del juego
    BufferedImage plantilla = null;
    Image [][] imagenes ;
    Image fondo;
    
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bucleDelJuego();
        }
    });

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        //para cargar el archivo de imagenes: 
        // 1º, el nombre del archivo
        // 2º filas que tiene el spritesheet
        // 3º columnas que tiene el spritesheet
        // 4º lo que mide de ancho el sprite en el spritesheet
        // 5º lo que mide de alto el sprite en el spritesheet
        // 6º para cambiar el tamaño de los sprites
        imagenes = cargaImagenes("/imagenes/invaders2.png", 5, 4, 64, 64, 2);
        
        miDisparo.imagen = imagenes[4][3];
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();

        temporizador.start();

        //inicializo la posición inicial de la nave
        miNave.imagen = imagenes[4][2];
        miNave.x = ANCHOPANTALLA / 2 - miNave.imagen.getWidth(this) / 2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this) - 40;
        
        //fonndo
        try{
            fondo = ImageIO.read(getClass().getResource("/imagenes/fondo.png"));
        }catch(IOException ex){
            
        }
        
        /*miExplosion.imagenExplosion = imagenes[4][0];
        miExplosion.imagenExplosion2 = imagenes[4][1];*/
        //inicializo el array de marcianos
        //os reto a que hagais esto usando mods (es decir, usando el bucle for anidado)

        //1 parametro: numero de la fila de marcianos que estoy creando
        //2º parametro: fila dentro del spritesheet del marciano que quiero pintar
        //3º parametro: columna dentro del spritesheet del marciano que quiero pintar
        creaFilaDeMarcianos(0, 0, 2);
        creaFilaDeMarcianos(1, 2, 2);
        creaFilaDeMarcianos(2, 2, 2);
        creaFilaDeMarcianos(3, 0, 2);        
    }
  
    
  private void creaFilaDeMarcianos(int numeroFila, int spriteFila, int spriteColumna){
      for (int j = 0; j < columnas; j++) {
          listaMarcianos[numeroFila][j] = new Marciano();
          listaMarcianos[numeroFila][j].imagen1 = imagenes[spriteFila][spriteColumna];
          listaMarcianos[numeroFila][j].imagen2 = imagenes[spriteFila][spriteColumna + 1];
          listaMarcianos[numeroFila][j].x = j * (15 + listaMarcianos[numeroFila][j].imagen1.getWidth(null));
          listaMarcianos[numeroFila][j].y = numeroFila * (10 + listaMarcianos[numeroFila][j].imagen1.getHeight(null));
      }
  }  
  
  // Este método servirá para reproducir los sonidos
  
  private void reproduceSonidos (String sonido){
      try{
          Clip clip = AudioSystem.getClip();
          clip.open(AudioSystem.getAudioInputStream(getClass().getResource(sonido)));
          clip.loop(0);
          Thread one = new Thread(){
              public void run(){
                    while(clip.getFramePosition()<clip.getFrameLength())
                        Thread.yield();
                    
          }
          };
        one.start();
      }catch (Exception e) {
          
      }
  }
    
    /*
    este método va a servir para crear el array de imagenes con todas las imagenes
    del spritesheet. Devolverá un array de dos dimensiones con las imágenes colocadas
    tal y como están en el spritesheet
    */
    private Image[][] cargaImagenes(String nombreArchivoImagenes, 
                                       int numFilas ,int numColumnas, int ancho, int alto, int escala){
        try {
            plantilla = ImageIO.read(getClass().getResource(nombreArchivoImagenes));
        } catch (IOException ex) { }
        Image [][] arrayImagenes = new Image[numFilas][numColumnas];
        
        //cargo las imagenes de forma individual en cada imagen del array de imagenes
        for (int i=0; i<numFilas; i++){
            for (int j=0; j<numColumnas; j++){
                arrayImagenes[i][j] = plantilla.getSubimage(j*ancho, i*alto, ancho, alto);
                arrayImagenes[i][j] = arrayImagenes[i][j].getScaledInstance(ancho/escala, ancho /escala, Image.SCALE_SMOOTH);
            }
        }
        
        return arrayImagenes;
        
        
//        //la última fila del spritesheet sólo mide 32 de alto, así que hay que hacerla aparte
//        for (int j=0; j<4; j++){
//            imagenes[20 + j] = plantilla.getSubimage(j*64, 5*64, 64, 32);
//        }
//
//        //la última columna del spritesheet sólo mide 32 de ancho, así que hay que hacerla aparte
//    
//        imagenes[24] = plantilla.getSubimage(4*64, 2*64, 32, 64);
//        imagenes[24] = imagenes[24].getScaledInstance(16, 32, Image.SCALE_SMOOTH);
//   
    }
    
    private void bucleDelJuego() {
        //se encarga del redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        contador++;
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);

        g2.drawImage(fondo,0,0, null);
        ///////////////////////////////////////////////////////
        //redibujaremos aquí cada elemento
        g2.drawImage(miDisparo.imagen, miDisparo.x, miDisparo.y, null);
        g2.drawImage(miNave.imagen, miNave.x, miNave.y, null);
        /*g2.drawImage(miExplosion.imagenExplosion, miExplosion.x, miExplosion.y, null);
        g2.drawImage(miExplosion.imagenExplosion2, miExplosion.x, miExplosion.y, null);*/
        pintaMarcianos(g2);
        pintaExplosiones(g2);
        chequeaColision();
        actualizaContadorTiempo();
        miNave.mueve();
        miDisparo.mueve();
        /////////////////////////////////////////////////////////////
        //*****************   fase final, se dibuja ***************//
        //*****************   el buffer de golpe sobre el Jpanel***//

        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

    }

    private void chequeaColision(){
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        
        rectanguloDisparo.setFrame( miDisparo.x, 
                                    miDisparo.y,
                                    miDisparo.imagen.getWidth(null),
                                    miDisparo.imagen.getHeight(null));
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                
                if (listaMarcianos[i][j].vivo) {
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x,
                                                listaMarcianos[i][j].y,
                                                listaMarcianos[i][j].imagen1.getWidth(null),
                                                listaMarcianos[i][j].imagen1.getHeight(null)
                                                );
                    if (rectanguloDisparo.intersects(rectanguloMarciano)){
                        posX =i;
                        posY =j;
                        listaMarcianos[i][j].vivo = false;
                        miDisparo.posicionaDisparo(miNave);
                        miDisparo.y = 1000;
                        miDisparo.disparado = false;
                        Explosion e = new Explosion();
                        listaExplosiones.add(e);
                        e.sonidoExplosion.start();

                //no borro aqui el disparo para evitar que se cuelgue 
                //listaDisparos.remove(j);
                    }
                }
            }
        }
    }
    
    private void cambiaDireccionMarcianos() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                listaMarcianos[i][j].setvX(listaMarcianos[i][j].getvX()* -1);
                listaMarcianos[i][j].y+=15;//Hace que bajen.
            }
        }
    }
     private void pintaExplosiones( Graphics2D g2){
            //pinto las explosiones
        for (int i=0; i<listaExplosiones.size(); i++){
            Explosion e = listaExplosiones.get(i);
            e.setTiempoDeVida(e.getTiempoDeVida() - 1);
            if (e.getTiempoDeVida() > 25){
                g2.drawImage(e.imagenExplosion, listaMarcianos[posX][posY].x,listaMarcianos[posX][posY].y, null);
            }
            else {
                g2.drawImage(e.imagenExplosion2, listaMarcianos[posX][posY].x, listaMarcianos[posX][posY].y, null);
            }
            
             //si el tiempo de vida de la explosión es menor que 0 la elimino
            if (e.getTiempoDeVida() <= 0){
                listaExplosiones.remove(i);
            }
        }
}
    private void actualizaContadorTiempo(){
    contadorTiempo ++;
    if (contadorTiempo > 100){
        contadorTiempo = 0;
    }
}

    private void pintaMarcianos(Graphics2D _g2) {

        int anchoMarciano = listaMarcianos[0][0].imagen1.getWidth(null);
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (listaMarcianos[i][j].vivo) {
                    listaMarcianos[i][j].mueve();
                    //chequeo si el marciano ha chocado contra la pared para cambiar la dirección 
                    //de todos los marcianos
                    if (listaMarcianos[i][j].x + anchoMarciano == ANCHOPANTALLA || listaMarcianos[i][j].x == 0) {
                        direccionMarcianos = true;
                    }
                    if (contador < 50) {
                        _g2.drawImage(listaMarcianos[i][j].imagen1,
                                listaMarcianos[i][j].x,
                                listaMarcianos[i][j].y,
                                null);
                    } else if (contador < 100) {
                        _g2.drawImage(listaMarcianos[i][j].imagen2,
                                listaMarcianos[i][j].x,
                                listaMarcianos[i][j].y,
                                null);
                    } else {
                        contador = 0;
                    }
                }
            }
        }
        if (direccionMarcianos ){
            cambiaDireccionMarcianos();
            direccionMarcianos = false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel2.setPreferredSize(new java.awt.Dimension(80, 80));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 80, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(510, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(359, Short.MAX_VALUE))
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
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(true);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(true);
                break;
            case KeyEvent.VK_SPACE:
                miDisparo.posicionaDisparo(miNave);
                miDisparo.disparado = true;
                reproduceSonidos("/sonidos/sonido_disparo.wav");
                break;
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                miNave.setPulsadoIzquierda(false);
                break;
            case KeyEvent.VK_RIGHT:
                miNave.setPulsadoDerecha(false);
                break;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
