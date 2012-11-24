/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ripendelrenan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author endel
 */
public class NoTres extends Thread {

  protected static final int ID_NO = 3;
  private int vetorDistancia[];
  private Socket socketOut;
  private Socket socketIn;
  private ServerSocket serverSocket;

  public NoTres() throws UnknownHostException, IOException {
    try {
      Thread t = new Thread(this);
      t.start();

      rtInit3();

    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public void run() {
    try {
      serverSocket = new ServerSocket(50003);
      while (true) {
        socketIn = serverSocket.accept();
        DataInputStream in = new DataInputStream(socketIn.getInputStream());

        RTPkt pacote = new RTPkt();

        pacote.setSourceId(in.readInt());
        pacote.setDestId(in.readInt());

        int aux[] = new int[4];
        for (int i = 0; i < 4; i++) {
          aux[i] = in.readInt();
        }
        
        pacote.setMinCost(aux);

        rtUpdate3(pacote);

        /*if (trace > 2) {
         System.out.println("Aceitei uma conexao!\n");
         System.out.println("No 0 escutando");
         }*/
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public void rtInit3() throws InterruptedException {
    int aux[] = {7,999,2,0};
    vetorDistancia = aux;
    System.out.println("init 3");
    sleep(1000);
//sleep((int)Math.random()*5); 
    // Atualizar os nos vizinhos
    toLayer2(new RTPkt());
  }

  // RECEBER atualizacao dos vizinhos
  public void rtUpdate3(RTPkt rcvdpkt) {

    int aux[] = rcvdpkt.getMinCost();
    boolean atualizou = false;

    // Recebendo o vetor do no 0
    if (rcvdpkt.getSourceId() == 0) {
      // Verifico se a minha distancia pro no 2 eh MENOR que a distancia que recebi
      // do no 0 + minha distancia pro no 0
      if (vetorDistancia[1] > aux[1] + vetorDistancia[0]) {
          vetorDistancia[1] = aux[1] + vetorDistancia[0];
        atualizou = true;
      }
      // Mesma coisa, mas para o no 3
      if (vetorDistancia[2] > aux[2] + vetorDistancia[0]) {
          vetorDistancia[2] = aux[2] + vetorDistancia[0];
        atualizou = true;
      }
    }
    // Recebendo o vetor do no 2
    if (rcvdpkt.getSourceId() == 2) {
      // Verifico se a minha distancia pro no 1 eh MENOR que a distancia que recebi
      // do no 3 + minha distancia pro no 3
      if (vetorDistancia[1] > aux[1] + vetorDistancia[2]) {
          vetorDistancia[1] = aux[1] + vetorDistancia[2];
        atualizou = true;
      }
      // Mesma coisa, mas para o no 2
      if (vetorDistancia[0] > aux[0] + vetorDistancia[2]) {
          vetorDistancia[0] = aux[0] + vetorDistancia[2];
        atualizou = true;
      }
    }
    if (atualizou) {
      String msg = "SOURCE: "+rcvdpkt.getSourceId()+" ";
      msg += "DEST: "+rcvdpkt.getDestId()+" MEU VETOR: ";
      for(int i = 0; i< 4; i++)
        msg += vetorDistancia[i]+" ";
      System.out.println(msg);
      
      toLayer2(new RTPkt());
    }
  }

  // ENVIAR atualizacao para os vizinhos
  public void toLayer2(RTPkt rcvdpkt) {

    rcvdpkt.setMinCost(vetorDistancia);
    rcvdpkt.setSourceId(ID_NO);
    rcvdpkt.setDestId(0);

    try {
      socketOut = new Socket("localhost", 50000);
      DataOutputStream out = new DataOutputStream(socketOut.getOutputStream());
      // escrevo na saida:
      // DE-PARA-Vetor de distancia
      out.writeInt(rcvdpkt.getSourceId());
      out.writeInt(rcvdpkt.getDestId());
      for (int i = 0; i < 4; i++) {
        out.writeInt(vetorDistancia[i]);
      }
      socketOut.close();

      // =======================================================================
      // Criando Socket para enviar para 2

      socketOut = new Socket("localhost", 50002);
      rcvdpkt.setDestId(2);
      out = new DataOutputStream(socketOut.getOutputStream());
      // escrevo na saida:
      // DE-PARA-Vetor de distancia
      out.writeInt(rcvdpkt.getSourceId());
      out.writeInt(rcvdpkt.getDestId());
      for (int i = 0; i < 4; i++) {
        out.writeInt(vetorDistancia[i]);
      }


    } catch (Exception e) {
            System.out.println(e.getMessage());

    }



  }
}
