package com.hank.hannotation;

public class FieldInfoN {
    String descriptor;
    String name;
    Object value;

    FieldInfoN(String descriptor, String name, Object value) {
        this.descriptor = descriptor;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldInfoN) {
            return ((FieldInfoN) obj).descriptor.equals(this.descriptor) && ((FieldInfoN) obj).name.equals(this.name) && ((FieldInfoN) obj).value.equals(this.value);
        } else {
            return false;
        }
    }
}
