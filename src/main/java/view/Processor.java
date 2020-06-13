package view;

import javafx.stage.Stage;

import java.util.ArrayList;

public abstract class Processor {
    protected Stage myStage;
    protected Processor parentProcessor;
    private ArrayList<Stage> subStages = new ArrayList<>();

    public Stage getMyStage() {
        return myStage;
    }

    public ArrayList<Stage> getSubStages() {
        return subStages;
    }

    public void setSubStages(ArrayList<Stage> subStages) {
        this.subStages = subStages;
    }

    public void setMyStage(Stage myStage) {
        this.subStages = new ArrayList<>();
        this.myStage = myStage;
        this.myStage.setOnCloseRequest(event -> {
            for (Stage subStage : this.subStages) {
                subStage.close();
            }
        });
    }

    public void addSubStage(Stage subStage) {
        this.subStages.add(subStage);
        //System.out.println("Khastam");
        System.out.println(subStage);
        subStage.setOnCloseRequest(event -> {
            //System.out.println("Fuck");
            this.removeSubStage(subStage);
        });
    }

    protected void removeSubStage(Stage subStage) {
        this.subStages.removeIf(stage -> {
            //System.out.println("HE HE HE");
            return stage.getTitle().equals(subStage.getTitle());
        });
    }

    protected boolean canOpenSubStage(String title, Processor processor) {
        for (Stage subStage : processor.getSubStages()) {
            if(subStage.getTitle().equals(title)){
                return false;
            }
        }
        return true;
    }


    public Processor getParentProcessor() {
        return parentProcessor;
    }

    public void setParentProcessor(Processor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }
}
