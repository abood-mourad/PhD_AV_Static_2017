package resolution.metaHeuristic;

import ilog.concert.IloException;

public enum metHeuMPRules 
{
	//Matching objective rule.
	LINK_OBJECTIVES 
	{
	    void postConstraint(metHeuMPConstrained model) 
		{
		    try 
		    {
		    	model.postLinkObjs();			//post master objective.
		    } catch (IloException e) 
		    {
		        e.printStackTrace();
		    }
		}
	},
		
	//Owners constraint rule.
	OWNERS_CONSTRAINT
	{
		void postConstraint(metHeuMPConstrained model)
		{
			try 
		    {
		    	model.postOwnersConstraint();	//post master covering constraint.
		    } catch (IloException e) 
		    {
		        e.printStackTrace();
		    }
		}
	},
		
	//Riders constraint rule.
	RIDERS_CONSTRAINT
	{
		void postConstraint(metHeuMPConstrained model)
		{
			try 
		    {
		    	model.postRidersConstraint();	 //post master columns constraint.
		    } catch (IloException e) 
		    {
		        e.printStackTrace();
		    }
		}
	},
		
	;
	
	abstract void postConstraint(metHeuMPConstrained model);
}
