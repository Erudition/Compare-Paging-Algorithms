import java.util.Comparator;


	public class LRU implements Comparator<Page> {

		@Override
		public int compare(Page arg0, Page arg1) {
			int comparison = 0;
			if (!arg0.referenceBit && arg1.referenceBit) //only first page is referenced
				comparison = -1;								//return smaller, so it is on top
			else if (arg0.referenceBit && !arg1.referenceBit) //only second page is referenced
				comparison = 1;						//return larger, so other is on top
			return comparison;
		}
	}