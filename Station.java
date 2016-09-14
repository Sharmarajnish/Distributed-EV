package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Station {
	
	int flowNum;
	ArrayList<Flow> flows;
	
	int id;
	
	int avg; // average speed of per flow
	
	double request;
	double wind;
	double debt;
	int V;
	
	double[] Cost; // cost at each time slot: x(t)\gamma(t)
	double[] Gamma;
	
	
	int T;
	
	int currentTime;
	
	public Station(int t,int v, int average){
		T = t;
		request =0;
		wind =0;
		debt =0;
		flows = new ArrayList<Flow>(flowNum);
		V=v;
		currentTime = 1;
		avg = average;
		id = 0;
		
		Cost = new double[T];
		Gamma = new double[T];
	}
	
	public void run(){
		
		
		//initiate flows; delay tolerance from 4-12;
		
		if(currentTime==1){
			
			int startNum =6;
			int endNum=12;
			flowNum = endNum-startNum+1;
			
			for(int f=startNum;f<endNum+1;f++){
//				System.out.println("create flow: "+f);
				Flow fl = new Flow(f,V);
				flows.add(fl);
			}
			
		} //end of T==1, initiation
		
		
		
		for(currentTime=1;currentTime<=T;currentTime++){
			// set wind power, gamma
			
			// update charging
			
			request = 0;
			
			wind= generateWind();
			setGamma(currentTime);
			
			int count =0;
			
//			if(currentTime==8)
//					System.out.println("slot 6");
			debt = 0;
			
			Cost[currentTime-1] =0;
			
			for(Flow e: flows){
				
				e.time = currentTime;
				
				e.run(currentTime, avg, wind/flowNum,Gamma[currentTime-1]);
				
				debt = debt+e.debet_queue;
				
				Cost[currentTime-1] = Cost[currentTime-1]+ e.xf*Gamma[currentTime-1];
				
				//calculate the total unfullfied request
				
//				System.out.println("calculating on flow "+ e.fid);
				
				for(int i=1;i<e.sfs.size()-2;i++){
					
					Subflow se = e.sfs.get(i);
					
					if(se!=null)
						request = request+se.q;
				}
				
				count = count+1;
				
//				if(count>1)
//					System.out.println("count "+ count);
				
			}
			
			System.out.println("time slot "+ currentTime+
					"-- debt "+ debt + " request " + request );
			
			
			
		} //end of time round
		
	}
	
	
	
	public double generateWind(){
		
		double w= 10*Math.random();
		return w;
	}
	
	public void setGamma(int t){
		
		if(t>0 && t<=T)
			Gamma[t-1] = 0.01+Math.random()*0.001;
		else return;
		
	}
	
	public static void main(String[] str){
		
		int timespan = 1000;
		int v_value = 500;
		int avg = 2;
		
		Station sta = new Station(timespan,v_value,avg);
		sta.run();
		
		double db2 =0;

		double avg_cr=0;
		double total_ev=0;
		double total_uev=0;
		
		
		for(Flow e:sta.flows){
			System.out.println("flow "+e.fid+" have "+e.counter+" EVs \n"
					+e.d_ev.size()+  "  in debet ");
			
			double real_debt=0;
			
			
			for(EV ev: e.d_ev){
				total_ev = total_ev+1;
				double d=ev.fin_eng-ev.current_eng;
				
				if(d>0)
					total_uev = total_uev+1;
				
				double ratio = d/ev.req_eng;
				
//				System.out.println("ev "+ev.id+" reqs "
//						+d);
				avg_cr = avg_cr+ratio;
				
				real_debt = real_debt+d;
			}
			
			System.out.println("real debt is "+ real_debt);
			
			db2 = 	db2+e.debet2;

			System.out.println("debt2 is "+ db2);

		}
		
		
		System.out.println("average charging finish rate is "+ (1-avg_cr/total_ev));
		
		System.out.println("unfull filled rate "+ total_uev/total_ev);
		
		
		
		/*
		 * write into files
		 */
		String fn = "Cost-V-"+sta.V+"-T-"+sta.T+".txt";
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(fn));
			
			double ctmp =0;
			
			for(int j=0;j<sta.T;j++){
				double tmp = sta.Cost[j];
				ctmp +=tmp;
				
				System.out.println("slot " + j+ " accumulated cost is "+ ctmp);
				
				write.write(ctmp+"\n");
			}
			
			String line = "average charging finish rate is "+ (1-avg_cr/total_ev)+"\n";
			write.write(line);
			write.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		
		
	} //end of main func.
	
	
	
}
