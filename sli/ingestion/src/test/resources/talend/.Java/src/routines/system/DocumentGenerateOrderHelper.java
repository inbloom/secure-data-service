package routines.system;

/**
 * the class for advanced xml components,and it control the generate order of group or loop xml element.
 * @author Administrator
 * 
 */
public class DocumentGenerateOrderHelper {

	/*
	 * store the order status for XML UI tree
	 * the array size mean the count of all group and loop elements in the UI tree.
	 * the array index mean a group or loop element in the UI tree.
	 * the array value mean the insert location of the current group or loop element in the UI tree.
	 */
	private int[] orders = null;
	
	private int currentIndex = 0;
	
	public DocumentGenerateOrderHelper(int size) {
		orders = new int[size];
	}
	
	private void updateLocationStatus(int index,int value) {
		currentIndex = index;
		//set the current node order 
		if(orders[currentIndex] == 0) {
			orders[currentIndex] = value;
		}
		//remove the order status of the child of the current node 
		if(currentIndex + 1 < orders.length) {
			orders[currentIndex + 1] = 0;
		}
	}
	
	public int getInsertLocation(int index,int value) {
		updateLocationStatus(index,value);
		//append for the same node
		return orders[currentIndex]++;
	} 
	
	public static void main(String[] args) {
		DocumentGenerateOrderHelper a = new DocumentGenerateOrderHelper(2);
		//the parameters come from UI information.
		System.out.println(a.getInsertLocation(0, 2));
		System.out.println(a.getInsertLocation(0, 2));
		System.out.println(a.getInsertLocation(0, 2));
	}
}
