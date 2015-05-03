/**
 * Copyright @ 2012 Quan Nguyen
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
package net.sourceforge.vietocr;

import java.awt.Rectangle;
import java.io.File;
import java.util.List;

public abstract class OCR<T> {

    final static String CONFIGS_FILE = "tess_configs";

    protected Rectangle rect = null;
    private String pageSegMode = "3"; // Fully automatic page segmentation, but no OSD (default)
    private String language = "eng";
    private String outputFormat = "text";
    private String datapath = "./";

    /**
     * Recognizes files or images.
     *
     * @param imageEntities List of files or images
     * @param selection Region of Interest
     * @return
     * @throws Exception
     */
    public String recognizeText(List<T> imageEntities, Rectangle selection) throws Exception {
        rect = selection;
        return recognizeText(imageEntities);
    }

    /**
     * Recognizes files or images.
     *
     * @param imageEntities List of files or images
     * @return
     * @throws Exception
     */
    public abstract String recognizeText(List<T> imageEntities) throws Exception;

    /**
     * Processes image file to output file.
     *
     * @param imageFile image file
     * @param outputFile output file
     * @throws Exception
     */
    public abstract void processPages(File imageFile, File outputFile) throws Exception;

    /**
     * Gets page segmentation mode.
     *
     * @return the PageSegMode
     */
    public String getPageSegMode() {
        return pageSegMode;
    }

    /**
     * Sets page segmentation mode.
     *
     * @param pageSegMode the mode to set
     */
    public void setPageSegMode(String pageSegMode) {
        this.pageSegMode = pageSegMode;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set; possible values: txt, hocr, pdf
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets path to tessdata.
     * 
     * @return the datapath
     */
    public String getDatapath() {
        return datapath;
    }

    /**
     * Sets path to tessdata.
     * 
     * @param datapath the datapath to set
     */
    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }
}
