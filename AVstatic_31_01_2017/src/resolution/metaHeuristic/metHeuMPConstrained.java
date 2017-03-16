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
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.CplexStatus;
import ilog.cplex.IloCplex.IIS.Status;
import resolution.Main.Data_Instance;

import java.rmi.activation.UnknownObjectException;
import java.security.PublicKey;
import java.util.ArrayList;

public class metHeuMPConstrained extends metHeuMP
{
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
	
	//a constructor to create ColGenMasterUConstrained object and associate it to the data object.
	public metHeuMPConstrained (Data_Instance data, ArrayList<metHeuMatch> matches,IloCplex solver, metHeuMPRules... rules)
	{
		super(data, matches,solver);
		for(metHeuMPRules rule: metHeuMPRules.values())
		{
			rule.postConstraint(this);
		}
	}
	
	// **************************************************************************************************
	// CONSTRAINTS
	// **************************************************************************************************
		
	//A method to create the objective of the matching problem.  
	public void postLinkObjs() throws IloException
	{
		try
		{
			//Allocate match variables (x variables).
			for(int i = 0 ; i < Input_Matches.size() ; i++)
			{
				Input_Matches.get(i).x = solver.intVar(0, 1, "x_"+i);
			}
			IloLinearNumExpr sum = solver.linearNumExpr();					//Create a linear expression to express the sum.
			for(int j = 0 ; j < Input_Matches.size() ; j++)					//For every match.
			{
				sum.addTerm(Input_Matches.get(j).num_participants, Input_Matches.get(j).x);
			}
			solver.addEq(Obj, sum);											//Add linked objective to the solver.
		} catch (IloException e)
		{
			e.printStackTrace();
		}
	}
	
	//A method to post the owners constraint of the matching problem.
	public void postOwnersConstraint() throws IloException
	{
		try
		{
			IloLinearNumExpr sum = solver.linearNumExpr();					//Create a linear expression to express the sum.
			for(int i = 0 ; i < data.offers.size() ; i++)					//For every offer (AV owner).
			{
				for(int j = 0 ; j < data.offers.get(i).feasible_matches.size() ; j++)   //For every feasible match for the current offer.
				{
					sum.addTerm(1, data.offers.get(i).feasible_matches.get(j).x);		
				}
				solver.addGe(1, sum);										//Add offer (owner) constraint to the solver.
				sum.clear();												//Clear the linear expression in order to be used for the next offer.
			}
		} catch (IloException e)
		{
			e.printStackTrace();
		}
	}
	
	//A method to post the riders constraint of the matching problem.
	public void postRidersConstraint() throws IloException
	{
		try
		{
			IloLinearNumExpr sum = solver.linearNumExpr();					//Create a linear expression to express the sum.
			for(int i = 0 ; i < data.requests.size() ; i++)					//For every request (rider).
			{
				for(int j = 0 ; j < data.requests.get(i).feasible_matches.size() ; j++)   //For every feasible match for the current request.
				{
					sum.addTerm(1, data.requests.get(i).feasible_matches.get(j).x);		
				}
				solver.addGe(1, sum);										//Add request (rider) constraint to the solver.
				sum.clear();												//Clear the linear expression in order to be used for the next request.
			}
		} catch (IloException e)
		{
			e.printStackTrace();
		}
	}
	
	
}
