package resolution.metaHeuristic;

import java.util.Comparator;
import java.lang.*;

public class metHeuRComparator implements Comparator<metHeuRequest>
{

	@Override
	public int compare(metHeuRequest r1, metHeuRequest r2) 
	{
		if(r1.tmp_distance > r2.tmp_distance)
		{
			return 1;
		}
		else if(r1.tmp_distance < r2.tmp_distance)
		{
			return -1;
		}
		return 0;
	}

}
