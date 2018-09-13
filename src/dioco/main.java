package dioco;

public class main {

	public static void main(String[] args) {
		int k = 1;
		IOTS M = new IOTS("C:/Users/migue.DESKTOP-5QULJSK/Desktop/Programacion/TFG/src/files/Specification0.txt");
		IOTS N = new IOTS("C:/Users/migue.DESKTOP-5QULJSK/Desktop/Programacion/TFG/src/files/Implementation0.txt");
		M.extendMachine(k);
		FDA Imp = N.determinize();
		FDA Spe = M.determinize();
		System.out.println(Imp.productMachine(Spe) ?
			"V: Implementation DOES conform the specification\nunder the bounded"+k+" dioco relation" :
			"X: Implementation DOES NOT conform the specification\nunder the bounded"+k+" dioco relation");
	}

}
