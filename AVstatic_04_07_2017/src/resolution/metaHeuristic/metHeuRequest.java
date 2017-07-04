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

public class metHeuRequest extends metHeuAnnounce
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
	
	public double max_walk;										//represents the maximum walking distance (time) possible for the rider.
	
	public ArrayList<metHeuMeetingPoint> MP_Pick;				//represents the set of feasible pickup meeting points for the request.
	
	public ArrayList<metHeuMeetingPoint> MP_Drop;				//represents the set of feasible drop off meeting points for the request.
	
	public ArrayList<metHeuMeetingArc> MP_Arc;					//represents the set of feasible meeting point arcs for the request.
	
	public ArrayList<metHeuMatch> feasible_matches;				//represents the set of feasible matches for the request, to be filled during the preprocessing.
	
	public double tmp_distance;									//represents the distance between request origin and a given point(to be used later to temporarily store updated distances).
	
	public Boolean is_artificial;								//represents a flag to indicate whether this request is transfered to an offer or not.
	
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
		
	//A default constructor.
	public metHeuRequest()
	{
		this.origin = new metHeuPoint();
		this.destination = new metHeuPoint();
		this.MP_Pick = new ArrayList<metHeuMeetingPoint>();
		this.MP_Drop = new ArrayList<metHeuMeetingPoint>();
		this.MP_Arc = new ArrayList<metHeuMeetingArc>();
		this.feasible_matches = new ArrayList<metHeuMatch>();
		this.tmp_distance = Double.MAX_VALUE;
		this.is_artificial = false;
	}
		
	//A constructor to create an offer (AV owner) object with parameters (origin, destination, etime, ltime, mawimum duration, number of available seats).
	public metHeuRequest(int r_id, metHeuPoint o, metHeuPoint d, double etime, double ltime, double walk)
	{
		this.id = r_id;
		this.origin = new metHeuPoint(o.x,o.y);
		this.destination = new metHeuPoint(d.x,d.y);
		this.e_time = etime;
		this.l_time = ltime;
		this.max_walk = walk;
		this.MP_Pick = new ArrayList<metHeuMeetingPoint>();
		this.MP_Drop = new ArrayList<metHeuMeetingPoint>();
		this.MP_Arc = new ArrayList<metHeuMeetingArc>();
		this.feasible_matches = new ArrayList<metHeuMatch>();
		this.tmp_distance = Double.MAX_VALUE;
		this.is_artificial = false;
	}
							
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

}
