package GUI;

import Core.Game;
import Core.User;
import klt.KIFactory;

import java.util.ArrayList;
import java.util.List;

public class GuiMain {
	public static void main(String[] args) {
		
		List<User> users = new ArrayList<>();

		//users.add(new Human(1));
		//users.add(new Human(2));
		users.add(KIFactory.getKI_Q_FighterB(2, 0.0, true));
		users.add(KIFactory.getBomberman(1, 0.0, true));
		//users.add(KI_Factory.getKI_Avoidbomb_Zone(2));
		int boardsize = 15;
		int bombCounter = 8;
		int explosionArea = 4;
		int maxSteps = 300;
		long gameoverSleep = 0;
		long stepSleep = 0l;
		boolean paintGUI = false;
		
		new GuiStart(new Game(users, boardsize, bombCounter, explosionArea, maxSteps, stepSleep), gameoverSleep, paintGUI);
	}
}
