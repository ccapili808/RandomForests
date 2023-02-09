import javafx.scene.Node;

public class DecisionNode {
    private Node left;
    private Node right;
    private String question;
    public DecisionNode(String question) {
        this.question = question;
    }
}
