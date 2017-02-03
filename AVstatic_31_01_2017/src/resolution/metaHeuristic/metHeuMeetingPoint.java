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

public class metHeuMeetingPoint 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
	
	public metHeuPoint location;									//represents the actual location of the meeting point.
	
	public double service_time;									//represents the service time required at the meeting point (the time for riders to get in or out the AV).
				
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
	
	//A default constructor.
	public metHeuMeetingPoint()
	{
		this.location = new metHeuPoint();
		this.service_time = 0;
	}
	
	//A constructor to create a meeting point object with parameters (location).
	public metHeuMeetingPoint(metHeuMeetingPoint loc)
	{
		this.location = new metHeuPoint(loc.location.x,loc.location.y);
		this.service_time = 0;
	}
	
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

		
}
