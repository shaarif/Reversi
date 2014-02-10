import java.util.ArrayList;


public class boardNode {
char[][] board=new char[8][8];
float value;
float alpha,beta;
int depth;
ArrayList<boardNode> children=new ArrayList<boardNode>();
String moveadded;
boardNode(char[][] state,int val)
{
	value=val;
	for(int i=0;i<8;i++)
	{
		for(int j=0;j<8;j++)
		{
			board[i][j]=state[i][j];
		}
	}
}
public boardNode() {
}
public void setChild(boardNode child)
{
	this.children.add(child);
}
}
