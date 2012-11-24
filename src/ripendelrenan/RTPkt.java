/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ripendelrenan;

/**
 *
 * @author endel
 */
public class RTPkt {
  private int sourceId;
  private int destId;
  private int minCost[];

  public RTPkt(){ }
  
  public RTPkt(int sourceId, int destId, int minCost[]){
    this.sourceId = sourceId;
    this.destId = destId;
    this.minCost = minCost;
  }
  public int getSourceId() {
    return sourceId;
  }

  public void setSourceId(int sourceId) {
    this.sourceId = sourceId;
  }

  public int getDestId() {
    return destId;
  }

  public void setDestId(int destId) {
    this.destId = destId;
  }

  public int[] getMinCost() {
    return minCost;
  }

  public void setMinCost(int[] minCost) {
    this.minCost = minCost;
  }
}
