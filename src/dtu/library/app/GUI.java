package dtu.library.app;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class GUI extends JFrame implements ActionListener{//Initialization of most parts of the GUI:
	protected JTextArea informationLabel = new JTextArea();
	protected JLabel listOfProjectsL = new JLabel("All current projects:");

	protected JTextArea projectsListAdditional = new JTextArea();


	protected static GUI gUI = new GUI();
	
	protected DataBase dataBase = new DataBase();

	//The sheets of options
	protected MainSheet mainSheet = new MainSheet(this);
	protected ChangeOperationSheet changeOptSheet = new ChangeOperationSheet(this);
	protected adminOptionsSheet adminOpt = new adminOptionsSheet();

	//List of the projects
	protected ArrayList<Project> allCurrentProjects = dataBase.getProjects();
	protected ArrayList<JTextField> allCurrentProjectsList = new ArrayList<JTextField>();
	
	//The current worker
	protected Worker worker = dataBase.getWorkers().get(0);

	public GUI() {
		
		//Some setup log area etc.
		informationLabel.setEditable(false);
		informationLabel.setBackground(Color.LIGHT_GRAY);
		informationLabel.setName("Feedback log:");
		informationLabel.setForeground(Color.RED);
		informationLabel.setLineWrap(true);
		JScrollPane infoPane = new JScrollPane(informationLabel);


		//The layout of the main part is a grid layout with 2 horizontal slots
		getContentPane().setLayout(new GridLayout(1,2));

		//ActionListener is added to all buttons
		mainSheet.startNewProjectB.addActionListener(this);
		mainSheet.startNewActivityB.addActionListener(this);
		mainSheet.addTimeB.addActionListener(this);
		mainSheet.startOnProjectB.addActionListener(this);
		mainSheet.startOnActivityB.addActionListener(this);

		changeOptSheet.setProjectLeadB.addActionListener(this);
		changeOptSheet.changeActivityNameB.addActionListener(this);
		changeOptSheet.changeTimeEstimateB.addActionListener(this);
		changeOptSheet.changeActivityDescriptionB.addActionListener(this);
		changeOptSheet.changeActivityConditionB.addActionListener(this);
		
		adminOpt.makeNewWorkerB.addActionListener(this);
		adminOpt.changeToOtherWorkerB.addActionListener(this);
		adminOpt.listWorkersB.addActionListener(this);
		adminOpt.listProjectsB.addActionListener(this);

		//The list of all current projects is setup
		JPanel projectsList = new JPanel();
		projectsList.setLayout(new GridLayout(3,1));

		reDrawProjectList();

		JScrollPane projectsListScroll = new JScrollPane(projectsListAdditional);
		projectsList.add(listOfProjectsL);
		projectsList.add(projectsListScroll);
		projectsList.add(infoPane);
		listOfProjectsL.setSize(50,100);
		projectsListAdditional.setSize(700, 150);

		JTabbedPane options = new JTabbedPane();
		options.add("Main options", mainSheet);
		options.add("Change options", changeOptSheet);
		options.add("DEV AND ADMIN", adminOpt);

		//Adding it all to the main GUI
		getContentPane().add(projectsList);
		getContentPane().add(options);

	}

	public static void main(String[] args) {

		gUI.setTitle("Software project");
		gUI.setSize(1000, 800);
		gUI.setResizable(false);
		gUI.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mainSheet.startNewProjectB) {
			if (mainSheet.newProjectNameF != null && !mainSheet.newProjectNameF.getText().equals("")) {
				allCurrentProjects.add(new Project(mainSheet.newProjectNameF.getText()));
				projectsListAdditional.append(allCurrentProjects.get(allCurrentProjects.size()-1).getName() + " with ID: " + allCurrentProjects.get(allCurrentProjects.size()-1).getID() + "\n");
				projectsListAdditional.append("With activities: \n");
			} else {
				informationLabel.append("Must give a project a name \n");
			}

			if (mainSheet.newProjectLeadF != null && !mainSheet.newProjectLeadF.getText().equals("")) {
				try { 
					allCurrentProjects.get(allCurrentProjects.size()-1).setProjectLeader(getCurrentWorker(), findWorker(mainSheet.newProjectLeadF.getText()));
				} catch (Exception error) {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			mainSheet.newProjectNameF.setText("");
			mainSheet.newProjectLeadF.setText("");


		}else if (e.getSource() == mainSheet.startNewActivityB) {
			Project current = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				if (allCurrentProjects.get(i).getID().equals(mainSheet.newActivityProjectF.getText()) || allCurrentProjects.get(i).getName().equals(mainSheet.newActivityProjectF.getText())) {
					current = allCurrentProjects.get(i);
					try {
						current.addActivity(worker, new Activity(mainSheet.newActivityNameF.getText(), Double.parseDouble(mainSheet.newActivityTimeEstF.getText()), current));
					} catch (Exception error) {
						informationLabel.append(error.getMessage() + "\n");
					}
				}
			}

			if (current == null) {
				informationLabel.append("No project with that ID or name found \n");
			} 
			
			reDrawProjectList();

			mainSheet.newActivityNameF.setText("");
			mainSheet.newActivityProjectF.setText("");
			mainSheet.newActivityTimeEstF.setText("");

		}else if (e.getSource() == mainSheet.addTimeB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(mainSheet.addTimeActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						break;
					}
				}
			}
			try {
				currentA.addTimeSpent(Double.parseDouble(mainSheet.addTimeFromF.getText()), Double.parseDouble(mainSheet.addTimeToF.getText()));
			} catch (Exception error) {
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + mainSheet.addTimeActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			reDrawProjectList();
			
			mainSheet.addTimeActivityF.setText("");
			mainSheet.addTimeFromF.setText("");
			mainSheet.addTimeToF.setText("");

		}else if (e.getSource() == mainSheet.startOnProjectB) {
			Project current = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				if (allCurrentProjects.get(i).getID().equals(mainSheet.workOnProjectProjectF.getText()) || allCurrentProjects.get(i).getName().equals(mainSheet.workOnProjectProjectF.getText())) {
					current = allCurrentProjects.get(i);
					try {
						current.addWorker(worker);
						informationLabel.append("Started working on project: " + mainSheet.workOnProjectProjectF.getText() + "\n");
					} catch (Exception error) {
						informationLabel.append(error.getMessage() + "\n");
					}
				}
			}

			if (current == null) {
				informationLabel.append("No project with that ID or name found \n");
			} 
			mainSheet.workOnProjectProjectF.setText("");

		}else if (e.getSource() == mainSheet.startOnActivityB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(mainSheet.workOnActivityActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						break;
					}
				}
			}

			try {
				currentA.addWorker(worker);
				informationLabel.append("Started working on activity: " + mainSheet.workOnActivityActivityF.getText() + "\n");
			} catch (Exception error) {
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + mainSheet.workOnActivityActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}

			mainSheet.workOnActivityActivityF.setText("");
		} else if (e.getSource() == changeOptSheet.setProjectLeadB) {
			Project current = null;
			Worker currentW = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				if (allCurrentProjects.get(i).getID().equals(changeOptSheet.setProjectLeadProjectF.getText()) || allCurrentProjects.get(i).getName().equals(changeOptSheet.setProjectLeadProjectF.getText())) {
					current = allCurrentProjects.get(i);
				}
			}

			for (int i = 0; i < dataBase.getWorkers().size(); i++) {
				if (dataBase.getWorkers().get(i).getID().equals(changeOptSheet.setProjectLeadWorkerF.getText())) {
					currentW = dataBase.getWorkers().get(i);
				}
			}

			try {
				current.setProjectLeader(worker, currentW);
				informationLabel.append("Succesfully change projectleader \n");
			} catch (Exception error) {
				if (current == null) {
					informationLabel.append("No project with that ID/name found \n");
				} else if (currentW == null) {
					informationLabel.append("No worker with that ID found \n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			changeOptSheet.setProjectLeadWorkerF.setText("");
			changeOptSheet.setProjectLeadProjectF.setText("");

		} else if (e.getSource() == changeOptSheet.changeActivityNameB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(changeOptSheet.changeActivityNameActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						break;
					}
				}
			}
			
			try {
				currentA.changeActivityName(worker, changeOptSheet.changeActivityNameNameF.getText());
				informationLabel.append("Succesfully changed activity name \n");
			} catch (Exception error) {
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + changeOptSheet.changeActivityNameActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			reDrawProjectList();
			
			changeOptSheet.changeActivityNameActivityF.setText("");
			changeOptSheet.changeActivityNameNameF.setText("");
			
		} else if (e.getSource() == changeOptSheet.changeTimeEstimateB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(changeOptSheet.changeTimeEstimateActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						i = allCurrentProjects.size();
						break;
					}
				}
			}
			
			try {
				currentA.changeActivityTime(worker, Double.parseDouble(changeOptSheet.changeTimeEstimateTimeEstimateF.getText()));
				informationLabel.append("Succesfully updated the time estimate on the activity \n");
			} catch (Exception error) {
				informationLabel.append(error.getStackTrace().toString());
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + changeOptSheet.changeTimeEstimateActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			reDrawProjectList();
			
			changeOptSheet.changeTimeEstimateActivityF.setText("");
			changeOptSheet.changeTimeEstimateTimeEstimateF.setText("");

		} else if(e.getSource() == changeOptSheet.changeActivityDescriptionB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(changeOptSheet.changeActivityDescriptionActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						break;
					}
				}
			}
			
			try {
				currentA.changeActivityDescription(worker, changeOptSheet.changeActivityDescriptionDescriptionF.getText());
				informationLabel.append("Succesfully updated the description for the activity \n");
			} catch (Exception error) {
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + changeOptSheet.changeActivityDescriptionActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			changeOptSheet.changeActivityDescriptionActivityF.setText("");
			changeOptSheet.changeActivityDescriptionDescriptionF.setText("");
			
		} else if (e.getSource() == changeOptSheet.changeActivityConditionB) {
			Activity currentA = null;

			for (int i = 0; i < allCurrentProjects.size(); i++) {
				for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
					if (allCurrentProjects.get(i).getActivities().get(j).getName().equals(changeOptSheet.changeActivityConditionActivityF.getText())) {
						currentA = allCurrentProjects.get(i).getActivities().get(j);
						break;
					}
				}
			}
			
			try {
				currentA.changeActivityCondition(worker, changeOptSheet.changeActivityConditionConditionF.getText());
				informationLabel.append("Succesfully change the condition for the activity \n");
			} catch (Exception error) {
				if (currentA == null) {
					informationLabel.append("No activity found with name: " + changeOptSheet.changeActivityDescriptionActivityF.getText() + "\n");
				} else {
					informationLabel.append(error.getMessage() + "\n");
				}
			}
			
			changeOptSheet.changeActivityConditionActivityF.setText("");
			changeOptSheet.changeActivityConditionConditionF.setText("");
		} else if (e.getSource() == adminOpt.makeNewWorkerB) {
			dataBase.addWorker(new Worker(adminOpt.makeNewWorkerF.getText()));
			adminOpt.makeNewWorkerF.setText("");
			
		} else if (e.getSource() == adminOpt.changeToOtherWorkerB) {
			Worker foundW = null;
			for (int i = 0; i < dataBase.getWorkers().size(); i++) {
				if (dataBase.getWorkers().get(i).getID().equals(adminOpt.changeToOtherWorkerF.getText())) {
					foundW = dataBase.getWorkers().get(i);
					break;
				}
			}
			
			if(foundW == null) {
				informationLabel.append("No worker with that ID found \n");
			} else {
				worker = foundW;
			}
			
			adminOpt.changeToOtherWorkerF.setText("");
				
		} else if (e.getSource() == adminOpt.listWorkersB) {
			for (int i = 0; i < dataBase.getWorkers().size(); i++) {
				informationLabel.append("Worker " + i + " : " + dataBase.getWorkers().get(i).getID() + " ");
			}
			informationLabel.append("\n");
			
		} else if (e.getSource() == adminOpt.listProjectsB) {
			for (int i = 0; i < dataBase.getProjects().size(); i++) {
				informationLabel.append("Project " + i + " : " + dataBase.getProjects().get(i).getName() + " with ID: " + dataBase.getProjects().get(i).getID());
			}
			informationLabel.append("\n");
		}
	}

	private Worker findWorker(String iD) throws Exception {
		for (int i = 0; i < dataBase.getWorkers().size(); i++) {
			if (dataBase.getWorkers().get(i).getID().equals(iD)) {
				return dataBase.getWorkers().get(i);
			}
		}
		throw new OperationNotAllowedException("Cannot find worker with iD: " + iD);
	}
	
	private void reDrawProjectList() {
		projectsListAdditional.setText("");
		for (int i = 0; i < allCurrentProjects.size(); i++) {
			projectsListAdditional.append(allCurrentProjects.get(i).getName() + " with ID: " + allCurrentProjects.get(i).getID() + "\n");
			projectsListAdditional.append("With activities: \n");
			for (int j = 0; j < allCurrentProjects.get(i).getActivities().size(); j++) {
				projectsListAdditional.append("    *" + allCurrentProjects.get(i).getActivities().get(j).getName() + " with time used: " + allCurrentProjects.get(i).getActivities().get(j).gettimeSpent() + " of " + allCurrentProjects.get(i).getActivities().get(j).getTimeEstimate() + "\n");
			}
		}
	}

	public Worker getCurrentWorker() {
		return worker;
	}
}
