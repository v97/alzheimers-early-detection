package com.aed;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Administrator on 21/05/2017.
 */
public class CustomShapeRenderer extends ShapeRenderer {
    public void roundedRect(float x, float y, float width, float height, float radius, Color color){
        setColor(color);

        // rectangles (5 separate instead of 2 overlapping, so that it works with transparency)
        super.rect(x + radius, y + radius, width - 2*radius, height - 2*radius); //center
        super.rect(x, y + radius, radius, height - 2*radius); //left
        super.rect(x + width - radius, y + radius, radius, height - 2*radius); //right
        super.rect(x + radius, y, width - 2*radius, radius); //bottom
        super.rect(x + radius, y + height - radius, width - 2*radius, radius); //top

        // arcs
        super.arc(x + radius, y + radius, radius, 180f, 90f); //bottom left
        super.arc(x + width - radius, y + radius, radius, 270f, 90f); //bottom right
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f); //top right
        super.arc(x + radius, y + height - radius, radius, 90f, 90f); //top left
    }
}