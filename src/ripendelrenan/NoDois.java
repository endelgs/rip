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
public class NoDois extends Thread {

  protected static final int ID_NO = 2;
  private int vetorDistancia[];
  private Socket socketOut;
  private Socket socketIn;
  private ServerSocket serverSocket;

  public NoDois() throws UnknownHostException, IOException {
    try {
      Thread t = new Thread(this);
      t.start();

      rtInit2();

    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @Override
  public void run() {
    try {
      serverSocket = new ServerSocket(50002);
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

        rtUpdate2(pacote);

        /*if (trace > 2) {
         System.out.println("Aceitei uma conexao!\n");
         System.out.println("No 0 escutando");
         }*/
      }
    } catch (Exception e) {
            System.out.println(e.getMessage());

    }
  }

  public void rtInit2() throws InterruptedException {
    int aux[] = {3,1,0,2};
    vetorDistancia = aux;
        

    sleep(1000);    
    // Atualizar os nos vizinhos
    toLayer2(new RTPkt());
  }

  // RECEBER atualizacao dos vizinhos
  public void rtUpdate2(RTPkt rcvdpkt) {

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
      if (vetorDistancia[3] > aux[3] + vetorDistancia[0]) {
          vetorDistancia[3] = aux[3] + vetorDistancia[0];
        atualizou = true;
      }
    }

    // Recebendo o vetor do no 1
    if (rcvdpkt.getSourceId() == 1) {
      // Verifico se a minha distancia pro no 0 eh MENOR que a distancia que recebi
      // do no 2 + minha distancia pro no 2
      if (vetorDistancia[0] > aux[0] + vetorDistancia[1]) {
          vetorDistancia[0] = aux[0] + vetorDistancia[1];
        atualizou = true;
      }
      // Mesma coisa, mas para o no 3
      if (vetorDistancia[3] > aux[3] + vetorDistancia[1]) {
          vetorDistancia[3] = aux[3] + vetorDistancia[1];
        atualizou = true;
      }
    }
    // Recebendo o vetor do no 3
    if (rcvdpkt.getSourceId() == 3) {
      // Verifico se a minha distancia pro no 1 eh MENOR que a distancia que recebi
      // do no 3 + minha distancia pro no 3
      if (vetorDistancia[1] > aux[1] + vetorDistancia[3]) {
          vetorDistancia[1] = aux[1] + vetorDistancia[3];
        atualizou = true;
      }
      // Mesma coisa, mas para o no 2
      if (vetorDistancia[2] > aux[2] + vetorDistancia[3]) {
          vetorDistancia[2] = aux[2] + vetorDistancia[3];
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
      // Criando Socket para enviar para 1

      socketOut = new Socket("localhost", 50001);
      rcvdpkt.setDestId(1);
      out = new DataOutputStream(socketOut.getOutputStream());
      // escrevo na saida:
      // DE-PARA-Vetor de distancia
      out.writeInt(rcvdpkt.getSourceId());
      out.writeInt(rcvdpkt.getDestId());
      for (int i = 0; i < 4; i++) {
        out.writeInt(vetorDistancia[i]);
      }
      // =======================================================================
      // Criando Socket para enviar para 3
      socketOut = new Socket("localhost", 50003);
      rcvdpkt.setDestId(3);
      out = new DataOutputStream(socketOut.getOutputStream());
      // escrevo na saida:
      // DE-PARA-Vetor de distancia
      out.writeInt(rcvdpkt.getSourceId());
      out.writeInt(rcvdpkt.getDestId());
      for (int i = 0; i < 4; i++) {
        out.writeInt(vetorDistancia[i]);
      }


    } catch (Exception e) {
            System.out.println(e.getMessage()+" 2");

    }



  }
}
