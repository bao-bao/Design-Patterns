
public class CPU {
	private int blockSize;
	int[] CStatus, CStorage;
	CPU(int size) {
		this.blockSize = size;
		CStatus = new int[size];
		CStorage = new int[size];
		for (int i = 0;i < size;i++) {
			CStatus[i] = 1;
			CStorage[i] = -1;
		}
	}
}
