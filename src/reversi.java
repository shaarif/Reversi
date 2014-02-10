import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class reversi {
	static char[][] root=new char[8][8];
	static Map <Integer,Character> columnDictionary = new TreeMap<Integer,Character>();
	static PrintWriter writerLog;
	static PrintWriter writerPath;
	static int d=1;
	static int count=0;
	static int depth=0;
	static int flag=0;
	static int task=0;
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
	/*Process the input*/
    task=3;
    depth=7;
    String inputFile="/Users/shaarif/Desktop/CS561/Assignment/reversi/examples/1/input1.txt";
    String outputPath="/Users/shaarif/Desktop/CS561/Assignment/reversi/examples/1/output1_t3.txt";
    String outputTraverse="/Users/shaarif/Desktop/CS561/Assignment/reversi/examples/1/output1_t3_log.txt";
    /*Process the input File*/
    createColumnDictionary();
    processinputfile(inputFile);
    switch(task)
    {
    case 1:
    	minimax(outputPath,outputTraverse);
    	break;
    case 2:
    	alphabeta(outputPath,outputTraverse);
    	break;
    case 3:
    	alphabeta(outputPath,outputTraverse);
    }
	}
	/* Task 3 alpha beta with positional weight*/
	/* Task 2 alpha beta*/
	static void alphabeta(String outputPath, String outputTraverse) throws FileNotFoundException, UnsupportedEncodingException
	{
		writerLog = new PrintWriter(outputTraverse, "UTF-8");
		writerPath= new PrintWriter(outputPath, "UTF-8");
		char player='X';
		char opponent='O';
		int counter=1;
		writerPath.println("STEP = "+counter);
		writerPath.println("BLACK");
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				writerPath.print(root[i][j]);
			}
			writerPath.println();
		}
		writerPath.println();
		int val=0;
		val=Integer.MIN_VALUE;
		while(!isFull(root))
		{  
		    if(player=='X')
	    	opponent='O';
	        if(player=='O')
	    	opponent='X';
			int[] bestindex=new int[2];
			char[][] updatedroot=new char[8][8];
		    boardNode initialstate=new boardNode(root,val);
	        initialstate.depth=1;
	        initialstate.moveadded="root";
	        initialstate.alpha=Integer.MIN_VALUE;
	        initialstate.beta=Integer.MAX_VALUE;
	        if(count==0)writerLog.println("Node,Depth,Value,Alpha,Beta");
	        bestindex=alphaBetasimulation(initialstate,player);
	        if(count==0)writerLog.close();
	        count++;
		    counter++;
		    writerPath.println("STEP = "+counter);
	        if(bestindex[0]!=Integer.MAX_VALUE)
	        {
		    //take that move
		    root[bestindex[0]][bestindex[1]]=player;
		    //flip the entire root
		    updatedroot=flipMoves(root,bestindex[0],bestindex[1],player,opponent);
		    for(int i=0;i<8;i++)
		    {
		    	for(int j=0;j<8;j++)
		    	{
		    		root[i][j]=updatedroot[i][j];
		    	}
		    }
	        }
		    if(haveNoLegalMoves(root,opponent)==true)
		    {
		    	if(isFull(root))
		    	{
		    		if(opponent=='O')
				    	writerPath.println("WHITE");
				    else if(opponent=='X')
				    	writerPath.println("BLACK");
		    	}
		    	else
		    	{
		    	if(opponent=='O')
			    	writerPath.println("WHITE PASS");
			    else if(opponent=='X')
			    	writerPath.println("BLACK PASS");
		    	}
		    }
		    else{
		    if(opponent=='O')
		    	writerPath.println("WHITE");
		    else if(opponent=='X')
		    	writerPath.println("BLACK");
		    }
			for(int i=0;i<8;i++)
			{
				for(int j=0;j<8;j++)
				{
					writerPath.print(root[i][j]);
				}
				writerPath.println();
			}
			writerPath.println();
			if(player=='X')
				{ player='O';
				val=Integer.MAX_VALUE;
				}
			else
			{
				player='X';
				val=Integer.MIN_VALUE;
			}
		}
		writerPath.println("Game End");
		writerPath.close();
	}
	/*creates tree*/
	static int[] alphaBetasimulation(boardNode initialstate,char player)
	{
		ArrayList<String> moves=predictLegalMoves(initialstate.board,player);
		if(!moves.isEmpty())
		{
			alphabetaSimulation(initialstate,moves,player);   // creates the tree
		printAlphaBetaTraverse(initialstate,player);       //performs min max
		int[] bestindex=selectbestMove(initialstate,player);    //select the best move
    	return bestindex;
		}
		else
		{
			int[] bestindex={Integer.MAX_VALUE,Integer.MAX_VALUE};
			return bestindex;
		}
	}
	static void alphabetaSimulation(boardNode root,ArrayList<String> moves,char player)
	{
		char opponent=player;  //random values
		if(player=='X')
			opponent='O';
		else if(player=='O')
			opponent='X';
			if(root.depth<=depth)
		{
			for(String m:moves)
			{
				//create a new node
				boardNode newState=new boardNode();
				newState.moveadded=m;
				newState.depth=root.depth+1;
				if(player=='X')
					newState.value=Integer.MAX_VALUE;
				else
					newState.value=Integer.MIN_VALUE;
				newState.alpha=Integer.MIN_VALUE;
				newState.beta=Integer.MAX_VALUE;
				root.setChild(newState);
				//make a copy of game board
				char[][] copy=new char[8][8];
				for(int i=0;i<8;i++)
				{
					for(int j=0;j<8;j++)
					{
						copy[i][j]=root.board[i][j];
					}
				}
				//make the prospective move
				int[] index=getIndex(m);
				copy[index[0]][index[1]]=player;
				//flip the simulated piece
				char[][] temp=new char[8][8];
				temp=flipMoves(copy,index[0],index[1],player,opponent);
				//if newState's depth is equal to depth+1(4), then change its value
				if(newState.depth==depth+1)
				{
					if(task==2)   //for alpha beta pruning with evaluation function as number of pieces
					{
					newState.value=evalNumberOfPieces(temp);
					}
					if(task==3)    //for alpha beta pruning with evaluation function as positional weight
					{
					newState.value=evalPositionalWeight(temp);
					}
				}
				ArrayList<String> legalmoves=predictLegalMoves(temp,opponent);
				newState.board=temp;
				if(!legalmoves.isEmpty())
					alphabetaSimulation(newState,legalmoves,opponent);
				else
				{
					if(task==2)   //for alpha beta pruning with evaluation function as number of pieces
					{
					newState.value=evalNumberOfPieces(newState.board);
					}
					if(task==3)   //for alpha beta pruning with evaluation function as number of pieces
					{
					newState.value=evalPositionalWeight(newState.board);	
					}
				}
			}
		}
	}
	/* performs min max with alpha beta pruning*/
	static void printAlphaBetaTraverse(boardNode root,char player)
	{
		char opponent=player;
		if(root.depth==1 && count==0)
			writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value)+" "+getInfinity(root.alpha)+" "+getInfinity(root.beta));
		if(player=='X')
		{
			opponent='O';
			for(boardNode n: root.children)
			{
				if(root.alpha<root.beta)
				{
					n.alpha=root.alpha;
					n.beta=root.beta;
				if(n.depth!=depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value)+" "+getInfinity(n.alpha)+" "+getInfinity(n.beta));//going down the tree
				printAlphaBetaTraverse(n,opponent);     //going down the tree
				if(n.depth==depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value)+" "+getInfinity(n.alpha)+" "+getInfinity(n.beta));
				root.value=Math.max(root.value,n.value);    //going up the tree
				}
				root.alpha=Math.max(root.alpha,root.value);
				if(count==0 && root.alpha>=root.beta)//modified
					{ writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value)+" "+getInfinity(root.alpha)+" "+getInfinity(root.beta)+" CUT-OFF");
					  break;
					}
				if(count==0)
					writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value)+" "+getInfinity(root.alpha)+" "+getInfinity(root.beta));//going up the tree	
			}
		}
		else
		{
			opponent='X';
			for(boardNode n: root.children)
			{   if(root.alpha<root.beta)
				{
				n.alpha=root.alpha;
				n.beta=root.beta;
				if(n.depth!=depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value)+" "+getInfinity(n.alpha)+" "+getInfinity(n.beta));//going down the tree
				printAlphaBetaTraverse(n,opponent); //going down the tree
				if(n.depth==depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value)+" "+getInfinity(n.alpha)+" "+getInfinity(n.beta));
				root.value=Math.min(root.value,n.value);   //going up the tree
				}
			    root.beta=Math.min(root.beta,root.value);
			    if(count==0 && root.alpha>=root.beta)
				{		writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value)+" "+getInfinity(root.alpha)+" "+getInfinity(root.beta)+" CUT-OFF");
				break;
				}
			    if(count==0)
					writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value)+" "+getInfinity(root.alpha)+" "+getInfinity(root.beta)); //going up the tree
			}
		}
	}
	/* Task 1 minimax*/
	static void minimax(String outputPath,String outputTraverse) throws FileNotFoundException, UnsupportedEncodingException
	{
		writerLog = new PrintWriter(outputTraverse, "UTF-8");
		writerPath= new PrintWriter(outputPath, "UTF-8");
		char player='X';
		char opponent='O';
		int counter=1;
		writerPath.println("STEP = "+counter);
		writerPath.println("BLACK");
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				writerPath.print(root[i][j]);
			}
			writerPath.println();
		}
		writerPath.println();
		int val=0;
		val=Integer.MIN_VALUE;
		while(!isFull(root))
		{  
		    if(player=='X')
	    	opponent='O';
	        if(player=='O')
	    	opponent='X';
			int[] bestindex=new int[2];
			char[][] updatedroot=new char[8][8];
		    boardNode initialstate=new boardNode(root,val);
	        initialstate.depth=1;
	        initialstate.moveadded="root";
	        if(count==0)writerLog.println("Node,Depth,Value");
	        bestindex=simMoves(initialstate,player);
	        if(count==0)writerLog.close();
	        count++;
		    counter++;
		    writerPath.println("STEP = "+counter);
	        if(bestindex[0]!=Integer.MAX_VALUE)
	        {
		    //take that move
		    root[bestindex[0]][bestindex[1]]=player;
		    //flip the entire root
		    updatedroot=flipMoves(root,bestindex[0],bestindex[1],player,opponent);
		    for(int i=0;i<8;i++)
		    {
		    	for(int j=0;j<8;j++)
		    	{
		    		root[i][j]=updatedroot[i][j];
		    	}
		    }
	        }
		    if(haveNoLegalMoves(root,opponent)==true)
		    {
		    	if(isFull(root))
		    	{
		    		if(opponent=='O')
				    	writerPath.println("WHITE");
				    else if(opponent=='X')
				    	writerPath.println("BLACK");
		    	}
		    	else
		    	{
		    	if(opponent=='O')
			    	writerPath.println("WHITE PASS");
			    else if(opponent=='X')
			    	writerPath.println("BLACK PASS");
		    	}
		    }
		    else{
		    if(opponent=='O')
		    	writerPath.println("WHITE");
		    else if(opponent=='X')
		    	writerPath.println("BLACK");
		    }
			for(int i=0;i<8;i++)
			{
				for(int j=0;j<8;j++)
				{
					writerPath.print(root[i][j]);
				}
				writerPath.println();
			}
			writerPath.println();
			if(player=='X')
				{ player='O';
				val=Integer.MAX_VALUE;
				}
			else
			{
				player='X';
				val=Integer.MIN_VALUE;
			}
		}
		writerPath.println("Game End");
		writerPath.close();
	}
	/*creates tree*/
    static int[] simMoves(boardNode initialstate,char player)
    {
    	ArrayList<String> moves=predictLegalMoves(initialstate.board,player);
    	if(!moves.isEmpty())
    	{
    		simMoves(initialstate,moves,player);  //creates the tree
    	
    	printTraverseLog(initialstate,player);  //performs min max
    	int[] bestindex=selectbestMove(initialstate,player);    //select the best move
    	return bestindex;
        }
    	else
		{
			int[] bestindex={Integer.MAX_VALUE,Integer.MAX_VALUE};
			return bestindex;
		}
    }
	static void simMoves(boardNode root,ArrayList<String> moves,char player)
	{  
		char opponent=player;  //random values
		if(player=='X')
			opponent='O';
		else if(player=='O')
			opponent='X';
		if(root.depth<=depth)
		{
			for(String m:moves)
			{
				//create a new node
				boardNode newState=new boardNode();
				newState.moveadded=m;
				newState.depth=root.depth+1;
				if(player=='X')
					newState.value=Integer.MAX_VALUE;
				else
					newState.value=Integer.MIN_VALUE;
				root.setChild(newState);
				//make a copy of game board
				char[][] copy=new char[8][8];
				for(int i=0;i<8;i++)
				{
					for(int j=0;j<8;j++)
					{
						copy[i][j]=root.board[i][j];
					}
				}
				//make the prospective move
				int[] index=getIndex(m);
				copy[index[0]][index[1]]=player;
				//flip the simulated piece
				char[][] temp=new char[8][8];
				temp=flipMoves(copy,index[0],index[1],player,opponent);
				//if newState's depth is equal to depth+1(4), then change its value
				if(newState.depth==depth+1)
				{
					newState.value=evalNumberOfPieces(temp);
				}
				//simulate opponent's possible moves
				ArrayList<String> legalmoves=predictLegalMoves(temp,opponent);
				newState.board=temp;
				if(!legalmoves.isEmpty())
					simMoves(newState,legalmoves,opponent);
				else
					newState.value=evalNumberOfPieces(newState.board);
		}
		}
	}
	/*performs Min Max*/
	static void printTraverseLog(boardNode root,char player)
	{
		char opponent=player;
		if(root.depth==1 && count==0)
			writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value));
		if(player=='X')
		{
			opponent='O';
			for(boardNode n: root.children)
			{
				if(n.depth!=depth+1 && count==0)
				{
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value));
				}
				printTraverseLog(n,opponent);
				if(n.depth==depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value));
				root.value=Math.max(root.value,n.value);
				if(count==0)
				writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value));
			}
		}
		else
		{
			opponent='X';
			for(boardNode n: root.children)
			{
				if(n.depth!=depth+1 && count==0)
				{
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value));
				}
				printTraverseLog(n,opponent);
				if(n.depth==depth+1 && count==0)
					writerLog.println(n.moveadded+" "+n.depth+" "+getInfinity(n.value));
				root.value=Math.min(root.value,n.value);
				if(count==0)
				writerLog.println(root.moveadded+" "+root.depth+" "+getInfinity(root.value));
			}
		}
	}
	/*select the best move*/
	static int[] selectbestMove(boardNode root,char player)
	{   int[] bestindex=new int[2];
		ArrayList<String> possibleMoves=new ArrayList<String>();  //contains the list of all
		for(boardNode child:root.children)
		{
			//select all with the maximum value(equal to the root)
			if(child.value==root.value)
			{
				possibleMoves.add(child.moveadded);
			}
		}
		ArrayList<int[]> possibleMovesIndex=new ArrayList<int[]>();
		for(String s:possibleMoves)
		{
			possibleMovesIndex.add(getIndex(s));
		}
		if(possibleMovesIndex.size()==1)
		{   
			bestindex=possibleMovesIndex.get(0);
		}
		//check for tie breaker
		else
			bestindex=tiebreaker(possibleMovesIndex,root.board,player);
		return bestindex;
	}
	/*breaks the tie*/
	static int[] tiebreaker(ArrayList<int[]> possibleMovesIndex,char[][] board,char player)
	{
		int minrow=Integer.MAX_VALUE;
		int[] minMove=new int[2];
		for(int[] index: possibleMovesIndex)
		{
			if(index[0]<minrow)
			{
				minrow=index[0];
				minMove=index;
			}
		}
		return minMove;
	}
	/*returns infinty value*/
	static String getInfinity(float value)
	{
		if(value==Integer.MAX_VALUE)
			return "Infinity";
		else if(value==Integer.MIN_VALUE)
			return "-Infinity";
        return Float.toString(value);
	}
	/*Flips the values*/
	static char[][] flipMoves(char[][] temp,int i,int j,char player,char opponent)
	{
		char[][] original=new char[8][8];
		for(int a=0;a<8;a++)
		{
			for(int b=0;b<8;b++)
			{
				original[a][b]=temp[a][b];
			}
		}
		int k=1;
		int flag=0;
		if(original[i][j]==player)
		{
			//check for right horizontal
			flag=0;
			k=1;
			if(j+k<8)   //check for overflow condition
			{
			if(original[i][j+k]==opponent)    //check if adjacent is opponent
			{
				k++;
				if(j+k<8)
				{
				while(original[i][j+k]==opponent||original[i][j+k]==player)  //check if next to adjacent is white or black
				{
					if(original[i][j+k]==player)  //check if next to adjacent is player then break
					{
						flag=1;
						break;
					}
					k++;
					if(j+k>=8)
						break;
				}
				}
			}
			if(flag==1)
			{  for(int z=1;z<k;z++)
			{
				original[i][j+z]=player;
			}
			}
			}
			//check for left horizontal
			flag=0;
			k=1;
			if(j-k>=0)   //check for overflow condition
			{
			if(original[i][j-k]==opponent)    //check if adjacent is opponent
			{
				k++;
				if((j-k)>=0)
				{
				while(original[i][j-k]==opponent||original[i][j-k]==player)  //check if next to adjacent is white or black
				{
					if(original[i][j-k]==player)  //check if next to adjacent is player then break
					{
						flag=1;
						break;
					}
					k++;
					if(j-k<0)
						break;
				}
				}
			}
			if(flag==1)
			{
				for(int z=1;z<k;z++)
				{
					original[i][j-z]=player;
				}
			}
			}
			//check for downwards vertical
			flag=0;
			k=1;
			if(i+k<8)    //check for overflow condition
			{
			if(original[i+k][j]==opponent)   //check if below is opponent
			{
				k++;
				if(i+k<8)
				{
				while(original[i+k][j]==opponent||original[i+k][j]==player)   //check if below to below is white or black
				{
					if(original[i+k][j]==player)  //check if below to below is player then break
					{
						flag=1;
						break;
					}
					k++;
					if(i+k>=8)
						break;
				}
				}
			}
			if(flag==1)
			{
				for(int z=1;z<k;z++)
				{
					original[i+z][j]=player;
				}
			}
			}
			//check for upwards vertical
			flag=0;
			k=1;
			if(i-k>=0)    //check for overflow condition
			{
			if(original[i-k][j]==opponent)   //check if below is opponent
			{
				k++;
				if(i-k>=0)
				{
				while(original[i-k][j]==opponent||original[i-k][j]==player)   //check if below to below is white or black
				{
					if(original[i-k][j]==player)  //check if below to below is player then break
					{
						flag=1;
						break;
					}
					k++;
					if(i-k<0)
						break;
				}
				}
			}
			if(flag==1)
			{
                for(int z=1;z<k;z++)
				{
					original[i-z][j]=player;
				}
			}
			}
			//check for downwards left diagonal
			flag=0;
			k=1;
			if(i+k<8 && j+k<8)   //check for overflow condition
			{
			if(original[i+k][j+k]==opponent)    //check if diagonal is opponent
			{
				k++;
				if((i+k)<8 && (j+k)<8)
				{
				while(original[i+k][j+k]==opponent||original[i+k][j+k]==player)   //check if diagonal is white or black
				{   
					if(original[i+k][j+k]==player)     //check if diagonal is player then break
					{
						flag=1;
						break;
					}
					k++;
					if((i+k)>=8 || (j+k)>=8)
						break;
				}
				}
			}
			if(flag==1)
			{
				for(int z=1;z<k;z++)
				{
					original[i+z][j+z]=player;
				}
			}
			}
			//check for upwards left diagonal
			flag=0;
			k=1;
			if(i-k>=0 && j-k>=0)   //check for overflow condition
			{
			if(original[i-k][j-k]==opponent)    //check if diagonal is opponent
			{
				k++;
				if((i-k)>=0 && (j-k)>=0)
				{
				while(original[i-k][j-k]==opponent||original[i-k][j-k]==player)   //check if diagonal is white or black
				{
					if(original[i-k][j-k]==player)     //check if diagonal is player then break
					{
						flag=1;
						break;
					}
					k++;
					if((i-k)<0 || (j-k)<0)
						break;
				}
				}
			}
			if(flag==1)
			{
				for(int z=1;z<k;z++)
				{
					original[i-z][j-z]=player;
				}
			}
			}
			//check for downwards right diagonal
					flag=0;
					k=1;
					if(i+k<8 && j-k>=0)   //check for overflow condition
					{
					if(original[i+k][j-k]==opponent)    //check if diagonal is opponent
					{
						k++;
						if((i+k)<8 && (j-k)>=0)
						{
						while(original[i+k][j-k]==opponent||original[i+k][j-k]==player)   //check if diagonal is white or black
						{   
							if(original[i+k][j-k]==player)     //check if diagonal is player then break
							{
								flag=1;
								break;
							}
							k++;
							if((i+k)>=8 || (j-k)<0)
								break;
						}
						}
					}
					if(flag==1)
					{
						for(int z=1;z<k;z++)
						{
							original[i+z][j-z]=player;
						}
					}
					}
					//check for upwards right diagonal
					flag=0;
					k=1;
					if(i-k>=0 && j+k<8)   //check for overflow condition
					{
					if(original[i-k][j+k]==opponent)    //check if diagonal is opponent
					{
						k++;
						if((i-k)>=0 && (j+k)<8)
						{
						while(original[i-k][j+k]==opponent||original[i-k][j+k]==player)   //check if diagonal is white or black
						{
							if(original[i-k][j+k]==player)     //check if diagonal is player then break
							{
								flag=1;
								break;
							}
							k++;
							if((i-k)<0 || (j+k)>=8)
								break;
						}
						}
					}
					if(flag==1)
					{
						for(int z=1;z<k;z++)
						{
							original[i-z][j+z]=player;
						}
					}
					}
		}
			return original;
	}
	/*checks if the board is Full*/
	static boolean isFull(char[][] boardstate)
	{
		int count=0;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(boardstate[i][j]=='*')
					count++;
			}
		}
		if(count==0)
			return true;
		else
			return false;
	}
	/*converts the legal move into index of matrix*/
	static int[] getIndex(String move)
	{ 
		int index[]=new int[2];
		char[] movearray=move.toCharArray();
		index[1]=getKeyInDictionary(movearray[0]);  //stores the column
		index[0]=movearray[1]-49;  //stores the row; 49 is the ascii for one
		return index;
	}
	/*Return index for a row char*/
	public static int getKeyInDictionary(char column)
	{
		for (Entry<Integer,Character> entry : columnDictionary.entrySet()) {
		     if(entry.getValue()==column)
		    	 return entry.getKey();
		}
		return Integer.MAX_VALUE;
	}
	/*predicting possible moves for a given board state and for a given player*/
	static ArrayList<String> predictmove(char[][] boardstate,int i,int j,char player,char opponent)
	{   int flag,k;
	    String move;
	    ArrayList<String> legalmoves=new ArrayList<String>();
		//check for right horizontal
		flag=0;
		k=1;
		if(j+k<8)   //check for overflow condition
		{
		if(boardstate[i][j+k]==opponent)    //check if adjacent is opponent
		{
			k++;
			if(j+k<8)
			{
			while(boardstate[i][j+k]==opponent||boardstate[i][j+k]==player)  //check if next to adjacent is white or black
			{
				if(boardstate[i][j+k]==player)  //check if next to adjacent is player then break
				{
					flag=1;
					break;
				}
				k++;
				if(j+k>=8)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for left horizontal
		flag=0;
		k=1;
		if(j-k>=0)   //check for overflow condition
		{
		if(boardstate[i][j-k]==opponent)    //check if adjacent is opponent
		{
			k++;
			if((j-k)>=0)
			{
			while(boardstate[i][j-k]==opponent||boardstate[i][j-k]==player)  //check if next to adjacent is white or black
			{
				if(boardstate[i][j-k]==player)  //check if next to adjacent is player then break
				{
					flag=1;
					break;
				}
				k++;
				if(j-k<0)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for downwards vertical
		flag=0;
		k=1;
		if(i+k<8)    //check for overflow condition
		{
		if(boardstate[i+k][j]==opponent)   //check if below is opponent
		{
			k++;
			if(i+k<8)
			{
			while(boardstate[i+k][j]==opponent||boardstate[i+k][j]==player)   //check if below to below is white or black
			{
				if(boardstate[i+k][j]==player)  //check if below to below is player then break
				{
					flag=1;
					break;
				}
				k++;
				if(i+k>=8)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for upwards vertical
		flag=0;
		k=1;
		if(i-k>=0)    //check for overflow condition
		{
		if(boardstate[i-k][j]==opponent)   //check if below is opponent
		{
			k++;
			if(i-k>=0)
			{
			while(boardstate[i-k][j]==opponent||boardstate[i-k][j]==player)   //check if below to below is white or black
			{
				if(boardstate[i-k][j]==player)  //check if below to below is player then break
				{
					flag=1;
					break;
				}
				k++;
				if(i-k<0)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for downwards left diagonal
		flag=0;
		k=1;
		if(i+k<8 && j+k<8)   //check for overflow condition
		{
		if(boardstate[i+k][j+k]==opponent)    //check if diagonal is opponent
		{
			k++;
			if((i+k)<8 && (j+k)<8)
			{
			while(boardstate[i+k][j+k]==opponent||boardstate[i+k][j+k]==player)   //check if diagonal is white or black
			{   
				if(boardstate[i+k][j+k]==player)     //check if diagonal is player then break
				{
					flag=1;
					break;
				}
				k++;
				if((i+k)>=8 || (j+k)>=8)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for upwards left diagonal
		flag=0;
		k=1;
		if(i-k>=0 && j-k>=0)   //check for overflow condition
		{
		if(boardstate[i-k][j-k]==opponent)    //check if diagonal is opponent
		{
			k++;
			if((i-k)>=0 && (j-k)>=0)
			{
			while(boardstate[i-k][j-k]==opponent||boardstate[i-k][j-k]==player)   //check if diagonal is white or black
			{
				if(boardstate[i-k][j-k]==player)     //check if diagonal is player then break
				{
					flag=1;
					break;
				}
				k++;
				if((i-k)<0 || (j-k)<0)
					break;
			}
			}
		}
		if(flag==1)
		{
			move=columnDictionary.get(j)+Integer.toString(i+1);
			if(!legalmoves.contains(move))
			legalmoves.add(move);
		}
		}
		//check for downwards right diagonal
				flag=0;
				k=1;
				if(i+k<8 && j-k>=0)   //check for overflow condition
				{
				if(boardstate[i+k][j-k]==opponent)    //check if diagonal is opponent
				{
					k++;
					if((i+k)<8 && (j-k)>=0)
					{
					while(boardstate[i+k][j-k]==opponent||boardstate[i+k][j-k]==player)   //check if diagonal is white or black
					{   
						if(boardstate[i+k][j-k]==player)     //check if diagonal is player then break
						{
							flag=1;
							break;
						}
						k++;
						if((i+k)>=8 || (j-k)<0)
							break;
					}
					}
				}
				if(flag==1)
				{
					move=columnDictionary.get(j)+Integer.toString(i+1);
					if(!legalmoves.contains(move))
					legalmoves.add(move);
				}
				}
				//check for upwards right diagonal
				flag=0;
				k=1;
				if(i-k>=0 && j+k<8)   //check for overflow condition
				{
				if(boardstate[i-k][j+k]==opponent)    //check if diagonal is opponent
				{
					k++;
					if((i-k)>=0 && (j+k)<8)
					{
					while(boardstate[i-k][j+k]==opponent||boardstate[i-k][j+k]==player)   //check if diagonal is white or black
					{
						if(boardstate[i-k][j+k]==player)     //check if diagonal is player then break
						{
							flag=1;
							break;
						}
						k++;
						if((i-k)<0 || (j+k)>=8)
							break;
					}
					}
				}
				if(flag==1)
				{
					move=columnDictionary.get(j)+Integer.toString(i+1);
					if(!legalmoves.contains(move))
					legalmoves.add(move);
				}
				}
				return legalmoves;
	}
	static ArrayList<String> predictLegalMoves(char[][] boardstate,char player)
	{
		ArrayList<String> legalmoves=new ArrayList<String>();
		char opponent = player;   //random values
		if(player=='X')
		{	
			opponent='O';
		}
		else if(player=='O')
		{
			opponent='X';
		}
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(boardstate[i][j]=='*')
				{
					for(String s:predictmove(boardstate,i,j,player,opponent))
					{
						if(!legalmoves.contains(s))
							legalmoves.add(s);
					}
				}
			}
		}
		return legalmoves;
	}
	//checks if it has legal moves
	static boolean haveNoLegalMoves(char[][] boardstate,char player)
	{
		ArrayList<String> legalmove=new ArrayList<String>();
		legalmove=predictLegalMoves(boardstate,player);
		if(legalmove.isEmpty())
			return true;
		return false;
	}
	/*Evaluation Function Number Of Pieces*/
	static int evalNumberOfPieces(char[][] boardstate)
	{
		int numOfblacks=0;
		int numOfwhites=0;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(boardstate[i][j]=='X')
					numOfblacks++;
				else if(boardstate[i][j]=='O')
					numOfwhites++;
			}
		}
		return(numOfblacks-numOfwhites);
	}
    /*Evaluation Function Positional Weight*/
	static int evalPositionalWeight(char[][] boardstate)
	{
		int[][] posWeight=new int[8][8];
		/*hard coding the weights*/
		posWeight[0][0]=posWeight[0][7]=posWeight[7][0]=posWeight[7][7]=99;
		posWeight[0][1]=posWeight[0][6]=posWeight[1][0]=posWeight[1][7]=posWeight[6][0]=posWeight[6][7]=posWeight[7][1]=posWeight[7][6]=-8;
		posWeight[0][2]=posWeight[0][5]=posWeight[2][0]=posWeight[2][7]=posWeight[5][0]=posWeight[5][7]=posWeight[7][2]=posWeight[7][5]=8;
		posWeight[0][3]=posWeight[0][4]=posWeight[3][0]=posWeight[3][7]=posWeight[4][0]=posWeight[4][7]=posWeight[7][3]=posWeight[7][4]=6;
		posWeight[1][1]=posWeight[1][6]=posWeight[6][1]=posWeight[6][6]=-24;
		posWeight[1][2]=posWeight[1][5]=posWeight[2][1]=posWeight[2][6]=posWeight[5][1]=posWeight[5][6]=posWeight[6][2]=posWeight[6][5]=-4;
		posWeight[1][3]=posWeight[1][4]=posWeight[3][1]=posWeight[3][6]=posWeight[4][1]=posWeight[4][6]=posWeight[6][3]=posWeight[6][4]=-3;
		posWeight[2][2]=posWeight[2][5]=posWeight[5][2]=posWeight[5][5]=7;
		posWeight[2][3]=posWeight[2][4]=posWeight[3][2]=posWeight[3][5]=posWeight[4][2]=posWeight[4][5]=posWeight[5][3]=posWeight[5][4]=4;
		posWeight[3][3]=posWeight[3][4]=posWeight[4][3]=posWeight[4][4]=0;
		int blacks=0;
		int whites=0;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(boardstate[i][j]=='X')
					blacks=blacks+posWeight[i][j];
				else if(boardstate[i][j]=='O')
					whites=whites+posWeight[i][j];
			}
		}
		return (blacks-whites);
	}
	/*Process input file*/
	static void processinputfile(String path)
	{
		BufferedReader br;
		try{
			br= new BufferedReader( new FileReader(path));
			String line;
			int i=0,j=0;
	        while ((line = br.readLine()) != null) {
	        	char[] charArray = line.toCharArray();
	        	j=0;
	        	while(j<charArray.length)
	        	{
	        		root[i][j]=charArray[j];
	        		j++;
	        	}
	            i++;
	        }
	        br.close();
		}catch (Exception e)
		{
			System.out.println(e);
		}
	}
	static void createColumnDictionary()
	{
		int i=0;
		int j=97;
		while(i<8)
		{
		columnDictionary.put(i, (char)j);
		i++;
		j++;
		}
	}
}
