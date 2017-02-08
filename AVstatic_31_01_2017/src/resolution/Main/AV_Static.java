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
import java.util.Random;

public class AV_Static 
{
	public static IloCplex solver;
	public static double radius = 1.1;
	public static double speed = 15;
	public static double max_duration = 0.35;
	public static int num_seats = 3;
	public static double max_walk = 0.11;

	public static void main(String[] args) throws FileNotFoundException
	{
		//Create a print stream to the file where results will be written.
		File f = new File("C:/Users/abood.mourad/workspace/AVStatic_31_01_2017/Solutions/Match_Gen_Algo_V1/test.txt");
		PrintStream printStream = new PrintStream(f);
		//Build data instances based on original files, save generated instances into new files.
		Build_Instances("BerlinCenter");										//Build different instances for BerlinCenter.
		//Build_Instances("Birmingham");											//Build different instances for Birmingham.
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
			String MPoutput_File = "C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_MPs_" + ni;
			try 
			{
				FileReader fr = new FileReader(input_file);
				BufferedReader br = new BufferedReader(fr);
				br.readLine();
				int counter = 1;
				while((line = br.readLine()) != null)
				{
					String[] parts = line.split("\\s+");
					metHeuPoint zone = new metHeuPoint(Double.parseDouble(parts[1]),Double.parseDouble(parts[2]));
					zone.id = counter++;
					zone_centers.add(zone);
				}
				br.close();
				//For every zone, generate four meeting points around its center and then write them a file.
				FileWriter MPfr = new FileWriter(MPoutput_File);
				BufferedWriter MPwr = new BufferedWriter(MPfr);
				MPwr.write("ID \t\t X \t\t\t\t\t Y \t\t\t Service Time");
				MPwr.newLine();
				counter = 1;
				ArrayList<metHeuMeetingPoint> meeting_points = new ArrayList<metHeuMeetingPoint>();
				for(int z = 0 ; z < zone_centers.size() ; z++)
				{
					double angle = Math.random()*Math.PI*2;
					//Create Point in X+Y+ space and add it to the list.
					metHeuMeetingPoint mp1 = new metHeuMeetingPoint();
					mp1.location.id = counter++;
					mp1.location.x = zone_centers.get(z).x + Math.cos(angle)*radius;
					mp1.location.y = zone_centers.get(z).y + Math.sin(angle)*radius;
					MPwr.write(mp1.location.id + "\t" + mp1.location.x + "\t" + mp1.location.y + "\t\t" + mp1.service_time);
					MPwr.newLine();
					meeting_points.add(mp1);
					//Create Point in X+Y- space and add it to the list.
					metHeuMeetingPoint mp2 = new metHeuMeetingPoint();
					mp2.location.id = counter++;
					mp2.location.x = zone_centers.get(z).x + Math.cos(angle)*radius;
					mp2.location.y = zone_centers.get(z).y - Math.sin(angle)*radius;
					MPwr.write(mp2.location.id + "\t" + mp2.location.x + "\t" + mp2.location.y + "\t\t" + mp2.service_time);
					MPwr.newLine();
					meeting_points.add(mp2);
					//Create Point in X-Y+ space and add it to the list.
					metHeuMeetingPoint mp3 = new metHeuMeetingPoint();
					mp3.location.id = counter++;
					mp3.location.x = zone_centers.get(z).x - Math.cos(angle)*radius;
					mp3.location.y = zone_centers.get(z).y + Math.sin(angle)*radius;
					MPwr.write(mp3.location.id + "\t" + mp3.location.x + "\t" + mp3.location.y + "\t\t" + mp3.service_time);
					MPwr.newLine();
					meeting_points.add(mp3);
					//Create Point in X-Y- space and add it to the list.
					metHeuMeetingPoint mp4 = new metHeuMeetingPoint();
					mp4.location.id = counter++;
					mp4.location.x = zone_centers.get(z).x - Math.cos(angle)*radius;
					mp4.location.y = zone_centers.get(z).y - Math.sin(angle)*radius;
					MPwr.write(mp4.location.id + "\t" + mp4.location.x + "\t" + mp4.location.y + "\t\t" + mp4.service_time);
					MPwr.newLine();
					meeting_points.add(mp4);
				}
				MPwr.close();
				//Generate OD trips.
				//Read AVGs for all zones.
				FileReader AVGwr = new FileReader("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_ZoneAVGs");
				BufferedReader AVGbr = new BufferedReader(AVGwr);
				FileWriter OFwr = new FileWriter("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_Offers_" + ni);
				FileWriter REwr = new FileWriter("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city + "_Requests_" + ni);
				BufferedWriter OFbr = new BufferedWriter(OFwr);
				BufferedWriter REbr = new BufferedWriter(REwr);
				OFbr.write("ID \t\t Origin_X \t\t\t\t\t Origin_Y \t\t\t\t\t Destination_X \t\t\t\t\t Destination_Y \t\t\t\t\t E_time \t\t\t L_time \t\t\t Max_Duration \t\t\t Seats");
				OFbr.newLine();
				REbr.write("ID \t\t Origin_X \t\t\t\t\t Origin_Y \t\t\t\t\t Destination_X \t\t\t\t\t Destination_Y \t\t\t\t\t E_time \t\t\t L_time \t\t\t Max_Walk");
				REbr.newLine();
				AVGbr.readLine();															//Read until the line of AVGs.
				AVGbr.readLine();
				AVGbr.readLine();
				AVGbr.readLine();
				AVGbr.readLine();
				String AVG_line;
				int offer_counter = 1;
				int request_counter = 1;
				for(int row = 0 ; row < zone_centers.size() ; row++)
				{
					AVG_line = AVGbr.readLine();											//Read the AVGs associated with the current zone.
					String[] AVG_parts = AVG_line.split(";");								//Split line into different AVG records (destination : AVG).
					for(int des = 0 ; des < AVG_parts.length -1 ; des++)					//For every AVG record.
					{
						String[] AVG_des = AVG_parts[des].split(":");						//Split AVG record into a destination and a AVG value.
						int num_trips =	getPoisson(Double.parseDouble(AVG_des[1]) * 0.055);	//Compute the actual number of trips.
						for(int t = 1 ; t <= num_trips ; t++)
						{
							if(t % 2 != 0)													//If the announcement to be generated is of an owner.
							{
								//Generate the morning trip offer.
								metHeuOffer offer1 = new metHeuOffer();						//Create offer object.
								offer1.id = offer_counter++;								//Assign offer id.									
								//Generate origin and destination points.
								double w = radius*Math.sqrt(Math.random());
								double u = 2*Math.PI*Math.random();
								offer1.origin.x = zone_centers.get(row).x + (w * Math.cos(u));
								offer1.origin.y = zone_centers.get(row).y + (w * Math.sin(u));
								offer1.destination.x = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).x + (w * Math.cos(u));
								offer1.destination.y = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).y + (w * Math.sin(u));
								//Generate time window.
								Random r = new Random();
								offer1.e_time = r.nextGaussian()*0.5+7.5;					//Earliest departure time.
								offer1.l_time = offer1.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(row), zone_centers.get(Integer.parseInt(AVG_des[0].trim()))));
								//Generate max duration and num of seats.
								offer1.max_duration = (offer1.l_time - offer1.e_time) + max_duration;
								offer1.num_seats = num_seats;
								//Add trip announcement (offer) to file.
								OFbr.write(offer1.id + "\t" + offer1.origin.x + "\t" + offer1.origin.y + "\t" + offer1.destination.x + "\t" + offer1.destination.y + "\t" + offer1.e_time + "\t" + offer1.l_time + "\t" + offer1.max_duration + "\t" + offer1.num_seats);
								OFbr.newLine();
								//Generate the evening trip offer.
								metHeuOffer offer2 = new metHeuOffer();						//Create offer object.
								offer2.id = offer_counter++;								//Assign offer id.
								//Assign offer origin and destination (reverse offer1).
								offer2.origin.x = offer1.destination.x;
								offer2.origin.y = offer1.destination.y;
								offer2.destination.x = offer1.origin.x;
								offer2.destination.y = offer2.origin.y;
								//Generate time window.
								offer2.e_time = r.nextGaussian()*0.5+17.5;					//Earliest departure time.
								offer2.l_time = offer2.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(Integer.parseInt(AVG_des[0].trim())) , zone_centers.get(row)));
								//Generate max duration and num of seats.
								offer2.max_duration = (offer2.l_time - offer2.e_time) + max_duration;
								offer2.num_seats = num_seats;
								//Add trip announcement (offer) to file.
								OFbr.write(offer2.id + "\t" + offer2.origin.x + "\t" + offer2.origin.y + "\t" + offer2.destination.x + "\t" + offer2.destination.y + "\t" + offer2.e_time + "\t" + offer2.l_time + "\t" + offer2.max_duration + "\t" + offer2.num_seats);
								OFbr.newLine();
								//Generate the round trip (during work hours).
								metHeuOffer offer3 = new metHeuOffer();						//Create offer object.
								offer3.id = offer_counter++;								//Assign offer id.
								//Assign offer origin and destination (same point).
								offer3.origin.x = offer1.destination.x;
								offer3.origin.y = offer1.destination.y;
								offer3.destination.x = offer1.destination.x;
								offer3.destination.y = offer1.destination.y;
								//Generate time window.
								offer3.e_time = r.nextGaussian()*0.5+10;
								offer3.l_time = r.nextGaussian()*0.5+16.5;
								//Generate max duration and num of seats.
								offer3.max_duration = (offer3.l_time - offer3.e_time) + max_duration;
								offer3.num_seats = num_seats + 1;
								//Add trip announcement (offer) to file.
								OFbr.write(offer3.id + "\t" + offer3.origin.x + "\t" + offer3.origin.y + "\t" + offer3.destination.x + "\t" + offer3.destination.y + "\t" + offer3.e_time + "\t" + offer3.l_time + "\t" + offer3.max_duration + "\t" + offer3.num_seats);
								OFbr.newLine();
							}
							else															//Else the announcement is of a rider.
							{
								//Generate the morning trip request.
								metHeuRequest request1 = new metHeuRequest();				//Create request object.
								request1.id = request_counter++;							//Assign request id.									
								//Generate origin and destination points.
								double w = radius*Math.sqrt(Math.random());
								double u = 2*Math.PI*Math.random();
								request1.origin.x = zone_centers.get(row).x + (w * Math.cos(u));
								request1.origin.y = zone_centers.get(row).y + (w * Math.sin(u));
								request1.destination.x = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).x + (w * Math.cos(u));
								request1.destination.y = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).y + (w * Math.sin(u));
								//Generate time window.
								Random r = new Random();
								request1.e_time = r.nextGaussian()*0.5+7.5;					//Earliest departure time.
								request1.l_time = request1.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(row), zone_centers.get(Integer.parseInt(AVG_des[0].trim()))));		//Latest arrival time.
								//Assign max walking time.
								request1.max_walk = max_walk;
								//Add trip announcement (request) to file.
								REbr.write(request1.id + "\t" + request1.origin.x + "\t" + request1.origin.y + "\t" + request1.destination.x + "\t" + request1.destination.y + "\t" + request1.e_time + "\t" + request1.l_time + "\t" + request1.max_walk);
								REbr.newLine();
								//Generate the evening trip request.
								metHeuRequest request2 = new metHeuRequest();				//Create request object.
								request2.id = request_counter++;							//Asiign request id.
								//Assign request origin and destination (reverse request1).
								request2.origin.x = request1.destination.x;
								request2.origin.y = request1.destination.y;
								request2.destination.x = request1.origin.x;
								request2.destination.y = request1.origin.y;
								//Generate time window.
								request2.e_time = r.nextGaussian()*0.5+17.5;				//Earliest departure time.
								request2.l_time = request2.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(Integer.parseInt(AVG_des[0].trim())),zone_centers.get(row)));		//Latest arrival time.
								//Assign max walking time.
								request2.max_walk = max_walk;
								//Add trip announcement (request) to file.
								REbr.write(request2.id + "\t" + request2.origin.x + "\t" + request2.origin.y + "\t" + request2.destination.x + "\t" + request2.destination.y + "\t" + request2.e_time + "\t" + request2.l_time + "\t" + request2.max_walk);
								REbr.newLine();
							}
						}
					}
				}
				OFbr.close();
				REbr.close();
				AVGbr.close();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.print("DONE GENERATING INSTANCES");
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
	
	//A method for calculating the travel time between two points.
	public static double Calculate_Travel_Time(double distance)
	{
		return distance / speed;
	}
	
	//A method for calculating a Poisson distribution.
	public static int getPoisson(double lambda) 
	{
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;
		do 
		{
			k++;
			p *= Math.random();
		} while (p > L);
		return k - 1;
	}

}
