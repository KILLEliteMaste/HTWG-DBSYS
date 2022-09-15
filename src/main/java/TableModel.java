import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableModel {
    private SimpleStringProperty name;
    private SimpleIntegerProperty sterne;

    public TableModel(String name, Integer sterne) {
        this.name = new SimpleStringProperty(name);
        this.sterne = new SimpleIntegerProperty(sterne);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Integer getSterne() {
        return sterne.get();
    }

    public SimpleIntegerProperty sterneProperty() {
        return sterne;
    }

    public void setSterne(Integer sterne) {
        this.sterne.set(sterne);
    }

    @Override
    public String toString() {
        return "TableModel{" +
                "name=" + name +
                ", sterne=" + sterne +
                '}';
    }
}
