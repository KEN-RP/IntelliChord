package rp.ken.intellichord;

public class Main {
	public static void main(String[] args) {
		MidiImportManager importManager;
		try {
			importManager = new MidiImportManager("sakura.mid");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
