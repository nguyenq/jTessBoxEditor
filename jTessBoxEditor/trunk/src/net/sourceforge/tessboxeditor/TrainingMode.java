/**
 * Copyright @ 2014 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tessboxeditor;

/**
 * Supported training modes.
 */
public enum TrainingMode {

    /**
     * Header text.
     */
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
    public static TrainingMode getValueByDesc(String desc) {
        if (desc != null) {
            for (TrainingMode mode : TrainingMode.values()) {
                if (desc.equals(mode.display)) {
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
