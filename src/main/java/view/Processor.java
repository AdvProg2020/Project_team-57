package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
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

    //Stage Managing Section
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
    //Stage Managing Section

    //TextField Special Setting Section
    protected String removeDots(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        boolean foundDot = false;
        int textSize = text.length();

        for (int i = 0; i < textSize; i++) {
            if(text.charAt(i) < 48 || text.charAt(i) > 57) {
                if(text.charAt(i) == '.') {
                    if(foundDot) {
                        stringBuilder.deleteCharAt(i);
                        textSize--;
                    }
                    foundDot = true;
                } else {
                    stringBuilder.deleteCharAt(i);
                    textSize--;
                }
            }
        }

        return stringBuilder.toString();
    }

    protected void setDoubleFields(TextField textField, double maxValue) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                if(newValue.equals(".")) {
                    textField.setText("0.");
                } else if (!newValue.matches("\\d+(.(\\d)+)?")) {
                    if(textField.getText().contains(".")) {
                        textField.setText(removeDots(textField.getText()));
                    } else {
                        textField.setText(newValue.replaceAll("[^\\d\\.]", ""));
                    }
                } else if(newValue.matches("\\d+(.(\\d)+)?") && Double.parseDouble(newValue) >= maxValue) {
                    //Todo checking
                    textField.setText(oldValue);
                }

            }
        });
    }

    protected void setIntegerFields(TextField textField, Integer maxValue) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                //Todo Checking

                int newValueLength = newValue.length(), maxValueLength = Integer.toString(maxValue).length();

                if (!newValue.matches("\\d+")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                } else if(newValue.matches("\\d+") && (newValueLength > maxValueLength ||
                        (newValueLength == maxValueLength && newValue.compareTo(Integer.toString(maxValue)) >= 0))) {
                    textField.setText(oldValue);
                }
            }
        });
    }
    //TextField Special Setting Section

    public Processor getParentProcessor() {
        return parentProcessor;
    }

    public void setParentProcessor(Processor parentProcessor) {
        this.parentProcessor = parentProcessor;
    }
}
