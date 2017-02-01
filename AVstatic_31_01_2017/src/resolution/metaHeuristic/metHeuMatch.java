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


public class metHeuMatch 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
		
	public metHeuOffer offer;										//represents the offer (AV owner) associated to the match.
	
	public ArrayList<metHeuRequest> requests;						//represents the set of requests (riders) associated to the match.
	
	public metHeuMeetingArc arc;									//represents the meeting point arc associated to the match.
	
	public int num_participants;									//represents the number of participants in the match.
	
	public double dist_save;										//represents the distance savings obtained in the match.
	
	protected IloIntVar x; 										//represents the usage of the match, equal 1 if a match is chosen, 0 otherwise.
		
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
		
	//A default constructor.
	public metHeuMatch()
	{
		this.offer = new metHeuOffer();
		this.requests = new ArrayList<metHeuRequest>();
		this.arc = new metHeuMeetingArc();
		this.num_participants = 0;
		this.dist_save = 0;
	}
		
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

		

}
