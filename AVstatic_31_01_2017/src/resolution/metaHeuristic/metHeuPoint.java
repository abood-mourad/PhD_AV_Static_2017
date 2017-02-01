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

public class metHeuPoint 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
			
	protected double x;								//represents point coordinates on the X axis.
	
	protected double y;								//represents point coordinates on the Y axis.
			
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
			
	//A default constructor
	public metHeuPoint()
	{
		
	}
	
	//A constructor to create a metHeuPoint object with (x,y) parameters.
	public metHeuPoint(double X, double Y)
	{
		this.x = X;
		this.y = Y;
	}
			
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

	
	
}
