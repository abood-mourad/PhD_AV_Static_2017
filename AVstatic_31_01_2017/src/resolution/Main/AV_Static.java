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
	public static double radius = 1.770274;										//1.770274										
	public static double speed = 24.14;											//24.14
	public static double max_duration = 0.35;
	public static int num_seats = 3;
	public static double max_walk = 0.804;
	public static final double R = 6372.8; 										//Kilometers.
	public static double share_percentage = 0.055;								//Percentage of trips willing to be shared.
	public static double uplift = 0.1;	
	public static double flexibility = 0.5;										//To be added to e_time when calculating l_time.
	public static double MP_service_time = 0.033;
	public static int version = 0;												//Denotes the instance version to be tested.

	public static void main(String[] args) throws FileNotFoundException
	{
		//Create a print stream to the file where results will be written.
		File f = new File("C:/Users/abood.mourad/workspace/AVStatic_31_01_2017/Solutions/Match_Gen_Algo_V1/Result_BR_" + version + ".txt");
		//File f = new File("C:/Users/abood.mourad/workspace/AVStatic_31_01_2017/Solutions/Match_Gen_Algo_V1/Result_BT_" + version + ".txt");
		//File f = new File("C:/Users/abood.mourad/workspace/AVStatic_31_01_2017/Solutions/Match_Gen_Algo_V1/Result_BI_" + version + ".txt");
		PrintStream printStream = new PrintStream(f);
		//Build data instances based on original files, save generated instances into new files.
		Build_Instances("BerlinCenter");										//Build different instances for BerlinCenter.
		//Build_Instances("BerlinTiergarten");											//Build different instances for BerlinTiergarten.
		//Build_Instances("Birmingham");											//Build different instances for Birmingham.
		//Read one data instance from file and store it in a Data_Instance object.
		Data_Instance instance = Read_Instance("BerlinCenter", version);				//Read a Data_Instance object from a file.
		//Data_Instance instance = Read_Instance("BerlinTiergarten", version);					//Read a Data_Instance object from a file.
		//Data_Instance instance = Read_Instance("Birmingham", version);					//Read a Data_Instance object from a file.
		instance.instance_id = "BR_" + version;
		//instance.instance_id = "BT_" + version;
		//instance.instance_id = "BI_" + version;
		long t= System.currentTimeMillis();										//Create a time variable to calculate excution time.
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
		//Close the printstream.
		printStream.close();
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
					for(int tmp = 0 ; tmp < 8 ; tmp++)
					{
						//Create meeting point and add it to the list.
						double w = radius*Math.sqrt(Math.random());
						double u = 2*Math.PI*Math.random();
						metHeuMeetingPoint mp = new metHeuMeetingPoint();
						mp.location.id = counter++;
						mp.location.x = zone_centers.get(z).x + (w * Math.cos(u));
						mp.location.y = zone_centers.get(z).y + (w * Math.sin(u));
						mp.service_time = MP_service_time;
						MPwr.write(mp.location.id + "\t" + mp.location.x + "\t" + mp.location.y + "\t\t" + mp.service_time);
						MPwr.newLine();
					}
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
				OFbr.write("ID \t\t Origin_X \t\t\t Origin_Y \t\t Destination_X \t\t Destination_Y \t\t E_time \t\t\t L_time \t\t\t Max_Duration \t\t\t Seats");
				OFbr.newLine();
				REbr.write("ID \t\t Origin_X \t\t\t Origin_Y \t\t Destination_X \t\t Destination_Y \t\t E_time \t\t\t L_time \t\t\t Max_Walk");
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
					String[] AVG_parts = AVG_line.trim().split(";");								//Split line into different AVG records (destination : AVG).
					for(int des = 0 ; des < AVG_parts.length -1 ; des++)					//For every AVG record.
					{
						String[] AVG_des = AVG_parts[des].split(":");						//Split AVG record into a destination and a AVG value.
						int num_trips =	getPoisson(Double.parseDouble(AVG_des[1]) * share_percentage);	//Compute the actual number of trips.
						for(int t = 1 ; t <= num_trips ; t++)
						{
							if(t % 2 == 0)													//If the announcement to be generated is of an owner.
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
								offer1.l_time = offer1.e_time + Calculate_Travel_Time(Calculate_Distance(offer1.origin, offer1.destination)) + flexibility;
								//offer1.l_time = offer1.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(row), zone_centers.get(Integer.parseInt(AVG_des[0].trim())))) + flexibility;
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
								offer2.l_time = offer2.e_time + Calculate_Travel_Time(Calculate_Distance(offer2.origin, offer2.destination)) + flexibility;
								//offer2.l_time = offer2.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(Integer.parseInt(AVG_des[0].trim())) , zone_centers.get(row))) + flexibility;
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
								request1.l_time = request1.e_time + Calculate_Travel_Time(Calculate_Distance(request1.origin, request1.destination)) + flexibility;
								//request1.l_time = request1.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(row), zone_centers.get(Integer.parseInt(AVG_des[0].trim())))) + flexibility;		//Latest arrival time.
								//Assign max walking time.
								request1.max_walk = max_walk;
								//Add trip announcement (request) to file.
								REbr.write(request1.id + "\t" + request1.origin.x + "\t" + request1.origin.y + "\t" + request1.destination.x + "\t" + request1.destination.y + "\t" + request1.e_time + "\t" + request1.l_time + "\t" + request1.max_walk);
								REbr.newLine();
								//Generate the evening trip request.
								metHeuRequest request2 = new metHeuRequest();				//Create request object.
								request2.id = request_counter++;							//Assign request id.
								//Assign request origin and destination (reverse request1).
								request2.origin.x = request1.destination.x;
								request2.origin.y = request1.destination.y;
								request2.destination.x = request1.origin.x;
								request2.destination.y = request1.origin.y;
								//Generate time window.
								request2.e_time = r.nextGaussian()*0.5+17.5;				//Earliest departure time.
								request2.l_time = request2.e_time + Calculate_Travel_Time(Calculate_Distance(request2.origin, request2.destination)) + flexibility;
								//request2.l_time = request2.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(Integer.parseInt(AVG_des[0].trim())),zone_centers.get(row))) + flexibility;		//Latest arrival time.
								//Assign max walking time.
								request2.max_walk = max_walk;
								//Add trip announcement (request) to file.
								REbr.write(request2.id + "\t" + request2.origin.x + "\t" + request2.origin.y + "\t" + request2.destination.x + "\t" + request2.destination.y + "\t" + request2.e_time + "\t" + request2.l_time + "\t" + request2.max_walk);
								REbr.newLine();
								//Generate a request during the day (between 10 and 16).
								metHeuRequest request3 = new metHeuRequest();				//Create request object.
								request3.id = request_counter++;							//Assign request id.
								//Generate origin and destination points.
								w = radius*Math.sqrt(Math.random());
								u = 2*Math.PI*Math.random();
								request3.origin.x = zone_centers.get(row).x + (w * Math.cos(u));
								request3.origin.y = zone_centers.get(row).y + (w * Math.sin(u));
								request3.destination.x = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).x + (w * Math.cos(u));
								request3.destination.y = zone_centers.get(Integer.parseInt(AVG_des[0].trim())).y + (w * Math.sin(u));
								//Generate time window (uniformly distributed between 10 AM and 16 PM).
								request3.e_time = r.nextDouble()*6+10;
								request3.l_time = request3.e_time + Calculate_Travel_Time(Calculate_Distance(request3.origin, request3.destination)) + flexibility;
								//request3.l_time = request3.e_time + Calculate_Travel_Time(Calculate_Distance(zone_centers.get(row), zone_centers.get(Integer.parseInt(AVG_des[0].trim())))) + flexibility;
								//Assign max walking time.
								request3.max_walk = max_walk;
								//Add trip announcement (request) to file.
								REbr.write(request3.id + "\t" + request3.origin.x + "\t" + request3.origin.y + "\t" + request3.destination.x + "\t" + request3.destination.y + "\t" + request3.e_time + "\t" + request3.l_time + "\t" + request3.max_walk);
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
	public static Data_Instance Read_Instance(String city_instance, int version)
	{
		Data_Instance instance = new Data_Instance();
		//Read instance from files.
		try 
		{
			//Read meeting points from file and add them to instance.
			FileReader MPfr = new FileReader("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city_instance + "_MPs_" + version);
			BufferedReader MPbr = new BufferedReader(MPfr);
			MPbr.readLine();
			String line = null;
			while((line = MPbr.readLine()) != null)
			{
				String[] parts = line.split("\\s+");
				metHeuMeetingPoint MP = new metHeuMeetingPoint();									//Create meeting point object.
				MP.location.id = Integer.parseInt(parts[0].trim());									//Read location id.
				MP.location.x = Double.parseDouble(parts[1].trim());								//Read location coordinate on X-axis.
				MP.location.y = Double.parseDouble(parts[2].trim());								//Read location coordinate on Y-axis.
				MP.service_time = Double.parseDouble(parts[3].trim());								//Read MP service time.
				instance.meeting_points.add(MP);													//Add meeting point to the instance list of meeting points.
			}
			MPbr.close();
			//Read trip offers from file and add them to instance.
			FileReader OFfr = new FileReader("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city_instance + "_Offers_" + version);
			BufferedReader OFbr = new BufferedReader(OFfr);
			OFbr.readLine();
			while((line = OFbr.readLine()) != null)
			{
				String[] parts = line.split("\\s+");												
				metHeuOffer offer = new metHeuOffer();												//Create offer object.
				offer.id = Integer.parseInt(parts[0].trim());										//Read offer id.
				offer.origin.x = Double.parseDouble(parts[1].trim());								//Read offer origin_x.
				offer.origin.y = Double.parseDouble(parts[2].trim());								//Read offer origin_y.
				offer.destination.x = Double.parseDouble(parts[3].trim());							//Read offer destination_x.
				offer.destination.y = Double.parseDouble(parts[4].trim());							//Read offer destination_y.
				offer.e_time = Double.parseDouble(parts[5].trim());									//Read offer earliest departure time.
				offer.l_time = Double.parseDouble(parts[6].trim());									//Read offer latest arrival time.
				offer.max_duration = Double.parseDouble(parts[7].trim());							//Read offer max duration.
				offer.num_seats = Integer.parseInt(parts[8].trim());								//Read offer num of available seats.
				instance.offers.add(offer);															//Add offer to the instance list of offers.
			}
			OFbr.close();
			//Read trip requests from file and add them to instance.
			FileReader REfr = new FileReader("C:/Users/abood.mourad/workspace/AVstatic_31_01_2017/Data_Instances/" + city_instance + "_Requests_" + version);
			BufferedReader REbr = new BufferedReader(REfr);
			REbr.readLine();
			while((line = REbr.readLine()) != null)
			{
				String[] parts = line.split("\\s+");
				metHeuRequest request = new metHeuRequest();										//Create request object.
				request.id = Integer.parseInt(parts[0].trim());										//Read request id.
				request.origin.x = Double.parseDouble(parts[1].trim());								//Read request origin_x.
				request.origin.y = Double.parseDouble(parts[2].trim());								//Read request origin_y.
				request.destination.x = Double.parseDouble(parts[3].trim());						//Read request destination_x.
				request.destination.y = Double.parseDouble(parts[4].trim());						//Read request destination_y.
				request.e_time = Double.parseDouble(parts[5].trim());								//Read request earliest departure time.
				request.l_time = Double.parseDouble(parts[6].trim());								//Read request latest arrival time.
				request.max_walk = Double.parseDouble(parts[7].trim());								//Read request max walking distance.
				instance.requests.add(request);														//Add request to the instance list of requests.
			}
			REbr.close();
			//For every request, find the set of feasible pickup MPs and the set of feasible drop-off MPs.
			for(int r = 0 ; r < instance.requests.size() ; r++)
			{
				instance.requests.get(r).MP_Pick.add(new metHeuMeetingPoint(instance.requests.get(r).origin));
				instance.requests.get(r).MP_Drop.add(new metHeuMeetingPoint(instance.requests.get(r).destination));
				for(int mp = 0 ; mp < instance.meeting_points.size() ; mp++)						//For every request.
				{
					//If the travel time between request origin and MP is less than the max walk of the request, then add MP to the set of feasible pickup MPs of the request.
					if(Calculate_Distance(instance.requests.get(r).origin, instance.meeting_points.get(mp).location) <= instance.requests.get(r).max_walk)
					{
						instance.requests.get(r).MP_Pick.add(instance.meeting_points.get(mp));
					}
					//If the travel time between request destination and MP is less than the max walk of the request, then add MP to the set of feasible drop-off MPs of the request.
					if(Calculate_Distance(instance.requests.get(r).destination, instance.meeting_points.get(mp).location) <= instance.requests.get(r).max_walk)
					{
						instance.requests.get(r).MP_Drop.add(instance.meeting_points.get(mp));
					}
				}
			}
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}
	
	//Match Generation Algorithm (preprocessing) #Heuristic-1
	public static ArrayList<metHeuMatch> Match_Gen_Algo_V1(Data_Instance instance)
	{
		ArrayList<metHeuMatch> final_matches = new ArrayList<metHeuMatch>();	//Create an ArrayList to store the list of final matches to be returned by the algorithm.
		ArrayList<metHeuOffer> extended_offers = new ArrayList<metHeuOffer>();	//Create an ArrayList to store offers for direct trips and the artificial round trip offers to be constructed.
		//for each offer in the list of offers
		for(int o = 0 ; o < instance.offers.size() ; o++)
		{
			//if the offer is a direct trip then 
			if((instance.offers.get(o).origin.x != instance.offers.get(o).destination.x) || (instance.offers.get(o).origin.y != instance.offers.get(o).destination.y))
			{
				extended_offers.add(instance.offers.get(o)); 						 	//add offer (direct trip) to an extended list
			}
			else  																		//it is a round trip
			{
				metHeuPoint origin = new metHeuPoint(instance.offers.get(o).origin.x,instance.offers.get(o).origin.y);		//Initialize tmp origin with current offer origin, to be updated iteratively.
				//Sort riders with respect to their distance from tmp origin.
				ArrayList<metHeuRequest> sorted_requests = Sort_Riders(instance.requests, instance.offers.get(o).origin);
				//Establish a round trip offer by concatenating some riders as artificial owners.
				double total = 0;
				int count = 0;
				while((instance.offers.get(o).e_time + total < instance.offers.get(o).l_time) && (count < sorted_requests.size()))								//While it is still possible to add new artificial trips.
				{
					if((sorted_requests.get(count).e_time > instance.offers.get(o).e_time + Calculate_Travel_Time(Calculate_Distance(origin, sorted_requests.get(count).origin)) + total) && (sorted_requests.get(count).l_time + Calculate_Travel_Time(Calculate_Distance(sorted_requests.get(count).destination, instance.offers.get(o).destination)) < instance.offers.get(o).l_time))
					{
						//Create new artificial trip offer.
						metHeuOffer offer = new metHeuOffer();
						offer.id = instance.offers.get(o).id;
						offer.tmp_id = sorted_requests.get(count).id;
						offer.origin = sorted_requests.get(count).origin;
						offer.destination = sorted_requests.get(count).destination;
						offer.e_time = sorted_requests.get(count).e_time;
						offer.l_time = sorted_requests.get(count).l_time;
						offer.max_duration = (sorted_requests.get(count).l_time - sorted_requests.get(count).e_time) - max_duration;
						offer.num_seats = num_seats;
						offer.is_round = true;
						//Add the new artificial trip offer to the extended list of offers.
						extended_offers.add(offer);
						//Increase total duration.
						total += Calculate_Travel_Time(Calculate_Distance(origin, offer.origin)) + Calculate_Travel_Time(Calculate_Distance(offer.origin, offer.destination));
						//Assign new origin point.
						origin = new metHeuPoint(offer.destination.x, offer.destination.y);
						//Exclude rider from instance list and from sorted list.
						sorted_requests.remove(count);
						for(int y = 0 ; y < instance.requests.size() ; y++)
						{
							if(instance.requests.get(y).id == sorted_requests.get(count).id)
							{
								instance.requests.get(y).is_artificial = true;
								break;
							}
						}
						//Resort riders according to the new origin.
						sorted_requests = Sort_Riders(sorted_requests, origin);
						count = 0;
					}
					else
					{
						count++;
					}
				}
			}
		}
		//
		//ArrayList<metHeuMeetingArc> temp = new ArrayList<metHeuMeetingArc>();
		//For every request, find the set of feasible meeting point arcs. (Better to do this directly in the algorithm for memory reasons)
		for(int req = 0 ; req < instance.requests.size() ; req++)
		{
			if(instance.requests.get(req).is_artificial == false)
			{
				for(int p = 0; p < instance.requests.get(req).MP_Pick.size() ; p++)					//For every feasible pickup MP.
				{
					for(int d = 0 ; d < instance.requests.get(req).MP_Drop.size() ; d++)			//For every feasible drop-off MP.
					{
						//Add pick MP-drop MP arc.
						metHeuMeetingArc arc4 = new metHeuMeetingArc(instance.requests.get(req).MP_Pick.get(p), instance.requests.get(req).MP_Drop.get(d));
						instance.requests.get(req).MP_Arc.add(arc4);
						//temp.add(arc4);
					}
				}
			}
		}
		//System.out.print(temp.size());
		//Update instance offers with the extended list of offers.
		instance.offers.clear();
		for(int nf = 0 ; nf < extended_offers.size() ; nf++)
		{
			//System.out.println(extended_offers.get(nf).id);
			instance.offers.add(extended_offers.get(nf));
		}
		//for each offer in the extended list of offers
		for(int i = 0 ; i < instance.offers.size() ; i++)
		{
			Boolean matched = false; 
			//obtain compatible riders with the offer
			ArrayList<metHeuRequest> feasible_riders = new ArrayList<metHeuRequest>();	//Create an ArrayList to store the set of feasible riders for the offer.
			double best_savings;															//Create a variable to save the value of the best distance savings obtained for the triplet (offer, request, arc).
			//for each request (rider) in the list of requests
			for(int j = 0 ; j < instance.requests.size() ; j++)
			{
				if(instance.requests.get(j).is_artificial == false)
				{
					best_savings = 0;
					metHeuMatch best_match = new metHeuMatch();
					//for each rider meeting point arc
					for(int a = 0 ; a < instance.requests.get(j).MP_Arc.size() ; a++)
					{
						//Here is the feasibility check of every possible match.
						//if the match of the offer, the rider and the meeting point arc is feasible then:
						if((Math.max(instance.requests.get(j).e_time + Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).origin, instance.requests.get(j).MP_Arc.get(a).arc_pick.location)), instance.offers.get(i).e_time + Calculate_Travel_Time(Calculate_Distance(instance.offers.get(i).origin, instance.requests.get(j).MP_Arc.get(a).arc_pick.location))) <= Math.min(instance.requests.get(j).l_time - (Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_pick.location, instance.requests.get(j).MP_Arc.get(a).arc_drop.location)) + Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_drop.location, instance.requests.get(j).destination)) + (2*instance.requests.get(j).MP_Arc.get(a).arc_pick.service_time)), instance.offers.get(i).l_time - (Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_pick.location, instance.requests.get(j).MP_Arc.get(a).arc_drop.location)) + Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_drop.location, instance.offers.get(i).destination))))) && (Calculate_Travel_Time(Calculate_Distance(instance.offers.get(i).origin, instance.requests.get(j).MP_Arc.get(a).arc_pick.location)) + Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_pick.location, instance.requests.get(j).MP_Arc.get(a).arc_drop.location)) + Calculate_Travel_Time(Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_drop.location, instance.offers.get(i).destination)) + (2*instance.requests.get(j).MP_Arc.get(a).arc_drop.service_time) <= instance.offers.get(i).max_duration))
						{
							//store meeting point arc
							//compute distance savings
							double savings = Calculate_Distance(instance.offers.get(i).origin, instance.offers.get(i).destination) - (Calculate_Distance(instance.offers.get(i).origin, instance.requests.get(j).MP_Arc.get(a).arc_pick.location) + Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_pick.location, instance.requests.get(j).MP_Arc.get(a).arc_drop.location) + Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_drop.location, instance.offers.get(i).destination)) + (Calculate_Distance(instance.requests.get(j).origin, instance.requests.get(j).destination) - (Calculate_Distance(instance.requests.get(j).origin, instance.requests.get(j).MP_Arc.get(a).arc_pick.location) + Calculate_Distance(instance.requests.get(j).MP_Arc.get(a).arc_drop.location, instance.requests.get(j).destination)));
							//if the computed distance savings > best match distance savings found so far then:
							if(savings > best_savings)
							{
								best_savings = savings;										//update best match distance savings.
								//update best match.
								best_match.offer = instance.offers.get(i);
								best_match.requests.add(instance.requests.get(j));
								best_match.arc = instance.requests.get(j).MP_Arc.get(a);
								best_match.dist_save = best_savings*uplift;
								best_match.num_participants = 2;
							}
						}
					}
					if(best_savings > 0)													//If best match distance savings > 0 then
					{
						//Add match to participants' lists of matches Ei, Ej.
						instance.offers.get(i).feasible_matches.add(best_match);
						instance.requests.get(j).feasible_matches.add(best_match);
						feasible_riders.add(instance.requests.get(j)); 						//Append rider to feasible rider list
						final_matches.add(best_match);										//append match to match list
						matched = true;
					}
				}
			}
			if((!matched) && instance.offers.get(i).is_round)								//Add a single artificial owner match.
			{
				metHeuMatch art_match = new metHeuMatch();
				art_match.offer = instance.offers.get(i);
				metHeuRequest redundant = new metHeuRequest(instance.offers.get(i).tmp_id, instance.offers.get(i).origin, instance.offers.get(i).destination, instance.offers.get(i).e_time, instance.offers.get(i).l_time, 0);
				art_match.requests.add(redundant);
				art_match.arc = new metHeuMeetingArc(new metHeuMeetingPoint(instance.offers.get(i).origin), new metHeuMeetingPoint(instance.offers.get(i).destination));
				art_match.dist_save = 0;
				art_match.num_participants = 1;
				redundant.feasible_matches.add(art_match);
				instance.offers.get(i).feasible_matches.add(art_match);
				final_matches.add(art_match);
			}
			//System.out.print(feasible_riders.size());
			if(feasible_riders.size() > 1)										//if number of feasible riders > 1 then
			{
				ArrayList<metHeuMatch> matches_L1 = new ArrayList<metHeuMatch>(instance.offers.get(i).feasible_matches);
				//for k=2, .., offer.capacity 
				for(int k = 2 ; k < instance.offers.get(i).num_seats ; k++)
				{
					//Retrieve meeting point arcs.
					ArrayList<metHeuMatch> matches_ALL = new ArrayList<metHeuMatch>(instance.offers.get(i).feasible_matches);
					//Remove meeting point arcs that are feasible for less than k riders.
					for(int l = 0 ; l < matches_ALL.size() ; l++)
					{
						if(matches_ALL.get(l).requests.size() < k-1)
						{
							matches_ALL.remove(l);
						}
					}
					//Try to construct new matches with k riders.
					for(int l1 = 0 ; l1 < matches_ALL.size() ; l1++)
					{
						for(int l2 = 0 ; l2 < matches_L1.size() ; l2++)
						{
							//If a new match found (feasibility check) then:
							if((!matches_ALL.contains(matches_L1.get(l2))) && Is_Time_Compatible(instance.offers.get(i), matches_ALL.get(l1).requests, matches_L1.get(l2).requests, matches_ALL.get(l1).arc))
							{
								//Compute distance savings.
								double saving = Calculate_Distance(instance.offers.get(i).origin, instance.offers.get(i).destination) - (Calculate_Distance(instance.offers.get(i).origin, matches_ALL.get(l1).arc.arc_pick.location) + Calculate_Distance(matches_ALL.get(l1).arc.arc_pick.location, matches_ALL.get(l1).arc.arc_drop.location) + Calculate_Distance(matches_ALL.get(l1).arc.arc_drop.location, instance.offers.get(i).destination)) + (Calculate_Distance(matches_L1.get(l2).requests.get(0).origin, matches_L1.get(l2).requests.get(0).destination) - (Calculate_Distance(matches_L1.get(l2).requests.get(0).origin, matches_ALL.get(l1).arc.arc_pick.location) + Calculate_Distance(matches_ALL.get(l1).arc.arc_drop.location, matches_L1.get(l2).requests.get(0).destination)));
								for(int l3 = 0 ; l3 < matches_ALL.get(l1).requests.size() ; l3++)
								{
									saving += Calculate_Distance(matches_L1.get(l2).requests.get(l3).origin, matches_L1.get(l2).requests.get(l3).destination) - (Calculate_Distance(matches_L1.get(l2).requests.get(l3).origin, matches_ALL.get(l1).arc.arc_pick.location) + Calculate_Distance(matches_ALL.get(l1).arc.arc_drop.location, matches_L1.get(l2).requests.get(l3).destination));
								}
								//If distance savings > 0 then:
								if(saving > 0)
								{
									//Create new match with k participants.
									metHeuMatch match = new metHeuMatch();
									match.offer = matches_ALL.get(l1).offer;
									match.requests = new ArrayList<metHeuRequest>(matches_ALL.get(l1).requests);
									match.requests.add(matches_L1.get(l2).requests.get(0));
									match.arc = matches_ALL.get(l1).arc;
									match.dist_save = saving * uplift;
									match.num_participants = matches_ALL.get(l1).num_participants + 1;
									//Append match to match lists (offer, requests and final list).
									instance.offers.get(i).feasible_matches.add(match);
									for(int l4 = 0 ; l4 < match.requests.size() ; l4++)
									{
										instance.requests.get(match.requests.get(l4).id).feasible_matches.add(match);
									}
									final_matches.add(match);
								}
							}
						}
					}
				}
			}
		}
		//return match list
		return final_matches;													//Return the final list of matches.
	}
	
	//A method that checks the time compatibility of a match (participants).
	public static boolean Is_Time_Compatible(metHeuOffer offer, ArrayList<metHeuRequest> request1, ArrayList<metHeuRequest> request2, metHeuMeetingArc arc)
	{
		double e_o = offer.e_time + Calculate_Distance(offer.origin, arc.arc_pick.location);
		double l_o = offer.l_time - (Calculate_Distance(arc.arc_pick.location, arc.arc_drop.location) + Calculate_Distance(arc.arc_drop.location, offer.destination) + (2*arc.arc_pick.service_time));
		double max_e_r = request2.get(0).e_time + Calculate_Distance(request2.get(0).origin, arc.arc_pick.location);
		double min_l_r = request2.get(0).l_time - (Calculate_Distance(arc.arc_pick.location, arc.arc_drop.location) + Calculate_Distance(arc.arc_drop.location, request2.get(0).destination) + (2*arc.arc_pick.service_time));
		double tmp_e_r, tmp_l_r = 0;
		for(int r = 0 ; r < request1.size() ; r++)
		{
			tmp_e_r = request1.get(r).e_time + Calculate_Distance(request1.get(r).origin, arc.arc_pick.location);
			tmp_l_r = request1.get(r).l_time - (Calculate_Distance(arc.arc_pick.location, arc.arc_drop.location) + Calculate_Distance(arc.arc_drop.location, request1.get(r).destination) + (2*arc.arc_pick.service_time));
			max_e_r = Math.max(max_e_r, tmp_e_r);
			min_l_r = Math.min(min_l_r, tmp_l_r);
		}
		if(Math.max(e_o, max_e_r) <= Math.min(l_o, min_l_r))
		{
			return true;
		}
		return false;
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
		double lat1 = p1.x, lon1 = p1.y, lat2 = p2.x, lon2 = p2.y;
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        //System.out.print(R * c * uplift + "\n");
        return R * c * uplift;
		//return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
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
