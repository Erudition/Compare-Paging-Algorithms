import java.util.Comparator;


	public class MFU implements Comparator<Page> {

		@Override
		public int compare(Page arg0, Page arg1) {
			int comparison = 0;
			if (arg0.getTouchCount() > arg1.getTouchCount())
				comparison = -1;
			else if (arg0.getTouchCount() < arg1.getTouchCount())
				comparison = 1;
			return comparison;
		}
	}