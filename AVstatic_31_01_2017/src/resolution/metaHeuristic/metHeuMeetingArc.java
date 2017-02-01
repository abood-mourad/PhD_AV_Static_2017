package resolution.metaHeuristic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import ilog.concert.IloException;
import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.CplexStatus;
import ilog.cplex.IloCplex.IIS.Status;
import java.rmi.activation.UnknownObjectException;
import java.security.PublicKey;
import java.util.ArrayList;

public class metHeuMeetingArc 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
	
	public metHeuMeetingPoint arc_pick;								//represents the pickup meeting point of the arc.
	
	public metHeuMeetingPoint arc_drop;								//represents the drop-off meeting point of the arc.
					
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
	
	//A default constructor.
	public metHeuMeetingArc()
	{
		this.arc_pick = new metHeuMeetingPoint();
		this.arc_drop = new metHeuMeetingPoint();
	}
	
	//A constructor to create a meeting arc with parameters (pickup meeting point, drop-off meeting point).
	public metHeuMeetingArc(metHeuMeetingPoint pick, metHeuMeetingPoint drop)
	{
		this.arc_pick = new metHeuMeetingPoint(pick);
		this.arc_drop = new metHeuMeetingPoint(drop);
	}
		
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

			
}
