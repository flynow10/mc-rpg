package com.wagologies.spigotplugin.utils;

import org.bukkit.util.Vector;

import java.util.Objects;

public class Quaternion {
    private double x, y, z, w;

    private Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    public Quaternion(Vector v) {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        this.w = 0;
    }

    public Quaternion() {
        this(0.0, 0.0, 0.0, 1.0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;

        return this;
    }

    public Quaternion negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;

        return this;
    }

    public double lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Quaternion normalize() {
        double invLength = 1 / length();
        return this.multiplyInPlace(invLength);
    }

    public Quaternion inverse() {
        double ls = lengthSquared();
        double invNorm = 1 / ls;

        this.x = -this.x * invNorm;
        this.y = -this.y * invNorm;
        this.z = -this.z * invNorm;
        this.w = this.w * invNorm;

        return this;
    }

    public Quaternion add(Quaternion q) {
        this.x += q.x;
        this.y += q.y;
        this.z += q.z;
        this.w += q.w;

        return this;
    }

    public Quaternion subtract(Quaternion q) {
        this.x -= q.x;
        this.y -= q.y;
        this.z -= q.z;
        this.w -= q.w;
        return this;
    }

    public Quaternion multiplyInPlace(Quaternion q) {
        double w = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
        double x = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
        double y = this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x;
        double z = this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w;

        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Quaternion multiplyInPlace(Vector v) {
        return this.multiplyInPlace(new Quaternion(v));
    }

    public Quaternion multiplyInPlace(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;

        return this;
    }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(this).multiplyInPlace(q);
    }

    public Quaternion multiply(Vector v) {
        return this.multiply(new Quaternion(v));
    }

    public Quaternion multiply(double scalar) {
        return new Quaternion(this).multiplyInPlace(scalar);
    }

    public Vector rotate(Vector v) {
        Quaternion rotatedPoint = Quaternion.Multiply(Quaternion.Multiply(this, v), Quaternion.Conjugate(this));
        return new Vector(rotatedPoint.x, rotatedPoint.y, rotatedPoint.z);
    }

    public double dot(Quaternion q) {
        return this.x * q.x + this.y * q.y + this.z * q.z + this.w * q.w;
    }

    public Quaternion slerp(Quaternion q, double t) {
        return slerp(q, t, false);
    }

    public Quaternion slerp(Quaternion q, double t, boolean allowLong) {
        double epsilon = 1e-6;
        double omegaCos = this.dot(q);

        boolean flip = false;
        if(omegaCos < 0 && !allowLong) {
            flip = true;
            omegaCos = -omegaCos;
        }

        double s1, s2;

        if(omegaCos > (1 - epsilon)) {
            // To close to slerp, revert to lerp instead
            s1 = 1 - t;
            s2 = (flip) ? -t : t;
        } else {
            double omega = Math.acos(omegaCos);
            double invSinOmega = 1 / Math.sin(omega);

            s1 = Math.sin((1-t) * omega) * invSinOmega;
            s2 = Math.sin(t * omega) * invSinOmega;
            if(flip) {
                s2 = -s2;
            }
        }

        return Quaternion.Add(this.multiply(s1), q.multiply(s2));
    }

    public static Quaternion Conjugate(Quaternion q) {
        return new Quaternion(q).conjugate();
    }

    public static Quaternion Negate(Quaternion q) {
        return new Quaternion(q).negate();
    }

    public static Quaternion Inverse(Quaternion q) {
        return new Quaternion(q).inverse();
    }

    public static Quaternion Add(Quaternion q1, Quaternion q2) {
        return new Quaternion(q1).add(q2);
    }

    public static Quaternion Subtract(Quaternion q1, Quaternion q2) {
        return new Quaternion(q1).subtract(q2);
    }

    public static Quaternion Multiply(Quaternion q1, Quaternion q2) {
        return new Quaternion(q1).multiplyInPlace(q2);
    }

    public static Quaternion Multiply(Quaternion q, Vector v) {
        return new Quaternion(q).multiplyInPlace(v);
    }

    public static Quaternion Multiply(Quaternion q, double scalar) {
        return new Quaternion(q).multiplyInPlace(scalar);
    }

    public static Quaternion Compose(Quaternion... quaternions) {
        Quaternion q = new Quaternion();
        for (int i = quaternions.length - 1; i >= 0; i--) {
            q.multiplyInPlace(quaternions[i]);
        }
        return q.normalize();
    }


    public static Vector Rotate(Vector v, Quaternion q) {
        return q.rotate(v);
    }

    public static double Dot(Quaternion q1, Quaternion q2) {
        return q1.dot(q2);
    }

    public static Quaternion Slerp(Quaternion q1, Quaternion q2, double t) {
        return q1.slerp(q2, t);
    }

    public static Quaternion Slerp(Quaternion q1, Quaternion q2, double t, boolean allowLong) {
        return q1.slerp(q2, t, allowLong);
    }

    public static Quaternion Identity() {
        return new Quaternion();
    }

    public static Quaternion FromAxisAngle(Vector axis, double angle) {
        Quaternion q = new Quaternion();

        double halfAngle = angle / 2;
        double s = Math.sin(halfAngle);
        double c = Math.cos(halfAngle);

        q.x = axis.getX() * s;
        q.y = axis.getY() * s;
        q.z = axis.getZ() * s;
        q.w = c;
        return q.normalize();
    }

    public static Quaternion FromYawPitchRoll(double yaw, double pitch, double roll) {
        Quaternion q = new Quaternion();

        double halfRoll = roll / 2;
        double sr = Math.sin(halfRoll);
        double cr = Math.cos(halfRoll);

        double halfPitch = pitch / 2;
        double sp = Math.sin(halfPitch);
        double cp = Math.cos(halfPitch);

        double halfYaw = yaw / 2;
        double sy = Math.sin(halfYaw);
        double cy = Math.cos(halfYaw);

        q.x = cy * sp * cr + sy * cp * sr;
        q.y = sy * cp * cr - cy * sp * sr;
        q.z = cy * cp * sr - sy * sp * cr;
        q.w = cy * cp * cr + sy * sp * sr;

        return q;
    }

    public static Quaternion FromToRotation(Vector from, Vector to) {
        Quaternion q = new Quaternion();
        Vector cross = from.getCrossProduct(to);
        q.x = cross.getX();
        q.y = cross.getY();
        q.z = cross.getZ();
        q.w = Math.sqrt(from.lengthSquared() * to.lengthSquared()) + from.dot(to);
        return q.normalize();
    }

    public static Quaternion LookRotation(Vector dir) {
        return Quaternion.LookRotation(dir, new Vector(0, 1, 0));
    }

    public static Quaternion LookRotation(Vector dir, Vector up) {
        if(dir.isZero()) {
            return Quaternion.Identity();
        }

        if(!up.equals(dir)) {
            up.normalize();
            Vector v = dir.clone().add(up.clone().multiply(-up.dot(dir)));
            Quaternion q = Quaternion.FromToRotation(new Vector(0, 0, 1), v);
            return Quaternion.Multiply(Quaternion.FromToRotation(v, dir), q);
        } else {
            return Quaternion.FromToRotation(new Vector(0, 0, 1), dir);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quaternion that = (Quaternion) o;
        return Double.compare(x, that.x) == 0 && Double.compare(y,
                that.y) == 0 && Double.compare(z, that.z) == 0 && Double.compare(w, that.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
