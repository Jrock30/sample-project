package com.sample.project.common.jpa.intf;

public interface ChangeableToFromEntity <E>{
    public E to();
    public void from(E entity);
}
