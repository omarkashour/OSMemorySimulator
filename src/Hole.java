
public class Hole implements Comparable<Hole>{
	private int holeSize;
	private int holeStartAddress = -1;

	public Hole(int holeSize, int holeStartLocation) {
		this.holeSize = holeSize;
		this.holeStartAddress = holeStartLocation;
	}

	public int getHoleSize() {
		return holeSize;
	}

	public void setHoleSize(int holeSize) {
		this.holeSize = holeSize;
	}

	public int getHoleStartAddress() {
		return holeStartAddress;
	}

	public void setHoleStartAddress(int holeStartAddress) {
		this.holeStartAddress = holeStartAddress;
	}

	@Override
	public int compareTo(Hole o) {
		return Integer.compare(this.holeStartAddress, o.getHoleStartAddress());
	}


}
