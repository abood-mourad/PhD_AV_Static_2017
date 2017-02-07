package resolution.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import resolution.metaHeuristic.metHeuMPConstrained;
import resolution.metaHeuristic.metHeuMatch;
import resolution.metaHeuristic.metHeuMeetingArc;
import resolution.metaHeuristic.metHeuMeetingPoint;
import resolution.metaHeuristic.metHeuOffer;
import resolution.metaHeuristic.metHeuPoint;
import resolution.metaHeuristic.metHeuRComparator;
import resolution.metaHeuristic.metHeuRequest;

import java.rmi.activation.UnknownObjectException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;

public class AV_Static 
{
	public static IloCplex solver;
	public static double radius = 1.1;

	public static void main(String[] args) throws FileNotFoundException
	{
		//Create a print stream to the file where results will be written.
		File f = new File("C:/Users/abood.mourad/workspace/AVStatic_31_01_2017/Solutions/Match_Gen_Algo_V1/test.txt");
		PrintStream printStream = new PrintStream(f);
		//Build data instances based on original files, save generated instances into new files.
		//Build_Instances("BerlinCenter");										//Build different instances for BerlinCenter.
		Build_Instances("Birmingham");											//Build different instances for Birmingham.
		//Birmingham 
		long t= System.currentTimeMillis();										//Create a time variable to calculate excution time.
		//Read one data instance from file and store it in a Data_Instance object.
		Data_Instance instance = Read_Instance("BerlinCenter1");				//Read a Data_Instance object from a file.
		//Call the preprocessing procedure passing the Data_Instance as an input.
		ArrayList<metHeuMatch> final_matches = Match_Gen_Algo_V1(instance);		//Store the set of feasible matches returned by the preprocession.
		//Call the solve method of the matching problem passing the set of feasible matches as an input.
		try 
		{
			solver = new IloCplex();											//Create a solver object.
		} catch (IloException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		metHeuMPConstrained matching_problem = new metHeuMPConstrained(instance, final_matches, solver);		//Create a matching problem object with input instance, final matches returned by the algorithm and a solver object as parameters.
		matching_problem.Solve(printStream, t);
		//Save the solution returned by the matching problem.
		//TO DO
		//Write solution to a file (output).
		//TO DO
		//Close the printstream.
	}
	
	//A method that builds multiple Data Instance objects to be used for running the model later.
	public static void Build_Instances(String city)
	{
		//Generate five different instances for the specified city.
		for(int ni = 0 ; ni < 5 ; ni++)
		{
			//Read all zones' Xs and Ys from file to a temporal list of points.
			ArrayList<metHeuPoint> zone_centers = new ArrayList<metHeuPoint>();
			String line = null;
			String input_file = "C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_ZoneXYs";
			String MPoutput_File = "C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_MPs";
			try 
			{
				FileReader fr = new FileReader(input_file);
				BufferedReader br = new BufferedReader(fr);
				br.readLine();
				while((line = br.readLine()) != null)
				{
					String[] parts = line.split("\\s+");
					metHeuPoint zone = new metHeuPoint(Double.parseDouble(parts[1]),Double.parseDouble(parts[2]));
					zone_centers.add(zone);
				}
				br.close();
				//For every zone, generate four meeting points around its center and then write them a file.
				FileWriter MPfr = new FileWriter(MPoutput_File);
				BufferedWriter MPwr = new BufferedWriter(MPfr);
				MPwr.write("\t\t X \t\t\t\t\t Y \t\t\t Service Time");
				MPwr.newLine();
				ArrayList<metHeuMeetingPoint> meeting_points = new ArrayList<metHeuMeetingPoint>();
				for(int z = 0 ; z < zone_centers.size() ; z++)
				{
					double angle = Math.random()*Math.PI*2;
					//Create Point in X+Y+ space and add it to the list.
					metHeuMeetingPoint mp1 = new metHeuMeetingPoint();
					mp1.location.x = zone_centers.get(z).x + Math.cos(angle)*radius;
					mp1.location.y = zone_centers.get(z).y + Math.sin(angle)*radius;
					MPwr.write(mp1.location.x + "\t" + mp1.location.y + "\t\t" + mp1.service_time);
					MPwr.newLine();
					meeting_points.add(mp1);
					//Create Point in X+Y- space and add it to the list.
					metHeuMeetingPoint mp2 = new metHeuMeetingPoint();
					mp2.location.x = zone_centers.get(z).x + Math.cos(angle)*radius;
					mp2.location.y = zone_centers.get(z).y - Math.sin(angle)*radius;
					MPwr.write(mp2.location.x + "\t" + mp2.location.y + "\t\t" + mp2.service_time);
					MPwr.newLine();
					meeting_points.add(mp2);
					//Create Point in X-Y+ space and add it to the list.
					metHeuMeetingPoint mp3 = new metHeuMeetingPoint();
					mp3.location.x = zone_centers.get(z).x - Math.cos(angle)*radius;
					mp3.location.y = zone_centers.get(z).y + Math.sin(angle)*radius;
					MPwr.write(mp3.location.x + "\t" + mp3.location.y + "\t\t" + mp3.service_time);
					MPwr.newLine();
					meeting_points.add(mp3);
					//Create Point in X-Y- space and add it to the list.
					metHeuMeetingPoint mp4 = new metHeuMeetingPoint();
					mp4.location.x = zone_centers.get(z).x - Math.cos(angle)*radius;
					mp4.location.y = zone_centers.get(z).y - Math.sin(angle)*radius;
					MPwr.write(mp4.location.x + "\t" + mp4.location.y + "\t\t" + mp4.service_time);
					MPwr.newLine();
					meeting_points.add(mp4);
				}
				MPwr.close();
				//Generate OD trips.
				
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//A method for reading one Data instance from a file to a Data instance object.
	public static Data_Instance Read_Instance(String city_instance)
	{
		Data_Instance instance = new Data_Instance();
		
		return instance;
	}
	
	//Match Generation Algorithm (preprocessing) #Heuristic-1
	public static ArrayList<metHeuMatch> Match_Gen_Algo_V1(Data_Instance instance)
	{
		ArrayList<metHeuMatch> final_matches = new ArrayList<metHeuMatch>();	//Create an ArrayList to store the list of final matches to be returned by the algorithm.
		ArrayList<metHeuOffer> extended_offers = new ArrayList<metHeuOffer>();	//Create an ArrayList to store offers for direct trips and the artificial round trip offers to be constructed.
		//TO DO
		//Save meeting points in k-d tree.
		//for each offer in the list of offers
		for(int o = 0 ; o < instance.offers.size() ; o++)
		{
			//if the offer is a direct trip then 
			if((instance.offers.get(o).origin.x == instance.offers.get(o).destination.x) && (instance.offers.get(o).origin.y == instance.offers.get(o).destination.y))
			{
				extended_offers.add(instance.offers.get(o)); 						 	//add offer (direct trip) to an extended list
			}
			else  																		//it is a round trip
			{
				metHeuPoint origin = new metHeuPoint(instance.offers.get(o).origin.x,instance.offers.get(o).origin.y);		//Initialize tmp origin with current offer origin, to be updated iteratively.
				//Sort riders with respect to their distance from tmp origin.
				ArrayList<metHeuRequest> sorted_request = Sort_Riders(instance.requests, instance.offers.get(o).origin);
				//Establish a round trip offer by concatenating some riders as artificial owners.
				//TO DO: Check ALGO !
			}
		}
		//for each request (rider) in the list of requests
		for(int j = 0 ; j < instance.requests.size() ; j++)
		{
			instance.requests.get(j).MP_Arc = Find_Rider_Feasible_Arcs(instance, instance.requests.get(j));      //search the MP tree and store the feasible MP arcs for the current request.
		}
		//for each offer in the extended list of offers
		for(int i = 0 ; i < extended_offers.size() ; i++)
		{
			//obtain compatible riders with the offer
			ArrayList<metHeuRequest> feasible_riders = new ArrayList<metHeuRequest>();	//Create an ArrayList to store the set of feasible riders for the offer.
			int best_savings;															//Create a variable to save the value of the best distance savings obtained for the triplet (offer, request, arc).
			//for each request (rider) in the list of requests
			for(int j = 0 ; j < instance.requests.size() ; j++)
			{
				best_savings = 0;
				//for each rider meeting point arc
				for(int a = 0 ; a < instance.requests.get(j).MP_Arc.size() ; a++)
				{
					//if the match of the offer, the rider and the meeting point arc is feasible then
						//store meeting point arc
						//compute distance savings
						//if the computed distance savings > best match distance savings found so far then
							//update best match distance savings
							//update best match
				}
				if(best_savings > 0)													//If best match distance savings > 0 then
				{
					//append match to match list
					feasible_riders.add(instance.requests.get(j)); 						//Append rider to feasible rider list
				}
			}
			if(feasible_riders.size() > 1)										//if number of feasible riders > 1 then
			{
				//for k=2, .., offer.capacity 
				for(int k = 2 ; k < extended_offers.get(i).num_seats ; k++)
				{
					//retrieve meeting point arcs
					//remove meeting point arcs that are feasible for less than k riders
					//try to construct new matches with k riders
					//if new match found then
						//compute distance savings
						//if distance savings > 0 then
							//append match to match list
				}
			}
		}
		//return match list
		return final_matches;													//Return the final list of matches.
	}
	
	//A method that returns the set of feasible meeting point arcs for a given rider.
	public static ArrayList<metHeuMeetingArc> Find_Rider_Feasible_Arcs(Data_Instance instance, metHeuRequest rider)
	{
		ArrayList<metHeuMeetingArc> feasible_arcs = new ArrayList<metHeuMeetingArc>();
		//TO DO
		return feasible_arcs;
	}
	
	//A method for sorting riders according to their distance from a certain point.
	public static ArrayList<metHeuRequest> Sort_Riders(ArrayList<metHeuRequest> requests, metHeuPoint origin)
	{
		//Calculate distance between every rider and an origin point.
		for(int i = 0 ; i < requests.size() ; i++)
		{
			requests.get(i).tmp_distance = Calculate_Distance(requests.get(i).origin, origin);
		}
		//Call the comparator to sort the list according the calculated distances.
		Collections.sort(requests, new metHeuRComparator());
		return requests;														//Return the sorted list.
	}
	
	//A method for calculating the distance between two points.
	public static double Calculate_Distance(metHeuPoint p1, metHeuPoint p2)
	{
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}

}
