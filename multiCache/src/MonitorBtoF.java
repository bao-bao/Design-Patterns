

public class MonitorBtoF {
	int CPUid;
	int BlockNum;
	int BlockStatus;
	int BlockStoNum = -1;
	int StCStorageNum = -1;
	int CtSStorageNum = -1;
	int BusWireStatus = 0;
	
	MonitorBtoF(int id, int bnum, int bsta, int bstonum, int stcstonum, int ctsstonum, int bws) {
		CPUid = id;
		BlockNum = bnum;
		BlockStatus = bsta;
		BlockStoNum = bstonum;
		
		StCStorageNum = stcstonum;
		CtSStorageNum = ctsstonum;
		
		BusWireStatus = bws;
	}

}

