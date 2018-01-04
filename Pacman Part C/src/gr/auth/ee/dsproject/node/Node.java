package gr.auth.ee.dsproject.node;

import gr.auth.ee.dsproject.pacman.Room;
import gr.auth.ee.dsproject.pacman.PacmanUtilities;
import java.util.ArrayList;

public class Node
{
  Room[][] room;
  int nodeX;
  int nodeY;
  int depth;
  int nodeMove;
  double min;
  double max;
  double nodeEvaluation;
  int[][] currentGhostPos;
  int[][] flagPos;
  boolean[] currentFlagStatus;
  ArrayList<Integer> flags=new ArrayList<Integer>();
  ArrayList<Integer> ghosts=new ArrayList<Integer>();
  

  Node parent;
  ArrayList<Node> children = new ArrayList<Node>();

  // Constructor
  public Node ()
  {
	  nodeX=0;
	  nodeY=0;
	  nodeMove=0;
	  room=null;	  
	  currentGhostPos=null;
	  flagPos=null;
	  currentFlagStatus=null;
	  flags=null;
	  ghosts=null;
	  e=(nodeMove+1)%2;
	  f=nodeY*e+nodeX*(nodeMove%2);
	  g=1;
	  if(nodeMove==0||nodeMove==3) {
	  	g=-1;
	  }
	  nodeEvaluation=0;
  }
  
  public Node (int nX,int nY,int nM,int dth,Room[][] maze) 
  {   
	  nodeX=nX;
	  nodeY=nY;
	  nodeMove=nM;
	  depth=dth;
	  room=maze;	  
	  currentGhostPos=this.findGhosts(room);
	  flagPos=this.findFlags(room);
	  currentFlagStatus=this.checkFlags(room);
	  flags=this.realFlagPos();
	  e=(nodeMove+1)%2;
	  f=nodeY*e+nodeX*(nodeMove%2);
	  g=1;
	  if(nodeMove==0||nodeMove==3) {
	  	g=-1;
	  }
	  ghosts=this.harmfulGhostPos();
	  nodeEvaluation=this.evaluate();
  }
//Δημιουργεί μια ArrayList με τους δείκτες (0 μεχρι PacmanUtilities.numberOfFlags-1) των σημαιών που δεν έχουν πιαστεί
  
public void setMin (double a) {
	min=a;
}
 
public void setMax (double b) {
	max=b;
}
public double getMinOptions() {
	return max;
}
  
public double getMaxOptions() {
	return min;
}

public void setParent (Node parent) {
	  this.parent=parent;
  }
  
  public void setChildren(Node child) {
	  children.add(child);
  }
  
  public void setNodeMove(int nodemove) {
	  nodeMove=nodemove;
  } 
//Επιστρέφει την κίνηση του αντικειμένου
  public int getMove() {
	  return nodeMove;
  }
  
  //Επιστρέφει την αξιολόγηση της κίνησης του αντικειμένου
  public double getEvaluation() {

	  return nodeEvaluation;
  }
  

  
  public int getNodeX () {
	  return nodeX;
  }

  public int getNodeY () {
	  return nodeY;
  }

  public Node getParent() {
	  return parent;
  }
  public ArrayList<Node> getChildren() {
	  return children;
  }
  
  public int[][] getGhostsPosition(){
	  return this.findGhosts(room);
  }
  
  
  
  private ArrayList<Integer> realFlagPos(){
	  ArrayList<Integer> flag=new ArrayList<Integer>();
	  for(int i=0;i<PacmanUtilities.numberOfFlags;i++) {
		  if(!currentFlagStatus[i]) {
			  flag.add(i);		  
		  }
	  }
	  return flag;
  }
  
//Δημιουργεί μια ArayList με τους δείκτες (0 μεχρι PacmanUtilities.numberOfGhosts-1) των φαντασμάτων που μπορούν να μειώσουν την nodeEvaluation.Περισσότερες λεπτομέρειες στην αναφορά.
 
  private ArrayList<Integer> harmfulGhostPos(){
	  ArrayList<Integer> ghost=new ArrayList<Integer>();	  
	  for(int i=0;i<PacmanUtilities.numberOfGhosts;i++) {
		  ghost.add(i);
		  if(currentGhostPos[i][1-e]==nodeY*(nodeMove%2)+nodeX*e) {
		    for(int j=0;j<PacmanUtilities.numberOfFlags;j++) {
		    	if((currentGhostPos[i][e]-flagPos[j][e])*(f-flagPos[j][e])<0&&flagPos[j][1-e]==nodeY*(nodeMove%2)+nodeX*e) {
		    		ghost.remove(ghost.size()-1);
		    		break;
		    	}
		    }
	      } 
	  }
	  return ghost;
  }
  
  
  
  private int[][] findGhosts (Room[][] Maze)

  {
	  int sum=0;  
		int[][] ghostPos=new int[PacmanUtilities.numberOfGhosts][2];
	    for(int i=0;i<PacmanUtilities.numberOfRows;i++) {
	    	for(int j=0;j< PacmanUtilities.numberOfColumns;j++) {
	    		if (room[i][j].isGhost()) {
	    		   ghostPos[sum][0]=i;
	    		   ghostPos[sum][1]=j;
	    		   sum++;
	    		}
	    	}
	    }
	    return ghostPos;
  }
  
