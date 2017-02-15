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
import resolution.Main.Data_Instance;

import java.rmi.activation.UnknownObjectException;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.ArrayList;

abstract class metHeuMP 
{
	// **************************************************************************************************
	// VARIABLES
	// **************************************************************************************************
	
	Data_Instance data;											//represents the data instance associated to the matching problem. 
	
	protected IloCplex solver; 									//represents the solver object to be used to solve the matching problem.
	
	protected long start; 										//represents the starting time of the execution.

	protected IloNumVar Obj; 									//represents the objective of the matching problem.
	
	protected ArrayList<metHeuMatch> Input_Matches; 			//represents the set of all matches in the matching problem.

	protected ArrayList<metHeuMatch> Best_Matches; 				//represents the set of best matches (solution).
	
	public double Obj_Val; 										//represents the objective value of the matching problem.
	
	// **************************************************************************************************
	// CONSTRUCTORS
	// **************************************************************************************************
	
	//A default constructor
	public metHeuMP()
	{
		
	}
	
	//A constructor to create a matching problem object and associate the set of feasible matches and the solver to it.
	public metHeuMP(Data_Instance instance,ArrayList<metHeuMatch> matches,IloCplex solver)
	{
		this.data = instance;
		this.solver = solver;
		this.Input_Matches = matches;
		this.Obj_Val = 0;
		createObjective();										//create problem objective.
	}
	
	// **************************************************************************************************
	// METHODS
	// **************************************************************************************************

	//A method create the objective object.
	private void createObjective() 
	{
		try 
		{
			//Building objective.
			int tmp = data.getNbP();
			Obj = solver.numVar(0, tmp, "Obj");
		} catch (IloException e) 
		{
			e.printStackTrace();
		}
	}
	
	//A method to solve the matching problem and find the objective value.
	public void Solve(PrintStream printStream, long t)
	{
		try
		{
			solver.addMaximize(Obj);										//initialize objective.
			solver.setParam(IloCplex.IntParam.RootAlg, 1); 					//adapt solver configurations, optimizer type = primal.
			start = System.currentTimeMillis();								//start the solving process.
			String tmp = solver.getModel().toString();						
			System.out.println(tmp);										//print in the model.
			//printStream.print(tmp);
			//printStream.println("================================================================");
			Boolean solved = solver.solve();								//run the solver.
			ilog.cplex.IloCplex.Status st = solver.getStatus();				//get solver status, for testing purposes.
			CplexStatus ss = solver.getCplexStatus();
			if(solved == Boolean.TRUE) 										//if the master problem is already solved.
			{
				double global_saving = 0;
				// Print value of variables
				for (int index = 0 ; index < Input_Matches.size() ; index++ )
				{	
					//System.out.println("x_" + index + " : " + solver.getValue(Input_Matches.get(index).x));		
					//printStream.print("x_" + index + " : " + solver.getValue(Input_Matches.get(index).x) + "\n");
					if(solver.getValue(Input_Matches.get(index).x) == 1.0)
					{
						printStream.print("Offer: " + Input_Matches.get(index).offer.id + " , Requests: ");
						for(int r = 0 ; r < Input_Matches.get(index).requests.size() ; r++)
						{
							printStream.print(Input_Matches.get(index).requests.get(r).id + " ; ");
						}
						global_saving += Input_Matches.get(index).dist_save;
						printStream.println();
					}
				}
				printStream.println("==============================================================================================================");
				//get the objective value.
				Obj_Val = solver.getObjValue();
				//printStream.print(Obj_Val);
				//Save (objective value, variable values, time) to a file using the printStream.
				DecimalFormat df = new DecimalFormat("#.###");
				printStream.println("Instance \t Zones \t MPs \t Offers \t Requests \t Exec_Time(s) \t Participants \t Rate(%) \t Savings(km)");
				printStream.println(data.instance_id + "\t\t 865 \t" + data.meeting_points.size() + "\t" + data.offers.size() + "\t\t" + data.requests.size() + "\t\t" + df.format((System.currentTimeMillis()-t)*0.001) + "\t\t\t" + Obj_Val + "\t\t\t" + df.format((Obj_Val/data.requests.size())*100) + "\t\t" + df.format(global_saving));
				printStream.println("==============================================================================================================");
			}
			else 
			{
				System.out.println("\nFail to solve the problem status: " + solved);
			}
		} catch (IloException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			//clear model, in order to be built again in the next iteration.
			solver.clearModel();
		} catch (IloException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
