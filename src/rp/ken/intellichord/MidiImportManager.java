package rp.ken.intellichord;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiImportManager {
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "CS", "D", "DS", "E", "F", "FS", "G", "GS", "A", "AS", "B"};

	public MidiImportManager(String fileName) throws Exception{
		Sequence sequence = MidiSystem.getSequence(new File(fileName));

		// 1拍は480tick 1拍は60/BPM秒なので10tick = 1250/BPM[ms]

		int bpm = 142;
		ArrayList<boolean[]> chordsBool = new ArrayList<>();
		ArrayList<boolean[]> firstNotes = new ArrayList<>();
		int trackNumber = 0;
		ArrayList<ArrayList<String>> melody = new ArrayList<>();
		ArrayList<ArrayList<Integer>> noteDurations = new ArrayList<>();
		for (Track track : sequence.getTracks()) {
			trackNumber++;
			System.out.println("Track " + trackNumber + ": size = " + track.size());
			System.out.println();
			int key = 0, octave = 0, note = 0;
			long onTime = 0, offTime = 0;
			String noteName = "";
			ArrayList<String> tmpm = new ArrayList<>();
			ArrayList<Integer> tmpd = new ArrayList<>();
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) message;
					if (sm.getCommand() == NOTE_ON) {
						key = sm.getData1();
						octave = (key / 12) - 1;
						note = key % 12;
						noteName = NOTE_NAMES[note];
						onTime = event.getTick();
						if (chordsBool.size() > (int) (onTime / 1920l)) {//十分
							boolean[] tmp = chordsBool.get((int) (onTime / 1920l));
							tmp[note] = true;
							chordsBool.set((int) (onTime / 1920l), tmp);
						} else {//足りない
							boolean[] tmp = new boolean[12];
							tmp[note] = true;
							chordsBool.add(tmp);
						}
						if (firstNotes.size() > (int) (onTime / 1920l)) {//十分
							boolean[] tmp = firstNotes.get((int) (onTime / 1920l));
							if (onTime % 1920 == 0) {
								tmp[note] = true;
							}
							firstNotes.set((int) (onTime / 1920l), tmp);
						} else {//足りない
							boolean[] tmp = new boolean[12];
							if (onTime % 1920 == 0) {
								tmp[note] = true;
							}
							firstNotes.add(tmp);
						}
						if (onTime - offTime != 0) {
							System.out.print(offTime + " A0 " + (onTime - offTime));
							tmpm.add("NO");
							tmpd.add((int) ((onTime - offTime) * 125 / bpm));
						}
						System.out.print(onTime + " " + noteName + octave + " ");
					} else if (sm.getCommand() == NOTE_OFF) {
						offTime = event.getTick();
						System.out.print((offTime - onTime));
						tmpm.add((noteName + octave));
						tmpd.add((int) ((offTime - onTime) * 125 / bpm));
					}
					System.out.println();
				}
			}
			int tmp = tmpd.size();
			tmpd.add(tmp);
			melody.add(tmpm);
			noteDurations.add(tmpd);
		}
		System.out.println();
		for (int i = 0; i < firstNotes.size(); i++) {
			boolean[] tmp = firstNotes.get(i);
			//System.out.println(Arrays.toString(firstNotes.get(i)));
			for (int j = 0; j < 12; j++) {
				if (tmp[j]) {
					System.out.println(NOTE_NAMES[j]);
					break;
				}
				if (j == 11) {
					System.out.println("NO");
				}
			}
		}
		System.out.println(melody);
		System.out.println(noteDurations);
	}
}
