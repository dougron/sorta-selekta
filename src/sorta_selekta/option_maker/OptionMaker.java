package sorta_selekta.option_maker;
import java.util.ArrayList;

public class OptionMaker {
	
	// returns an ArrayList<ArrayList<Integer>> of every combination of options, not
	// including repeats, of all the number from 0 to size - 1

	public static ArrayList<ArrayList<Integer>> getOptionList(int size){
		ArrayList<ArrayList<Integer>> aList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < size; i++){
			ArrayList<Integer> nList = new ArrayList<Integer>();
			nList.add(i);
			aList.add(nList);
 			
		}
		for (int j = 1; j < size; j++){
			addNewItem(aList, size, j);
		}
		return aList;
	}
	private static void addNewItem(ArrayList<ArrayList<Integer>> aList, int max, int targetLength){
		ArrayList<ArrayList<Integer>> pList = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> nList: aList){
			if (nList.size() == targetLength){
				for (int i = nList.get(nList.size() - 1) + 1; i < max; i ++){
					ArrayList<Integer> xList = new ArrayList<Integer>();
					for (int x: nList){
						xList.add(x);
					}
					xList.add(i);
					pList.add(xList);
				}
			}			
		}
		aList.addAll(pList);
	}
}
