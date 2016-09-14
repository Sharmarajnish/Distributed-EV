package src;

import java.util.ArrayList;

import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.randvar.PoissonGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;
import umontreal.iro.lecuyer.rng.RandomStream;

public class Flow {
	
	int fid;
	int V;
	
	int counter;
	
	int time;
	
	double arrReq; // the total energy request arrived at current slot
	
	double qf;
	double debet_queue; 
	
	double yf;   
	
	double xf;  //total charged energy from out side at curren slot;
	
	double debet2;
	
	ArrayList<EV> d_ev;
	
	ArrayList<Subflow> sfs;

	
	public PoissonGen pg;
	public NormalGen ng;
	
	public Flow(int id, int v){
		
		fid = id;
		V = v;
		
		arrReq =0;
		qf = 0;
		debet_queue =0;
		yf=0;
		time = 1;
		counter=0;
//		evList = new ArrayList<EV>();
		sfs = new ArrayList<Subflow>(fid+1);
		
		d_ev = new ArrayList<EV>();
		debet2 = 0;
		
		xf = 0;
	}
	
	
	public void genEV(int avg, String pro){
		
		RandomStream rd = new MRG31k3p();
		int arr_num=0;
		
		if(pro=="uni")
		{
				arr_num = avg;
		}else if(pro== "poi"){
			pg = new PoissonGen(rd,avg);
			arr_num = pg.nextInt();
		}else if(pro=="gau"){
			double sigma =1;
			ng = new NormalGen(rd,avg,sigma);
			arr_num = (int) Math.abs(ng.nextDouble());
		}
		
		ArrayList arrList = new ArrayList<EV>(arr_num);
		
		arrReq = 0;
		
		if(arr_num==0 ){
			System.out.println("arr_num is 0" );
			
			if(sfs.size()!=0)
			sfs.set(0, null);
			
			return;
		}
		
		
		for(int i=0;i<arr_num;i++){
			int p =10;
			EV ev = new EV(time,fid,p);
			
			ev.capacity=  16 + Math.random()*(32-16);
//			ev.capacity = 32;
			
			ev.ini_eng = ev.capacity*Math.random()*0.8;
//			ev.ini_eng = 12;
			
			ev.current_eng = ev.ini_eng;
			
			ev.fin_eng = ev.capacity*(Math.random()*0.2+ 0.8);
//			ev.fin_eng = 32;
			
			
			ev.req_eng = ev.fin_eng-ev.ini_eng;
			ev.id = counter;
			counter = counter+1;
			
			arrList.add(ev);
			arrReq = arrReq +ev.req_eng;
			
		}
		
		Subflow arr = new Subflow(fid,fid,V);
		arr.q = arrReq;
		arr.evList.addAll(arrList);
		
		if(sfs.size()==0){
			
			sfs.add(arr);
			System.out.println("first generate");
			
		}
		else
			sfs.set(0, arr);
		
		
		
		
	}
	
	
	
	/*
	 * schedule the subflows
	 */
	public void scheduling(double wind,double gamma){
		
		/*
		for(Subflow e: sfs){
			e.time = time;
		}
		*/
		
		xf =0;
		
		yf = 0;
		
		for(Subflow e:sfs){
			if(e!=null && e.evList.size()>0){
				e.time = time;
				e.schedule(wind/(double) fid,gamma);
				
				xf = xf+ e.x;

			}
//			yf = yf+e.y;
		}
	}
	
	public void queueEvolution(){
		
			
			int inisize = sfs.size();
			
			if(inisize ==0)
				return;
			
			for(int i=0; i<inisize && i<= fid;i++){
				Subflow e = sfs.get(i);
				
				if(e!=null){
					double tmp_q = e.q;
					e.q = e.q-e.y;
				
					if(e.q<0){
						
						System.out.println("q at evo "+tmp_q);
						System.out.println("y at evo"+e.y);
						
					}
					
				}
					
			}
			
			
			if(inisize<=fid){ //increase the size 
				Subflow tmp = sfs.get(0);
				sfs.add(tmp);
			}
			
			int size = sfs.size();
			for(int i=0;i<size-1;i++){
				sfs.set(size-i-1, sfs.get(size-i-2));
			}
			
			if(inisize==fid+1){
				Subflow debet = sfs.get(fid); 
			
				if(debet!=null){
					
					debet_queue = debet_queue+ debet.q;  //update debet queue
					
					
					
					d_ev.addAll(debet.evList);
					
					double a =0;
					for(EV e: debet.evList){
						a = a+ e.fin_eng-e.current_eng;
					}
					
					if(Math.abs(debet.q-a) > 0.01){
						System.out.println("error");
						System.out.println("q is "+ debet.q);
						System.out.println("a is "+ a);

					}
					
					debet2 = debet2+a;
					
				}
				
			}
			
	}
	
	public void run(int time, int avg, double wp, double gamma){
		
//		genEV(avg, "uni");
		
		genEV(avg, "poi");

		scheduling(wp, gamma);
		
		queueEvolution();
		
	}
	
}
