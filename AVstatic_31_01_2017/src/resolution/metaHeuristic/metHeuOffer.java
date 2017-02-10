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

public class metHeuOffer extends metHeuAnnounce
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
	
	public double max_duration;									//represents the maximum trip duration that the owner accepts.
	
	public int num_seats;										//represents the number of available seats.
	
	public ArrayList<metHeuMatch> feasible_matches;				//represents the set of feasible matches for the offer, to be filled during the preprocessing.
		
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
	
	//A default constructor.
	public metHeuOffer()
	{
		this.origin = new metHeuPoint();
		this.destination = new metHeuPoint();
		this.feasible_matches = new ArrayList<metHeuMatch>();
	}
	
	//A constructor to create an offer (AV owner) object with parameters (origin, destination, etime, ltime, mawimum duration, number of available seats).
	public metHeuOffer(int o_id, metHeuPoint o, metHeuPoint d, double etime, double ltime, double duration, int seats)
	{
		this.id = o_id;
		this.origin = new metHeuPoint(o.x,o.y);
		this.destination = new metHeuPoint(d.x,d.y);
		this.e_time = etime;
		this.l_time = ltime;
		this.max_duration = duration;
		this.num_seats = seats;
		this.feasible_matches = new ArrayList<metHeuMatch>();
	}
						
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

}
