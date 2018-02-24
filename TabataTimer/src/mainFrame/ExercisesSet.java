package mainFrame;

import java.util.LinkedList;


public class ExercisesSet {
    
    public final LinkedList<String> exercies;
    private int iterator;
    private int length;
    
    public ExercisesSet(LinkedList<String> exercises) {
	this.exercies = exercises;
	this.iterator = 0;
	this.length = exercises.size();
    }
    
    public String getNextSong() {
	String next = exercies.get(iterator);
	iterator = (iterator + 1) < length ? iterator + 1 : 0;
	return next;
    }
    
    
}
