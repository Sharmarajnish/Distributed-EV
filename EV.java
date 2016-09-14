package src;

public class EV {
	
	int arr_time;
	int delay;
	int leave_time;
	int ttl;
	
	int flowId;
	
	int id;
	
	double capacity;
	double ini_eng;
	double req_eng;
	double fin_eng;
	double current_eng;
	double p_max;
	double current_cp;  //changring power
	
	double[] cv;
	
	
	public EV(int time, int id,double p){
		
		arr_time = time;
		flowId = id;
		delay = id;
		leave_time = arr_time + delay;
		ttl = id;
		
		ini_eng=0;
		req_eng=0;
		fin_eng=0;
		current_eng=ini_eng;
		p_max = p;
		
		current_cp=0;
		
		cv = new double[20];
		id =0;
	
	}
	
	
	public void updateTtl(){
		if(ttl>0)
			ttl=ttl-1;
	}
	
	/*
	 * set curent charing power, 
	 * and update current_eng;
	 */
	public void charging(double cp,double interval, int time){
		double charging = cp*interval;
		
		
		if(cp>=0){
			if(current_eng+charging>fin_eng){
				current_cp = fin_eng-current_eng;
				current_eng = fin_eng;
			}else{
				current_cp = charging;
				current_eng = current_eng+ current_cp;
			}
		}
		if(cp<0){
			if(current_eng+charging<0){
				current_cp = -current_eng;
				current_eng =0;
			}else{
				current_cp = charging;
				current_eng = current_eng+current_cp;
			}
		}
		
		cv[time-arr_time] = current_cp;
		
		if(current_cp <0)
			System.out.println("bingo");
		
	}
	
	
	
	
	
	
	

}
