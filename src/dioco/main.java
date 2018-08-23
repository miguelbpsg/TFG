package dioco;

public class main {

	public static void main(String[] args) {
		int k = 4;
		IOTS M = new IOTS("C:/Users/migue.DESKTOP-5QULJSK/Desktop/Programacion/TFG/src/files/Specification2.txt");
		IOTS N = new IOTS("C:/Users/migue.DESKTOP-5QULJSK/Desktop/Programacion/TFG/src/files/Implementation2.txt");
		M.extendMachine(k);
		System.out.println(Dioco.productMachine(M,N) ?
			"V: Implementation DOES conform the specification\nunder the bounded"+k+" dioco relation" :
			"X: Implementation DOES NOT conform the specification\nunder the bounded"+k+" dioco relation");
	}

}
