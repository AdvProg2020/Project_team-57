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
        //System.out.println("Fuck");
        this.myStage.setOnCloseRequest(event -> {
            //System.out.println("Hi");
            for (Stage subStage : this.subStages) {
                subStage.close();
            }
            if(parentProcessor != null) {
                //System.out.println("Hello");
                //System.out.println("2." + myStage.getTitle());
                parentProcessor.removeSubStage(myStage);
            }
        });
    }

    public void addSubStage(Stage subStage) {
        //System.out.println("1." + subStage.getTitle());
        this.subStages.add(subStage);
        //System.out.println(this.subStages.size());
        //System.out.println("This: " + this);
        //System.out.println("Khastam");
        //System.out.println(subStage);
        if(parentProcessor == null){
            subStage.setOnCloseRequest(event -> {
                //System.out.println("Fuck");
                this.removeSubStage(subStage);
            });
        }

    }

    protected void removeSubStage(Stage subStage) {
        //System.out.println(this.subStages.size());
        //System.out.println("This: " + this);
/*        for (Stage stage : this.subStages) {
            System.out.println("3." + stage.getTitle());
        }*/
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
