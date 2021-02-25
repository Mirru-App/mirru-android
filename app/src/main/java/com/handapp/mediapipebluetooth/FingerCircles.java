
package com.handapp.mediapipebluetooth;

import java.text.*;
import java.util.ArrayList;
import java.util.List;

import mikera.arrayz.Array;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;

class FingerCircles
{
    static List rotatePoints(Vector3 rotateToNormal, Vector3 point1, Vector3 point2, Vector3 point3) {
        // Rotate points 1, 2, and 3 into the plane defined by normal
        // returning the three rotated points
        point2.sub(point1);
        Vector3 v1 = point2;

        point3.sub(point2);
        Vector3 v2 = point3;

        v1.crossProduct(v2);
        Vector3 unnormalized = v1;

        Vector3 normal = unnormalized.toNormal();

        Vector3 v = normal;
        v.crossProduct(rotateToNormal.toVector());
        double[] vArray = v.toDoubleArray();

        double s = v.normalise();
        double c = normal.dotProduct(rotateToNormal.toVector());

        double[][] skewMatArray = {
                {0, -1*vArray[2], vArray[1]},
                {vArray[2],    0,       -1*vArray[0]},
                {-1*vArray[1], vArray[0],    0}
        };

        Matrix skewMat = Matrix.create(skewMatArray);

        Matrix skewMatMultiplied = Matrix.create(skewMatArray);
        skewMatMultiplied.innerProduct(skewMat);
        skewMatMultiplied.innerProduct((1-c)/(s*s));

        Matrix rotationMatrix = Matrix.create(3,3);
        rotationMatrix.add(IdentityMatrix.create(3));
        rotationMatrix.add(skewMat);
        rotationMatrix.add(skewMatMultiplied);

        Matrix inverseRotationMatrix = rotationMatrix.toMatrixTranspose();

        Vector3 rp1 = Vector3.create(rotationMatrix.innerProduct(point1).toVector());
        Vector3 rp2 = Vector3.create(rotationMatrix.innerProduct(point2).toVector());
        Vector3 rp3 = Vector3.create(rotationMatrix.innerProduct(point3).toVector());

        Vector3[] vectors = new Vector3[] {rp1, rp2, rp3};
        List results = new ArrayList();
        results.add(inverseRotationMatrix);
        results.add(vectors);

        return results;
    }

    // Function to find the circle on
    // which the given three points lie
    // This code is contributed by chandan_jnu
    //https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/
    static float getAngle(Vector2 point1, Vector2 point2, Vector2 point3, boolean isThumb) {
        float x12 = (float) point1.x - (float) point2.x;
        float x13 = (float) point1.x - (float) point3.x;

        float y12 = (float) point1.y - (float) point2.y;
        float y13 = (float) point1.y - (float) point3.y;

        float y31 = (float) point3.y - (float) point1.y;
        float y21 = (float) point2.y - (float) point1.y;

        float x31 = (float) point3.x - (float) point1.x;
        float x21 = (float) point2.x - (float) point1.x;

        // x1^2 - x3^2
        float sx13 = (float) (Math.pow(point1.x, 2) -
                Math.pow(point3.x, 2));

        // y1^2 - y3^2
        float sy13 = (float) (Math.pow(point1.y, 2) -
                Math.pow(point3.y, 2));

        float sx21 = (float) (Math.pow(point2.x, 2) -
                Math.pow(point1.x, 2));

        float sy21 = (float) (Math.pow(point2.y, 2) -
                Math.pow(point1.y, 2));

        float f = ((sx13) * (x12)
                + (sy13) * (x12)
                + (sx21) * (x13)
                + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        float g = ((sx13) * (y12)
                + (sy13) * (y12)
                + (sx21) * (y13)
                + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        float c = -(float) Math.pow((float) point1.x, 2) - (float) Math.pow((float) point1.y, 2) -
                2 * g * (float) point1.x - 2 * f * (float) point1.y;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        float h = -g;
        float k = -f;
        float sqr_of_r = h * h + k * k - c;

        // r is the radius
        float r = (float) Math.sqrt(sqr_of_r);
        DecimalFormat df = new DecimalFormat("#.#####");
//        System.out.println("Centre = (" + h + "," + k + ")");
//        System.out.println("Radius = " + df.format(r));

        //normalize radii
        final float PI = (float) 3.14;
        float shift = (float) 0.020746529414226417;
        float normalizedRadii = (float) (Math.atan(1 * r - shift) / ( PI / 2) * 1);
//        System.out.println("normalizedRadii = " + normalizedRadii);

        float angle;

        if (!isThumb) {
            angle = normalizedRadii * 180;
        } else {
            angle = 180 - (180*normalizedRadii);
        }
        //System.out.println("angle = " + normalizedRadii);
        return angle;
    }
}