  private int[][] findFlags (Room[][] Maze)
  {
	  int sum=0;  
		int[][] flagPos=new int[PacmanUtilities.numberOfFlags][2];
	    for(int i=0;i<PacmanUtilities.numberOfRows;i++) {
	    	for(int j=0;j< PacmanUtilities.numberOfColumns;j++) {
	    		if (room[i][j].isFlag()) {
	    			flagPos[sum][0]=i;
	    			flagPos[sum][1]=j;
	    		   sum++;
	    		}
	    	}
	    }
	    return flagPos; 
  }

  private boolean[] checkFlags (Room[][] Maze)
  {
	  boolean[] isFlagGone=new boolean[4];  
	    for(int i=0;i<PacmanUtilities.numberOfFlags;i++) {   	
	       isFlagGone[i]=room[flagPos[i][0]][flagPos[i][1]].isCapturedFlag();
	    }
	    return isFlagGone;
  }
  
  int e;
  int f;
  int g;
  
  private double evaluate ()
  {
	  {
		  double evaluation = 0; 
		  //Μία επανάληψη για κάθε φάντασμα που μας απειλεί
		  for(int i=0;i<ghosts.size();i++) {
			  //h είναι η απόσταση του i φαντάσματος από τον pacman
			  double h=Math.sqrt(Math.pow(Math.abs(currentGhostPos[ghosts.get(i)][0]-nodeX),2)+Math.pow(Math.abs(currentGhostPos[ghosts.get(i)][1]-nodeY),2));
			  double j=currentGhostPos[ghosts.get(i)][e]-f;
			  //Αν το φάντασμα i βρίσκεται στη θέση που πάμε να κινηθούμε
			  if((currentGhostPos[ghosts.get(i)][0]==(nodeX+(nodeMove%2)*(2-nodeMove)))&&(currentGhostPos[ghosts.get(i)][1]==(nodeY+(Math.pow(-1,(nodeMove/2)))*((nodeMove%2)-1)))){
		      	  evaluation=evaluation-200;
		      }else
		    	  //Αν το i φάντασμα βρίσκεται σε γειτονική θέση της θέσης που πάμε να κινηθούμε
		      if(((currentGhostPos[ghosts.get(i)][0]==(nodeX+(nodeMove%2)*(2-nodeMove))&&Math.abs(currentGhostPos[ghosts.get(i)][1]-(nodeY+(Math.pow(-1,(nodeMove/2)))*((nodeMove%2)-1)))==1)||((Math.abs(currentGhostPos[ghosts.get(i)][0]-(nodeX+(nodeMove%2)*(2-nodeMove)))==1)&&currentGhostPos[ghosts.get(i)][1]==(nodeY+(Math.pow(-1,(nodeMove/2)))*((nodeMove%2)-1))))||((currentGhostPos[ghosts.get(i)][0]==(nodeX+2*(nodeMove%2)*(2-nodeMove)))&&(currentGhostPos[ghosts.get(i)][1]==(nodeY+2*(Math.pow(-1,(nodeMove/2)))*((nodeMove%2)-1))))){
		          int temp=0;
		          //Μια επανάληψη για κάθε σημαία
		    	  for(int k=0;k<PacmanUtilities.numberOfFlags;k++) {
		    		  //Αν η σημαία (πιασμένη η όχι) k βρίσκεται στη θέση που πάμε να κινηθούμε
		        	  if((flagPos[k][0]==(nodeX+(nodeMove%2)*(2-nodeMove)))&&(flagPos[k][1]==(nodeY+(Math.pow(-1,(nodeMove/2)))*((nodeMove%2)-1)))) {
		        		  temp=1;
		        		  break;
		        	  }
		          }
		    	  //Αν δεν βρέθηκε σημαία (πιασμένη η όχι) στη θέση που πάμε να κινηθούμε
		          if (temp!=1) {
		    	  evaluation=evaluation-100;
		    	  }
		      }else	
		    	  //Αν το i φάντασμα δεν απειλεί άμεσα τον pacman αλλά βρίσκεται προς την κατεύθυνση που πάμε να κινηθούμε και υπάρχουν πανω απο 2 σημαίες διαθέσιμες
		            if(g*j>0 && flags.size()>2) {
		            	//Αφαιρούμε από την evaluation ένα ποσό αντιστρόφως ανάλογο της απόστασης του φαντάσματος
		      	        evaluation=evaluation-30/(ghosts.size()*h);    //Οι συντελεστές μπήκαν εμπειρικά       
		            }
		  }
		//Μια επανάληψη για κάθε σημαία που δεν έχει πιαστεί
		    for(int i=0;i<flags.size();i++) {
		      //h τωρα είναι η απόσταση της i σημαίας από τον pacman
		      double h=Math.sqrt(Math.pow(Math.abs(flagPos[flags.get(i)][0]-nodeX),2)+Math.pow(Math.abs(flagPos[flags.get(i)][1]-nodeY),2));
		      double j=flagPos[flags.get(i)][e]-f;
		      //Αν η i σημαία βρίσκεται προς την κατεύθυνση που πάμε να κινηθούμε
		      if(g*j>0) {
		    	  //Προστείθεται στην evaluation ένα ποσό αντιστρόφως ανάλογο της απόστασης της σημαίας
		    	  evaluation=evaluation+20+Math.abs(j)/(Math.pow(h,2))+70/(flags.size()*Math.pow(h,2)); //Οι συντελεστές μπήκαν εμπειρικά 
		      }
		    }
		    
		    if(evaluation<-100) {
		    	evaluation=-100;
		    }
		    if(evaluation>100) {
		    	evaluation=100;
		    }
		      
		    return evaluation;

		  }
   

  }

}
