/**
 * Copyright @ 2013 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tessboxeditor.utilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageUtils {

    /**
     * Adds noise to an image. Adapted from an algorithm in
     * http://www.gutgames.com/post/Adding-Noise-to-an-Image-in-C.aspx
     * @param originalImage
     * @param amount
     * @return 
     */
    public static BufferedImage addNoise(BufferedImage originalImage, int amount) {
        BufferedImage targetImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Random randomizer = new Random();
        int n = amount * 2 + 1;

        for (int x = 0; x < targetImage.getWidth(); ++x) {
            for (int y = 0; y < targetImage.getHeight(); ++y) {
                int rgb = originalImage.getRGB(x, y);
                Color color = new Color(rgb);
                // add random integers ranging from -amount to amount
                int r = color.getRed() + randomizer.nextInt(n) - amount;
                int g = color.getGreen() + randomizer.nextInt(n) - amount;
                int b = color.getBlue() + randomizer.nextInt(n) - amount;
                r = r > 255 ? 255 : r;
                r = r < 0 ? 0 : r;
                g = g > 255 ? 255 : g;
                g = g < 0 ? 0 : g;
                b = b > 255 ? 255 : b;
                b = b < 0 ? 0 : b;

                color = new Color(r, g, b);
                targetImage.setRGB(x, y, color.getRGB());
            }
        }

        return targetImage;
    }
}
