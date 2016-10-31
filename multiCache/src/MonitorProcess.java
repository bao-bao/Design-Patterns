
public class MonitorProcess {
	CPU[] carray;
	int VisitCPU = 0;//A = 1; B = 2; C = 3; D = 4;
	int VisitAdd = 0;
	int RorW = 0;//READ = 0;WRITE = 1;
	int BlockNum = 0;
	int BWStatus = 0;
	int StCStoNum = -1;
	int CtSStoNum = -1;
	int current = 0;
	int cpuSize, cacheSize;
	MonitorBtoF[] process;

	private static MonitorProcess monitor;

	private MonitorProcess(int cpuSize, int cacheSize) {
		carray = new CPU[cpuSize];
		process = new MonitorBtoF[cpuSize];
		for (int i = 0;i < cpuSize;i++) {
			carray[i] = new CPU(cacheSize);
		}
		this.cpuSize = cpuSize;
		this.cacheSize = cacheSize;
	}

	public static void initMonitor(int cpuSize, int cacheSize) {
		monitor = new MonitorProcess(cpuSize, cacheSize);
	}

	public static MonitorProcess getMonitor() {
		if (monitor == null)
			monitor = new MonitorProcess(4, 4);
		return monitor;
	}

	public MonitorBtoF[] getProcess() {
		return process;
	}

	public void process(Code code) {
		VisitCPU = code.visitCPU;
		VisitAdd = code.visitAddBlockID;
		RorW = code.codeType;
		BlockNum = VisitAdd % cacheSize;

		processCPU();
		setProcess();
		processBus();
	}

	private void reset() {
		VisitCPU = 0;
		VisitAdd = 0;
		RorW = 0;
		BWStatus = 0;
		StCStoNum = -1;
		CtSStoNum = -1;
	}

