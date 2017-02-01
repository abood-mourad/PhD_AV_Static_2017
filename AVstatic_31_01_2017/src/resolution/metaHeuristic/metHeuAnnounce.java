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

public abstract class metHeuAnnounce 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
				
	protected metHeuPoint origin;							//represents the origin location of the trip announcement.
	
	protected metHeuPoint destination;						//represents the destination location of the trip announcement.
	
	protected double e_time;								//represents the earliest departure time of the trip announcement.
	
	protected double l_time;								//represents the latest departure time of the trip announcement.
	
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
				
	//A default constructor
	public metHeuAnnounce()
	{
			
	}
					
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

}
