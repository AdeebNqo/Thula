/*
 * Copyright (C) 2014 Easy Target
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easytarget.micopi.engine;

import com.easytarget.micopi.Contact;

/**
 * Created by michel on 12/11/14.
 *
 * Image generator
 */
public class CircleMatrixGenerator {

    /**
     * Fills the Canvas in the Painter with circles in a grid
     *
     * @param painter Paint the generated shape in this object
     * @param contact Data from this Contact object will be used to generate the shapes and colors
     */
    public static void generate(Painter painter, Contact contact) {
        if (painter == null || contact == null) return;

        // Prepare painting values based on image size and contact data.
        final String md5String = contact.getMD5EncryptedString();

        // Use the length of the first name to determine the number of shapes per row and column.
        final int firstNameLength = contact.getNameWord(0).length();
//        int shapesPerRow = (md5String.charAt(20) % 2 == 0) ? firstNameLength : firstNameLength / 2;
//        if (shapesPerRow < 3) shapesPerRow = contact.getNumberOfLetters();
//        if (shapesPerRow < 3) shapesPerRow += 2;

        final int shapesPerRow;
        if (firstNameLength < 3) {
            shapesPerRow = 3;
        } else if (firstNameLength > 8) {
            shapesPerRow = 8;
        } else {
            shapesPerRow = firstNameLength;
        }

        final int imageSize = painter.getImageSize();
        final float strokeWidth = md5String.charAt(12) * 3f;
        float circleDistance;
//        circleDistance = (imageSize / shapesPerRow) + (imageSize / (shapesPerRow * 2f));
        circleDistance = (imageSize / shapesPerRow);

        int md5Pos = 0;
//        int color = ColorCollection.getColor(contact.getFullName().charAt(0));
        for (int y = 0; y < shapesPerRow; y++) {
            for (int x = 0; x < shapesPerRow; x++) {

                md5Pos++;
                if (md5Pos >= md5String.length()) md5Pos = 0;
                final char md5Char = md5String.charAt(md5Pos);

//                if (doGenerateColor)
                final int color = ColorCollection.getColor(md5Char);

                final int index = y * shapesPerRow + x;
                final float radius;
                if ((index & 1) == 0) radius = md5Char * 6f;
                else radius = md5Char * 5f;

                painter.paintShape(
                        Painter.MODE_CIRCLE_FILLED,
                        color,
                        255,
//                        255 - md5String.charAt(md5Pos),
                        strokeWidth,        // Stroke width
                        0,
                        x * circleDistance,
                        y * circleDistance,
                        radius
                );
                circleDistance *= 1.1f;
            }
        }
    }

}
