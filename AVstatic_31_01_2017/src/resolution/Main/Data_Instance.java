package resolution.Main;

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
import resolution.metaHeuristic.metHeuMatch;
import resolution.metaHeuristic.metHeuMeetingPoint;
import resolution.metaHeuristic.metHeuOffer;
import resolution.metaHeuristic.metHeuRequest;

import java.rmi.activation.UnknownObjectException;
import java.security.PublicKey;
import java.util.ArrayList;

public class Data_Instance 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
		
	public ArrayList<metHeuOffer> offers;								//represents the set of offers available in the instance.
	
	public ArrayList<metHeuRequest> requests;							//represents the set of requests available in the instance.
	
	public ArrayList<metHeuMeetingPoint> meeting_points;				//represents the set of meeting points available in the instance.
	
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
		
	//A defaul constructor.
	public Data_Instance()
	{
		this.offers = new ArrayList<metHeuOffer>();
		this.requests = new ArrayList<metHeuRequest>();
		this.meeting_points = new ArrayList<metHeuMeetingPoint>();
	}
		
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

	//A method that returns the number of participants.
	public int getNbP()
	{
		//TO DO
		return 0;
	}
	
	//A method that writes the data instance into a file.
	public void Write_Instance()
	{
		//TO DO
	}
}