	private void processCPU() {
		for(int i = 1; i <= cpuSize; i++) {
			if((VisitCPU == i) && (RorW == 0)) {//read
				if(carray[i - 1].CStatus[BlockNum] == 1) {//status = I
					
					BWStatus = 1;//BusWireStatus, send message to bus wire
					StCStoNum = VisitAdd;
					CtSStoNum = -1;
					carray[i - 1].CStatus[BlockNum] = 2;//BlockStatus, change status of block
					carray[i - 1].CStorage[BlockNum] = VisitAdd;//BlockStoNum, change storage of block
					
					current = i;
					break;
				}
				
				if(carray[i - 1].CStatus[BlockNum] == 2) {//status = S
					if(VisitAdd == carray[i - 1].CStorage[BlockNum]) {
						BWStatus = 3;
						StCStoNum = VisitAdd;
						CtSStoNum = -1;
						carray[i - 1].CStatus[BlockNum] = carray[i - 1].CStatus[BlockNum];
						carray[i - 1].CStorage[BlockNum] = carray[i - 1].CStorage[BlockNum];//status not changed
					}
					else {//replace, RdMiss
						BWStatus = 1;
						StCStoNum = VisitAdd;
						CtSStoNum = -1;
						carray[i - 1].CStatus[BlockNum] = 2;
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					
					current = i;
					break;
				}
				
				if(carray[i - 1].CStatus[BlockNum] == 3) {//status = M
					if(VisitAdd == carray[i - 1].CStorage[BlockNum]) {//mingzhong
						BWStatus = 3;
						CtSStoNum = -1;
						StCStoNum = -1;
						carray[i - 1].CStatus[BlockNum] = 3;//BlockStatus, change status of block
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					else {//replace, RdMiss
						BWStatus = 1;
						CtSStoNum = carray[i - 1].CStorage[BlockNum];
						StCStoNum = VisitAdd;
						carray[i - 1].CStatus[BlockNum] = 2;
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					
					current = i;
					break;
				}
				
			}
			if((VisitCPU == i) && (RorW == 1)) {//write
				if(carray[i - 1].CStatus[BlockNum] == 1) {//status = I
					
					BWStatus = 2;//send message to bus wire
					StCStoNum = VisitAdd;
					CtSStoNum = -1;
					carray[i - 1].CStatus[BlockNum] = 3;//change status of block
					carray[i - 1].CStorage[BlockNum] = VisitAdd;
					
					current = i;
					break;
				}
				
				if(carray[i - 1].CStatus[BlockNum] == 2) {//status = S
					if(VisitAdd == carray[i - 1].CStorage[BlockNum]) {//mingzhong
						BWStatus = 4;//write
						StCStoNum = -1;
						CtSStoNum = -1;
						carray[i - 1].CStatus[BlockNum] = 3;//change status of block
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					else {//replace
						BWStatus = 2;//WtMiss
						StCStoNum = VisitAdd;
						CtSStoNum = -1;
						carray[i - 1].CStatus[BlockNum] = 3;
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					
					current = i;
					break;
				}
			
		
				if(carray[i - 1].CStatus[BlockNum] == 3) { //status = M
					if(VisitAdd == carray[i - 1].CStorage[BlockNum]) { //mingzhong
						BWStatus = 4;
						CtSStoNum = carray[i - 1].CStatus[BlockNum];
						StCStoNum = VisitAdd;
						carray[i - 1].CStatus[BlockNum] = 3; //BlockStatus, change status of block
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					else {//replace, WtMiss
						BWStatus = 2;
						CtSStoNum = carray[i - 1].CStorage[BlockNum];
						StCStoNum = VisitAdd;
						carray[i - 1].CStatus[BlockNum] = 3;
						carray[i - 1].CStorage[BlockNum] = VisitAdd;
					}
					
					current = i;
					break;
				}
			}
		}
	}
		
	public void setProcess() {
		MonitorBtoF btf = new MonitorBtoF(VisitCPU, BlockNum, carray[VisitCPU - 1].CStatus[BlockNum],
				carray[VisitCPU - 1].CStorage[BlockNum], StCStoNum, CtSStoNum, BWStatus);
		process[VisitCPU - 1] = btf;
	}
	
	private void processBus() {
		if(BWStatus == 4) {//write
			for(int j = 1; j <= cpuSize; j++) {
				if(j != current) {
					VisitCPU = j;
					if(carray[VisitCPU - 1].CStorage[BlockNum] == VisitAdd) {
						carray[VisitCPU - 1].CStatus[BlockNum] = 1;
						carray[VisitCPU - 1].CStorage[BlockNum] = -1;
						StCStoNum = -1;
						CtSStoNum = -1;
						BWStatus = 5;
					}
					else {
						carray[VisitCPU - 1].CStatus[BlockNum] = carray[VisitCPU - 1].CStatus[BlockNum];
						carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
						StCStoNum = -1;
						CtSStoNum = -1;
						BWStatus = 0;
					}
					setProcess();
				}
				
			}
		}
		if(BWStatus == 2) {//WtMiss
			for(int j = 1; j <= cpuSize; j++) {
				if(j != current) {
					VisitCPU = j;
					if(carray[VisitCPU - 1].CStorage[BlockNum] == VisitAdd) {
						BWStatus = 5;
						if(carray[VisitCPU - 1].CStatus[BlockNum] == 3) {//M
							StCStoNum = -1;
							CtSStoNum = carray[VisitCPU - 1].CStorage[BlockNum];
							carray[VisitCPU - 1].CStatus[BlockNum] = 1;
							carray[VisitCPU - 1].CStorage[BlockNum] = -1;
						}
						else {//S
							carray[VisitCPU - 1].CStatus[BlockNum] = 1;
							carray[VisitCPU - 1].CStorage[BlockNum] = -1;
							StCStoNum = -1;
							CtSStoNum = -1;
						}
					}
					else {
						carray[VisitCPU - 1].CStatus[BlockNum] = carray[VisitCPU - 1].CStatus[BlockNum];
						carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
						StCStoNum = -1;
						CtSStoNum = -1;
						BWStatus = 0;
					}
					setProcess();
				}
				
			}
		}
		if(BWStatus == 3) {//read
			for(int j = 1; j <= cpuSize; j++) {
				if(j != current) {
					VisitCPU = j;
					carray[VisitCPU - 1].CStatus[BlockNum] = carray[VisitCPU - 1].CStatus[BlockNum];
					carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
					StCStoNum = -1;
					CtSStoNum = -1;
					BWStatus = 0;
					
					setProcess();
				}
				
			}
		}
				
		if(BWStatus == 1) {//RdMiss
			//System.out.println("true");
			
			for(int j = 1; j <= cpuSize; j++) {
				if(j != current) {
					VisitCPU = j;
					
					if(carray[VisitCPU - 1].CStorage[BlockNum] == VisitAdd) {
						if(carray[VisitCPU - 1].CStatus[BlockNum] == 3) {
							carray[VisitCPU - 1].CStatus[BlockNum] = 2;
							carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
							CtSStoNum = carray[VisitCPU - 1].CStorage[BlockNum];
							StCStoNum = -1;
							BWStatus = 0;
						}
						else {
							carray[VisitCPU - 1].CStatus[BlockNum] = carray[VisitCPU - 1].CStatus[BlockNum];
							carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
							StCStoNum = -1;
							CtSStoNum = -1;
							BWStatus = 0;
						}
						
					}
					else {
						carray[VisitCPU - 1].CStatus[BlockNum] = carray[VisitCPU - 1].CStatus[BlockNum];
						carray[VisitCPU - 1].CStorage[BlockNum] = carray[VisitCPU - 1].CStorage[BlockNum];
						StCStoNum = -1;
						CtSStoNum = -1;
						BWStatus = 0;
					}
					setProcess();
				}

			}
		}
	}		
						
}
	
	 

