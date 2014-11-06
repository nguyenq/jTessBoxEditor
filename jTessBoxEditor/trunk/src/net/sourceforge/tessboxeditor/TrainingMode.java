package net.sourceforge.tessboxeditor;

/**
 * Supported training modes.
 */
public enum TrainingMode {

    HeaderText("-- Training Mode --"),
    /**
     * Make box only.
     */
    Make_Box_File_Only("Make Box File Only"),
    /**
     * Train with Existing Box.
     */
    Train_with_Existing_Box("Train with Existing Box"),
    /**
     * Starts from Shape Clustering step.
     */
    Shape_Clustering("    Shape Clustering..."),
    /**
     * Starts from Dictionary step.
     */
    Dictionary("    Dictionary..."),
    /**
     * Train from scratch, complete training process.
     */
    Train_from_Scratch("Train from Scratch");

    private final String display;

    private TrainingMode(String str) {
        this.display = str;
    }

    /**
     * Gets enum from description.
     *
     * @param desc
     * @return
     */
    public static TrainingMode getMode(String desc) {
        if (desc != null) {
            for (TrainingMode mode : TrainingMode.values()) {
                if (desc.equalsIgnoreCase(mode.display)) {
                    return mode;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return display;
    }
}
