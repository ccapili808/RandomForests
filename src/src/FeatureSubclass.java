public class FeatureSubclass {
    private String parentFeature;
    private String value;
    private int total;
    private String classification;
    public FeatureSubclass(String parentFeature, String value, int total, String classification) {
        this.parentFeature = parentFeature;
        this.value = value;
        this.total = total;
        this.classification = classification;
    }
}
