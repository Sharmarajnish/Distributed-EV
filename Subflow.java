package src;
import umontreal.iro.lecuyer.randvar.NormalGen;
import umontreal.iro.lecuyer.randvar.PoissonGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;
import umontreal.iro.lecuyer.rng.RandomStream;


import java.util.ArrayList;

public class Subflow {
	
	int flowId;
	int ttl;
	
	double request_queue;
	double debet_queue;
	
	ArrayList<EV> evList;
	
	double x; //charing power from outside at current slot;
	
	double w;// wind power at current slot
	
	double y; // total charing power at current slot
	
	double q; //total unfulfilled energy request
	
	int time;
	

	
	int V;
	double gamma;
	
	public Subflow(int id, int r, int v){
		
		flowId = id;
		ttl=r;
		
		request_queue=0;
		debet_queue=0;
		
		evList = new ArrayList<EV>();
		V =v;
		gamma=0.01;
		
		x=0;
		w=0;
		y=0;
		q=0;
	}
	
	
	
	public void schedule(double wind, double g){
		
		w=wind;
		gamma = g;
		x=0;
		
		double thr = V*gamma+w-q;
//		if(q>20)
//			System.out.println("q is "+q);
		
		if(thr<=0){
			for(EV e: evList){
				double p = min(e.p_max,(q-w)/(double)evList.size());
				e.charging(p,1,time);
				x =x+ e.current_cp-w/evList.size() ;
			}
		}else
		{
			for(EV e: evList){
				double p = -e.p_max;
				p=0; //p
				e.charging(p, 1,time);
				x =x+ e.current_cp-w/evList.size() ;

			}
		}
		
		y = x+wind;
		double tmp = q;
		
		
		for(EV e: evList)
			e.updateTtl();
		
		if(ttl>0)
			ttl = ttl-1;
	}
	
	public void rqEnvolve(){
		
	}
	

	
	public double min(double x, double y){
		if( x<y)
			return x;
		else 
			return y;
	}

}
