package dioco;

public class Pair {
	private int m;
	private int n;
	
	public Pair(int m, int n) {
		this.m = m;
		this.n = n;
	}
	
	public int getM() {
		return m;
	}
	
	public int getN() {
		return n;
	}
	
	@Override
	public boolean equals(Object p) {
		return p == this || (m == ((Pair)p).getM() && n == ((Pair)p).getN());
	}
	
	@Override
	public String toString() {
		return m+","+n;
	}
}
